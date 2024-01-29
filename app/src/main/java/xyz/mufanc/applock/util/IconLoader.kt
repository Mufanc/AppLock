package xyz.mufanc.applock.util

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmapOrNull
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

class IconLoader(
    private val pm: PackageManager
) : ModelLoader<ApplicationInfo, ByteBuffer> {

    // always handle ApplicationInfo
    override fun handles(model: ApplicationInfo): Boolean = true

    override fun buildLoadData(
        model: ApplicationInfo,
        w: Int,
        h: Int,
        options: Options
    ): ModelLoader.LoadData<ByteBuffer> {
        return ModelLoader.LoadData(ObjectKey(model.packageName), IconFetcher(pm, model))
    }

    private class IconFetcher(
        private val pm: PackageManager,
        private val info: ApplicationInfo
    ) : DataFetcher<ByteBuffer> {

        override fun getDataClass() = ByteBuffer::class.java

        override fun getDataSource() = DataSource.LOCAL

        override fun cancel() = Unit

        override fun cleanup() = Unit

        @OptIn(DelicateCoroutinesApi::class)
        override fun loadData(
            priority: Priority,
            callback: DataFetcher.DataCallback<in ByteBuffer>
        ) {
            GlobalScope.launch(Dispatchers.IO) {
                val icon = pm.getApplicationIcon(info.packageName).toBitmapOrNull()

                if (icon != null) {
                    val baos = ByteArrayOutputStream()

                    icon.compress(Bitmap.CompressFormat.PNG, 90, baos)

                    callback.onDataReady(ByteBuffer.wrap(baos.toByteArray()))
                } else {
                    callback.onLoadFailed(NullPointerException("The icon of ${info.packageName} is not a bitmap!"))
                }
            }
        }
    }

    class Factory(
        private val pm: PackageManager
    ) : ModelLoaderFactory<ApplicationInfo, ByteBuffer> {

        override fun teardown() = Unit

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<ApplicationInfo, ByteBuffer> {
            return IconLoader(pm)
        }
    }
}
