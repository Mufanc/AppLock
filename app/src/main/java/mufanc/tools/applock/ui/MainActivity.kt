package mufanc.tools.applock.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import mufanc.easyhook.api.Logger
import mufanc.tools.applock.App
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.R
import mufanc.tools.applock.databinding.ActivityMainBinding
import mufanc.tools.applock.ui.adapter.ThemeColorAdapter
import mufanc.tools.applock.util.Settings
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setTheme(ThemeColorAdapter.getColorThemeStyle())
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.apply {
            setContentView(root)
            setSupportActionBar(toolbar)

            val navController = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment_activity_main)
                .let { (it as NavHostFragment).navController }

            appBarConfiguration = AppBarConfiguration.Builder(
                R.id.navigation_dashboard,
                R.id.navigation_scope,
                R.id.navigation_settings
            ).build()

            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }

        if (BuildConfig.DEBUG) {  // 用于生成屏幕截图
            registerReceiver(
                object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        Logger.i(intent.toUri(0))
                        when (intent.getStringExtra("command")!!) {
                            "theme" -> {
                                Settings.prefs.edit().apply {
                                    putString(
                                        ThemeColorAdapter.ThemeColor::class.java.simpleName,
                                        intent.getStringExtra("color")!!
                                    )
                                }.apply()
                            }
                            "navigate" -> {
                                findNavController(R.id.nav_host_fragment_activity_main)
                                    .navigate(
                                        when (intent.getStringExtra("page")!!) {
                                            "0" -> R.id.navigation_dashboard
                                            "1" -> R.id.navigation_scope
                                            "2" -> R.id.navigation_settings
                                            else -> error("")
                                        }
                                    )
                            }
                        }
                    }
                },
                IntentFilter("${BuildConfig.APPLICATION_ID}.ACTION_CONTROL")
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment_activity_main)
            .navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (App.isModuleActivated.not()) {
            Thread.currentThread().stackTrace.forEach {
                if (it.methodName == "handleRelaunchActivity") return
            }
            exitProcess(0)
        }
    }
}
