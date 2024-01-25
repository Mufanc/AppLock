package xyz.mufanc.applock.ui

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import xyz.mufanc.applock.R
import xyz.mufanc.applock.databinding.ActivityMainBinding
import xyz.mufanc.applock.ui.base.BaseActivity
import xyz.mufanc.applock.ui.util.ThemeManager

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(cache: Bundle?) {
        setTheme(ThemeManager.getColorThemeStyle())

        super.onCreate(cache)

        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.run {
            setContentView(root)
            setSupportActionBar(toolbar)

            val navController = supportFragmentManager
                .findFragmentById(R.id.nav_activity_main)
                .let { (it as NavHostFragment).navController }

            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_home,
                    R.id.nav_apps,
                    R.id.nav_settings
                )
            )

            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }
    }
}
