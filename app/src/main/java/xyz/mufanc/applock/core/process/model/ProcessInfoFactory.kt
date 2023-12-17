package xyz.mufanc.applock.core.process.model

import android.util.ArrayMap
import xyz.mufanc.applock.core.util.ApiAdapter
import xyz.mufanc.applock.core.util.Ref

object ProcessInfoFactory : ApiAdapter<Any, ProcessInfo>() {
    
    fun create(record: Any): ProcessInfo {
        return try {
            adapt(record)
        } catch (err: Throwable) {
            ProcessInfo.INVALID
        }
    }

    override fun doAndroidP(from: Any): ProcessInfo {
        val ref = Ref(from)
        return ProcessInfo(
            killedByAm = ref["killedByAm"].obtain()!!,
            pid = ref["pid"].obtain()!!,
            uid = ref["uid"].obtain()!!,
            gids = (ref["gids"].obtain<IntArray>())?.toList(),
            isolated = ref["isolated"].obtain()!!,
            name = ref["processName"].obtain(),
            packageList = (ref["pkgList"].obtain<ArrayMap<String, *>>())?.keys?.toList(),
            processType = ref["curProcState"].obtain()!!
        )
    }

    override fun doAndroidQ(from: Any): ProcessInfo {
        TODO("Not yet implemented")
    }

    override fun doAndroidR(from: Any): ProcessInfo {
        TODO("Not yet implemented")
    }

    override fun doAndroidS(from: Any): ProcessInfo {
        val ref = Ref(from)
        return ProcessInfo(
            killedByAm = ref["mKilledByAm"].obtain()!!,
            pid = ref["mPid"].obtain()!!,
            uid = ref["uid"].obtain()!!,
            gids = (ref["mGids"].obtain<IntArray>())?.toList(),
            isolated = ref["isolated"].obtain()!!,
            name = ref["processName"].obtain(),
            packageList = (ref["mPkgList"]["mPkgList"].obtain<ArrayMap<String, *>>())?.keys?.toList(),
            processType = ref["mState"]["mCurProcState"].obtain()!!
        )
    }

    override fun doAndroidT(from: Any): ProcessInfo {
        val ref = Ref(from)
        return ProcessInfo(
            killedByAm = ref["mKilledByAm"].obtain()!!,
            pid = ref["mPid"].obtain()!!,
            uid = ref["uid"].obtain()!!,
            gids = (ref["mGids"].obtain<IntArray>())?.toList(),
            isolated = ref["isolated"].obtain()!!,
            name = ref["processName"].obtain(),
            packageList = (ref["mPkgList"]["mPkgList"].obtain<ArrayMap<String, *>>())?.keys?.toList(),
            processType = ref["mState"]["mCurProcState"].obtain()!!
        )
    }

    override fun doAndroidU(from: Any): ProcessInfo {
        TODO("Not yet implemented")
    }
}
