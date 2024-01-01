plugins {
    alias(libs.plugins.agp.app)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
}

val androidMinSdkVersion: Int by rootProject.extra
val androidTargetSdkVersion: Int by rootProject.extra
val androidCompileSdkVersion: Int by rootProject.extra

val androidSourceCompatibility: JavaVersion by rootProject.extra
val androidTargetCompatibility: JavaVersion by rootProject.extra
val androidKotlinJvmTarget: String by rootProject.extra

val versionNamePrefix = "3.0.0"

fun String.execute(): String {
    return Runtime.getRuntime()
        .exec(this.split("\\s+".toRegex()).toTypedArray())
        .apply { waitFor() }
        .inputStream.bufferedReader().readText().trim()
}

fun getVersionCode(): Int {
    return "git rev-list --count HEAD".execute().toInt()
}

fun getVersionName(): String {
    val hash = "git rev-parse --short HEAD".execute()
    val count = "git rev-list --count HEAD".execute()

    val versionName = "$versionNamePrefix.r$count.$hash"

    return versionName
}

fun getCommitHash(): String {
    return "git rev-parse HEAD".execute()
}

android {
    namespace = "xyz.mufanc.applock"
    compileSdk = androidCompileSdkVersion

    defaultConfig {
        applicationId = "xyz.mufanc.applock"
        minSdk = androidMinSdkVersion
        targetSdk = androidTargetSdkVersion
        versionCode = getVersionCode()
        versionName = getVersionName()
        buildConfigField("String", "COMMIT_HASH", "\"${getCommitHash()}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles("proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = androidSourceCompatibility
        targetCompatibility = androidTargetCompatibility
    }

    kotlinOptions {
        jvmTarget = androidKotlinJvmTarget
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // Api Stub
    compileOnly(project(":api-stub"))

    // Xposed
    compileOnly(libs.xposed.api)
    implementation(libs.xposed.service)

    // AutoX
    ksp(libs.autox.ksp)
    implementation(libs.autox.annotation)

    // Reflector
    implementation(kotlin("reflect"))
    implementation(libs.reflector)

    // App
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
}
