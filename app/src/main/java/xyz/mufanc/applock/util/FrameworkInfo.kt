package xyz.mufanc.applock.util

import io.github.libxposed.service.XposedService

data class FrameworkInfo(
    val name: String,
    val version: String,
    val versionCode: Long,
    val apiVersion: Int
) {
    constructor(service: XposedService): this(
        service.frameworkName,
        service.frameworkVersion,
        service.frameworkVersionCode,
        service.apiVersion
    )
}
