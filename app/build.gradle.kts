val verCode = 60
val verName = "3.5.0"

// 测试版本附加后缀, 由 CI 传入 -PbuildSuffix=.test.xxxxxxx
val buildSuffix = providers.gradleProperty("buildSuffix").getOrElse("")

val javaVersion = JavaVersion.VERSION_21

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.chrxw.purenga"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    buildFeatures {
        buildConfig = true
        resValues = true
    }

    defaultConfig {
        applicationId = "com.chrxw.purenga"
        minSdk = 26
        targetSdk = 37
        versionCode = verCode
        versionName = verName + buildSuffix

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

    packaging {
        resources {
            // 保证 META-INF/xposed/* 不被资源合并丢弃
            merges += "META-INF/xposed/*"
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
    implementation(libs.ezxhelper.core)
    implementation(libs.ezxhelper.xposed.api)
    implementation(libs.ezxhelper.android.utils)
    implementation(libs.okhttp)
    implementation(libs.gson)

    compileOnly(libs.libxposed.api)
    implementation(libs.libxposed.service)

    // 模块界面 (Miuix / HyperOS 风格)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
//    implementation(libs.miuix.ui)
//    implementation(libs.miuix.icons)

    // View 体系的 Material 组件, 供注入进程内的弹窗 (DialogUtils) 使用
    implementation(libs.material)
}
