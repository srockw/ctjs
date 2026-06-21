buildscript {
    dependencies {
        classpath(libs.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ksp)
    implementation("org.jsoup:jsoup:1.15.3")
}
