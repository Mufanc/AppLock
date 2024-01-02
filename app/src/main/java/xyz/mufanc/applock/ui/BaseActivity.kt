package xyz.mufanc.applock.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.BaseContextWrappingDelegate

abstract class BaseActivity : AppCompatActivity() {
    override fun getDelegate(): AppCompatDelegate {
        return BaseContextWrappingDelegate(super.getDelegate())
    }
}
