// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.2.2" apply false
    id("com.android.library") version "7.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.7.10" apply false
    id("org.jetbrains.kotlin.jvm") version "1.7.10" apply false
    id("dev.rikka.tools.materialthemebuilder") version "1.3.3" apply false
}

val androidCompileSdkVersion by extra(32)
val androidMinSdkVersion by extra(28)
val androidTargetSdkVersion by extra(32)
val androidSourceCompatibility by extra(JavaVersion.VERSION_11)
val androidTargetCompatibility by extra(JavaVersion.VERSION_11)
val kotlinJvmTarget by extra("11")

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
