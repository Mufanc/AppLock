package mufanc.tools.applock.util

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.signature.ObjectKey
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

class AppIconLoader(private val pm: PackageManager) : ModelLoader<ApplicationInfo, InputStream> {
    override fun buildLoadData(
        model: ApplicationInfo,
        width: Int, height: Int,
        options: Options
    ) = ModelLoader.LoadData(
        ObjectKey(model.packageName),
        object : DataFetcher<InputStream> {
            override fun loadData(
                priority: Priority,
                callback: DataFetcher.DataCallback<in InputStream>
            ) {
                val icon = pm.getApplicationIcon(model).toBitmap()
                val buffer = ByteArrayOutputStream().also {
                    icon.compress(Bitmap.CompressFormat.PNG, 100, it)
                }.toByteArray()
                callback.onDataReady(ByteArrayInputStream(buffer))
            }

            override fun cleanup() = Unit

            override fun cancel() = Unit

            override fun getDataClass() = InputStream::class.java

            override fun getDataSource(): DataSource = DataSource.REMOTE
        }
    )

    override fun handles(model: ApplicationInfo) = true
}
