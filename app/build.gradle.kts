plugins {
    alias(libs.plugins.agp.app)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
}

android {
    namespace = "xyz.mufanc.applock"
    compileSdk = 34

    defaultConfig {
        applicationId = "xyz.mufanc.applock"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles("proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
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
    implementation(libs.reflector)

    // App
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
}
