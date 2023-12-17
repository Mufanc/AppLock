package xyz.mufanc.applock.core.process.model

import android.app.ActivityManager
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Modifier

@Parcelize
data class ProcessInfo(
    val killedByAm: Boolean,
    val pid: Int,
    val uid: Int,
    val gids: List<Int>,
    val isolated: Boolean,
    val name: String,
    val packageList: List<String>,
    val processType: Int,  // ActivityManager.PROCESS_STATE_*
) : Parcelable {

    companion object {
        val processTypeNames by lazy {
            ActivityManager::class.java
                .declaredFields
                .mapNotNull { field ->
                    if (field.modifiers and Modifier.STATIC == 0) return@mapNotNull null
                    if (!field.name.startsWith("PROCESS_STATE_")) return@mapNotNull null
                    return@mapNotNull Pair(field.get(null), field.name)
                }
                .toMap()
        }
    }

    override fun toString(): String {
        val indent = " ".repeat(4)

        return StringBuilder()
            .append(javaClass.simpleName).append("(\n")
            .append(indent).append("killedByAm=").append(killedByAm).append("\n")
            .append(indent).append("pid=").append(pid).append("\n")
            .append(indent).append("uid=").append(uid).append("\n")
            .append(indent).append("gids=").append(gids).append("\n")
            .append(indent).append("isolated=").append(isolated).append("\n")
            .append(indent).append("processName=").append(name).append("\n")
            .append(indent).append("processType=").append(processTypeNames[processType]).append("\n")
            .append(")")
            .toString()
    }
}
