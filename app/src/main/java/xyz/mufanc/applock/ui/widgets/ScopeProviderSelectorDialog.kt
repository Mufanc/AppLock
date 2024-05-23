package xyz.mufanc.applock.ui.widgets

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.asLiveData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import xyz.mufanc.applock.R
import xyz.mufanc.applock.core.AppLockService
import xyz.mufanc.applock.core.util.Log
import xyz.mufanc.applock.util.I18n
import xyz.mufanc.applock.util.RemotePrefs

class ScopeProviderSelectorDialog private constructor(
    private val dialog: AlertDialog
) : DialogInterface by dialog {

    companion object {
        private const val TAG = "ScopeProviderSelectorDialog"

        private val disabledProviders = RemotePrefs.disabledProviders.asLiveData()

        operator fun invoke(activity: Activity): ScopeProviderSelectorDialog? {
            val service = AppLockService.client() ?: return null
            val availableProviders = service.availableProviders.toSet()

            Log.d(TAG, "available providers: $availableProviders")
            Log.d(TAG, "disabled providers: ${disabledProviders.value?.all?.keys}")

            val registeredProviders = activity.resources.getStringArray(R.array.scope_providers)
            val registeredProviderNames = activity.resources.getStringArray(R.array.scope_provider_names)

            val visibleProviders = registeredProviders
                .filter { id -> availableProviders.contains(id) }

            val visibleProviderNames = registeredProviders.indices
                .filter { i -> registeredProviders[i] in availableProviders }
                .map { i -> registeredProviderNames[i] }

            val dialog = MaterialAlertDialogBuilder(activity)
                .setTitle(I18n.strings.settingsItemScopeProviders)
                .setPositiveButton(R.string.dialog_scope_provider_btn_dismiss) { _, _ ->
                    Log.d(TAG, "dismiss")
                }
                .setAdapter(ProviderAdapter(activity, visibleProviders, visibleProviderNames)) { _, index ->
                    Log.d(TAG, "$index")
                }
                .create()


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                activity.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
                    override fun onActivityDestroyed(activity: Activity) {
                        dialog.dismiss()
                    }

                    override fun onActivityCreated(activity: Activity, bundle: Bundle?) = Unit
                    override fun onActivityStarted(activity: Activity) = Unit
                    override fun onActivityResumed(activity: Activity) = Unit
                    override fun onActivityPaused(activity: Activity) = Unit
                    override fun onActivityStopped(activity: Activity) = Unit
                    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) = Unit
                })
            } else {
                // Todo:
            }

            return ScopeProviderSelectorDialog(dialog)
        }
    }

    fun show() {
        dialog.show()
    }

    private class ProviderAdapter(
        context: Context,
        private val providerIds: List<String>,
        private val providerNames: List<String>,
    ) : ArrayAdapter<String>(
        context,
        R.layout.item_dialog_scope_provider_list,
        R.id.scope_provider_name,
        providerNames
    ) {
        override fun getView(position: Int, view: View?, parent: ViewGroup): View {
            val itemView = super.getView(position, view, parent)

            if (position == 0) {
                itemView.isEnabled = false
            }

            val prefs = disabledProviders.value ?: return itemView

            itemView as MultiChoiceItem
            itemView.isChecked = !prefs.contains(providerIds[position])
            itemView.setOnCheckedChangeListener { _, checked ->
                val editor = prefs.edit()

                if (checked) {
                    editor.remove(providerIds[position])
                } else {
                    editor.putBoolean(providerIds[position], true)
                }

                editor.apply()

                Log.i(TAG, "update disabled providers: ${providerIds[position]}=${if (checked) "enabled" else "disabled"} (${providerNames[position]})")
            }

            return itemView
        }
    }
}