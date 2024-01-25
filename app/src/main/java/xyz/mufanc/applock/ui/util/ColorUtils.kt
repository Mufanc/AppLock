// modified from: https://cs.android.com/android/platform/superproject/+/android14-release:packages/apps/ThemePicker/src/com/android/customization/model/color/ColorUtils.kt

/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.mufanc.applock.ui.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.SystemProperties
import android.util.Log
import androidx.annotation.ColorInt

/** Utility to wrap Monet's color extraction */
object ColorUtils {
    private const val TAG = "ColorUtils"
    private const val MONET_FLAG = "flag_monet"
    private var sSysuiRes: Resources? = null
    private var sFlagId = 0

    /** Returns true if color extraction is enabled in systemui. */
    @SuppressLint("DiscouragedApi")
    @JvmStatic
    fun isMonetEnabled(context: Context): Boolean {
        var monetEnabled = SystemProperties.getBoolean("persist.systemui.flag_monet", false)
        if (!monetEnabled) {
            if (sSysuiRes == null) {
                try {
                    val pm = context.packageManager
                    val sysUIInfo =
                        pm.getApplicationInfo(
                            "com.android.systemui",
                            PackageManager.GET_META_DATA or PackageManager.MATCH_SYSTEM_ONLY
                        )
                    sSysuiRes = pm.getResourcesForApplication(sysUIInfo)
                } catch (e: PackageManager.NameNotFoundException) {
                    Log.w(TAG, "Couldn't read color flag, skipping section", e)
                }
            }
            if (sFlagId == 0) {
                sFlagId =
                    if (sSysuiRes == null) 0
                    else sSysuiRes!!.getIdentifier(MONET_FLAG, "bool", "com.android.systemui")
            }
            if (sFlagId > 0) {
                monetEnabled = sSysuiRes!!.getBoolean(sFlagId)
            }
        }
        return monetEnabled
    }

    @JvmStatic
    fun toColorString(@ColorInt color: Int): String {
        return String.format("%06X", 0xFFFFFF and color)
    }
}
