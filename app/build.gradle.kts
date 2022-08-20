import java.text.SimpleDateFormat
import java.util.*

val versionNamePrefix = "2.1.1"

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp") version "1.7.10-1.0.6"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    compileOnly(project(":hidden-api-stub"))
//    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.10")

    compileOnly("de.robv.android.xposed:api:82")
    compileOnly("de.robv.android.xposed:api:82:sources")
    implementation(project(":easyhook:api"))
    ksp(project(":easyhook:ksp-xposed"))

    implementation("dev.rikka.shizuku:api:12.1.0")
    implementation("dev.rikka.shizuku:provider:12.1.0")
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")

    implementation("androidx.room:room-runtime:2.4.3")
    annotationProcessor("androidx.room:room-compiler:2.4.3")
    kapt("androidx.room:room-compiler:2.4.3")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.1")
    implementation("androidx.preference:preference:1.2.0")
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.5.0")
    implementation("com.google.android.material:material:1.6.1")
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
