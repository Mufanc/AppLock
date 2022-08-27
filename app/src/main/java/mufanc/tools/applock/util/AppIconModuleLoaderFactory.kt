package mufanc.tools.applock.util

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import java.io.InputStream

class AppIconModuleLoaderFactory(
    private val packageManager: PackageManager
) : ModelLoaderFactory<ApplicationInfo, InputStream> {
    override fun build(factory: MultiModelLoaderFactory): ModelLoader<ApplicationInfo, InputStream> {
        return AppIconLoader(packageManager)
    }

    override fun teardown() = Unit
}