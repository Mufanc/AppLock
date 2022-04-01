import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat

val versionNamePrefix = "2.0.1"

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "mufanc.tools.applock"
        minSdk = 28
        targetSdk = 32
        versionCode = getVersionCode()
        versionName = getVersionName()
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
}

fun getVersionCode(): Int {
    val versionCode = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()).toInt()
    println("version code: $versionCode")
    return versionCode
}

fun getVersionName(): String {
    fun String.execute(): String {
        return Runtime.getRuntime()
            .exec(this.split(" ").toTypedArray())
            .apply { waitFor() }
            .inputStream.bufferedReader().readText().trim()
    }

    val hash = "git rev-parse --short HEAD".execute()
    val count = "git rev-list --count HEAD".execute()
    val versionName = "$versionNamePrefix.r$count.$hash"
    println("version name: $versionName")
    return versionName
}
