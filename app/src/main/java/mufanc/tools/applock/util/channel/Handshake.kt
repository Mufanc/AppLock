package mufanc.tools.applock.util.channel

import android.os.Bundle
import androidx.annotation.Keep

@Keep
class Handshake(bundle: Bundle) : Pack(bundle) {
    val pid: Int by bundle
    val uid: Int by bundle
    val version: Int by bundle

    constructor(pid: Int, uid: Int, version: Int) : this(Bundle()) {
        bundle.putInt(::pid.name, pid)
        bundle.putInt(::uid.name, uid)
        bundle.putInt(::version.name, version)
    }
}
