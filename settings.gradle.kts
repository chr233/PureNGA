pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven("https://api.xposed.info/")
        maven("https://s01.oss.sonatype.org/content/repositories/releases")
        mavenCentral()
    }
}
rootProject.name = "PureNGA"
include(":app")
