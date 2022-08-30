package mufanc.tools.applock.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import mufanc.tools.applock.App
import mufanc.tools.applock.R
import mufanc.tools.applock.databinding.ActivityMainBinding
import mufanc.tools.applock.ui.adapter.ThemeColorAdapter
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setTheme(ThemeColorAdapter.getColorThemeStyle(this))
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