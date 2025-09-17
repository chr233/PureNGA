val verCode = 50
val verName = "3.0.0.Final"

val javaVersion = JavaVersion.VERSION_21

android {
    namespace = "com.chrxw.purenga"
    compileSdk = 35


    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.chrxw.purenga"
        minSdk = 24
        targetSdk = 36
        versionCode = verCode
        versionName = verName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        proguardFiles()
        multiDexEnabled = false
        proguardFiles
    }

    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    androidResources {
        additionalParameters += listOf("--allow-reserved-package-id", "--package-id", "0x50")
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion.toString())
    }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

dependencies {
    implementation(libs.ezxhelper)
    implementation(libs.okhttp)
    implementation(libs.gson)

    compileOnly(libs.xposedapi)

    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.material)
    implementation(libs.androidx.material3)

    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
}
