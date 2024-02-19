import dev.rikka.tools.materialthemebuilder.MaterialThemeBuilderExtension
import org.bouncycastle.util.encoders.Base64
import java.util.Properties

plugins {
    alias(libs.plugins.agp.app)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.material.color)
    alias(libs.plugins.flexi.locale)
    id("kotlin-parcelize")
    id("kotlin-kapt")
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

    return "$versionNamePrefix.r$count.$hash"
}

fun getCommitHash(): String {
    return "git rev-parse HEAD".execute()
}

fun decodeBase64(data: String): String {
    return Base64.decode(data).decodeToString().trim()
}

android {
    namespace = "xyz.mufanc.applock"
    compileSdk = androidCompileSdkVersion

    signingConfigs {
        create("release") {
            val props = Properties().apply {
                load(rootProject.file("local.properties").inputStream())
            }

            storeFile = file(props["keystore.store.file"] as String)
            storePassword = decodeBase64(props["keystore.store.password"] as String)
            keyAlias = props["keystore.key.alias"] as String
            keyPassword = decodeBase64(props["keystore.key.password"] as String)
        }
    }

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
            isMinifyEnabled = true
            proguardFiles("proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
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
        viewBinding = true
        dataBinding = true
    }
}

afterEvaluate {
    android.applicationVariants.forEach { variant ->
        variant.assembleProvider.get().doLast {
            for (output in variant.outputs) {
                val outputFile = output.outputFile

                if (outputFile.relativeTo(projectDir).startsWith("build/intermediates")) {
                    continue
                }

                val targetName = "AppLock-v${variant.versionName}-${variant.name}.apk"
                val targetFile = File(outputFile.parentFile, targetName)

                outputFile.renameTo(targetFile)
            }
        }
    }
}

materialThemeBuilder {
    packageName = android.namespace

    fun applyThemeConfigs(theme: MaterialThemeBuilderExtension.Theme) = theme.run {
        lightThemeFormat = "Theme.AppLock.Light.%s"
//        lightThemeParent = "Theme.Material3.Light.NoActionBar"
        darkThemeFormat = "Theme.AppLock.Dark.%s"
//        darkThemeParent = "Theme.Material3.Dark.NoActionBar"
    }

    themes {
        for ((name, color) in listOf(
            "Anemo" to "#75C3A9",
            "Cryo" to "#A0D7E4",
            "Dendro" to "#A6C938",
            "Electro" to "#B08FC2",
            "Geo" to "#FAB72E",
            "Hydro" to "#4BC3F1",
            "Pyro" to "#EF7A35"
        )) {
            create("NatureElement.$name") {
                primaryColor = color
                applyThemeConfigs(this)
            }
        }

        create("NatureElement.Dynamic") {
            isDynamicColors = true
            applyThemeConfigs(this)
        }
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

    // Reflect
    implementation(kotlin("reflect"))
    implementation(libs.joor)

    // Glide
    kapt(libs.glide.compiler)
    implementation(libs.glide)

    // App
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.preference.ktx)
}
