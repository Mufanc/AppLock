plugins {
    alias(libs.plugins.agp.app) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.agp.library) apply false
}

val androidMinSdkVersion by extra(27)
val androidTargetSdkVersion by extra(34)
val androidCompileSdkVersion by extra(34)

val androidSourceCompatibility by extra(JavaVersion.VERSION_17)
val androidTargetCompatibility by extra(JavaVersion.VERSION_17)
val androidKotlinJvmTarget by extra("17")
