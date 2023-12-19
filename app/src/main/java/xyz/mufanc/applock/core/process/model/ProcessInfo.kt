package xyz.mufanc.applock.core.process.model

import android.app.ActivityManager
import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Modifier

@Parcelize
@Keep
data class ProcessInfo(
    val killedByAm: Boolean,
    val pid: Int,
    val uid: Int,
    val gids: List<Int>?,
    val isolated: Boolean,
    val name: String?,
    val packageList: List<String>?,
    val state: Int,  // ActivityManager.PROCESS_STATE_*
    val isValid: Boolean = true
) : Parcelable {

    companion object {
        val processStateNames by lazy {
            ActivityManager::class.java
                .declaredFields
                .mapNotNull { field ->
                    if (field.modifiers and Modifier.STATIC == 0) return@mapNotNull null
                    if (!field.name.startsWith("PROCESS_STATE_")) return@mapNotNull null
                    return@mapNotNull Pair(field.get(null), field.name)
                }
                .toMap()
        }

        val INVALID = ProcessInfo(false, 0, 0, emptyList(), false, "", emptyList(), 0, false)
    }

    override fun toString(): String {
        val indent = " ".repeat(4)
        val builder = StringBuilder()

        builder.append(javaClass.simpleName).append("(\n")

        if (isValid) {
            builder
                .append(indent).append("killedByAm=").append(killedByAm).append("\n")
                .append(indent).append("pid=").append(pid).append("\n")
                .append(indent).append("uid=").append(uid).append("\n")
                .append(indent).append("gids=").append(gids).append("\n")
                .append(indent).append("isolated=").append(isolated).append("\n")
                .append(indent).append("name=").append(name).append("\n")
                .append(indent).append("pkgList=").append(packageList).append("\n")
                .append(indent).append("state=").append(processStateNames[state]).append("\n")
        } else {
            builder.append(indent).append("[ failed to dump process info ]\n")
        }

        builder.append(")")

        return builder.toString()
    }
}
