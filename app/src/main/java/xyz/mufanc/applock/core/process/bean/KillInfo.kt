package xyz.mufanc.applock.core.process.bean

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class KillInfo(
    val reason: String,
    val reasonCode: Int,
    val subReason: Int,
    val processInfo: ProcessInfo
) : Parcelable {
    override fun toString(): String {
        val indent = " ".repeat(4)

        return StringBuilder()
            .append(javaClass.simpleName).append("(\n")
            .append(indent).append("reason=").append(reason).append("\n")
            .append(indent).append("reasonCode=").append(reasonCode).append("\n")
            .append(indent).append("subReason=").append(subReason).append("\n")
            .append(indent)
            .append("processInfo=")
            .append(processInfo.toString().prependIndent(indent).trim())
            .append("\n")
            .append(")")
            .toString()
    }
}
