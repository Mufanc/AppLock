package xyz.mufanc.applock.util

import android.content.Context
import android.content.pm.ApplicationInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import java.nio.ByteBuffer

@GlideModule
class IconLoaderGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.prepend(
            ApplicationInfo::class.java, ByteBuffer::class.java,
            IconLoader.Factory(context.packageManager)
        )
    }
}
