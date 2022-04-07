package mufanc.tools.applock.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.R
import mufanc.tools.applock.databinding.FragmentHomeBinding
import mufanc.tools.applock.xposed.AppLockHelper

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val model = ViewModelProvider(this)[HomeViewModel::class.java]
        binding?.status = model
        binding?.apply {
            val mode = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getString(
                    "work_mode",
                    requireContext().resources.getStringArray(R.array.resolve_mode_values)[0]
                )
            when (mode) {
                "xposed" -> {
                    shizukuStatus.visibility = View.GONE
                }
                "shizuku" -> {
                    moduleActivated.visibility = View.GONE
                    hookerStatus.visibility = View.GONE
                }
            }
        }
        if (model.requireReboot && !BuildConfig.DEBUG) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.require_reboot)
                .setMessage(R.string.core_version_not_match)
                .setPositiveButton(R.string.reboot_now) { _, _ ->
                    AppLockHelper.client?.reboot() ?: requireActivity().finish()
                }
                .setNegativeButton(R.string.reboot_later) { dialog, _ ->
                    dialog.dismiss()
                    requireActivity().finish()
                }
                .setCancelable(false)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}