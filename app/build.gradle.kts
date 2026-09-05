val verCode = 58
val verName = "3.4.0"

val javaVersion = JavaVersion.VERSION_21

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.chrxw.purenga"
    compileSdk = 37

    buildFeatures {
        buildConfig = true
        resValues = true
    }

    defaultConfig {
        applicationId = "com.chrxw.purenga"
        minSdk = 24
        targetSdk = 37
        versionCode = verCode
        versionName = verName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        proguardFiles()
        multiDexEnabled = false
        proguardFiles
    }

    signingConfigs {
        create("release") {
            val keystoreFile = System.getenv("KEYSTORE_FILE")
            if (!keystoreFile.isNullOrEmpty()) {
                val password = System.getenv("KEYSTORE_PASSWORD")
                val alias = System.getenv("KEY_ALIAS")
                val keyPass = System.getenv("KEY_PASSWORD")
                require(!password.isNullOrEmpty()) { "KEYSTORE_PASSWORD must be set when KEYSTORE_FILE is specified" }
                require(!alias.isNullOrEmpty()) { "KEY_ALIAS must be set when KEYSTORE_FILE is specified" }
                require(!keyPass.isNullOrEmpty()) { "KEY_PASSWORD must be set when KEYSTORE_FILE is specified" }
                storeFile = file(keystoreFile)
                storePassword = password
                keyAlias = alias
                keyPassword = keyPass
            }
        }
    }

    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName(
                if (System.getenv("KEYSTORE_FILE").isNullOrEmpty()) "debug" else "release"
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

    buildToolsVersion = "36.0.0"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion.toString())
    }
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
}
