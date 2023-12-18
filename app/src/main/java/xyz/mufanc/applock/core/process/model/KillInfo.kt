package xyz.mufanc.applock.core.process.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class KillInfo(
    val reason: String?,
    val description: String?,
    val reasonCode: Int,
    val subReason: Int,
    val processInfo: ProcessInfo?,
    val isValid: Boolean = true
) : Parcelable {

    companion object {
        val INVALID = KillInfo("", "", 0, 0, ProcessInfo.INVALID, false)
    }

    override fun toString(): String {
        val indent = " ".repeat(4)
        val builder = StringBuilder()

        builder.append(javaClass.simpleName).append("(\n")

        if (isValid) {
            builder
                .append(indent).append("reason=").append(reason).append("\n")
                .append(indent).append("reasonCode=").append(reasonCode).append("\n")
                .append(indent).append("subReason=").append(subReason).append("\n")
                .append(indent)
                .append("processInfo=")
                .append(processInfo.toString().prependIndent(indent).trim())
                .append("\n")
        } else {
            builder.append(indent).append("[ failed to dump kill info ]\n")
        }

        builder.append(")")

        return builder.toString()
    }
}
