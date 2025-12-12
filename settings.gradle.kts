pluginManagement {
    repositories {
//        maven {
//            name = "google mirror"
//            url = uri("https://maven.aliyun.com/repository/google")
//        }
        maven {
            name = "central mirror"
            url = uri("https://maven.aliyun.com/repository/central")
        }
        google()
        maven {
            name = "Xposed"
            url = uri("https://api.xposed.info/")
        }
        maven {
            name = "Sonatype Releases"
            url = uri("https://s01.oss.sonatype.org/content/repositories/releases")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
//        maven {
//            name = "google mirror"
//            url = uri("https://maven.aliyun.com/repository/google")
//        }
        maven {
            name = "central mirror"
            url = uri("https://maven.aliyun.com/repository/central")
        }
        google()
        maven {
            name = "Xposed"
            url = uri("https://api.xposed.info/")
        }
        maven {
            name = "Sonatype Releases"
            url = uri("https://s01.oss.sonatype.org/content/repositories/releases")
        }
        mavenCentral()
    }
}
rootProject.name = "PureNGA"
include(":app")
