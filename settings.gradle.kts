pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven ("https://storage.zego.im/maven") // <- Add this line.
        maven ("https://jitpack.io")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven ("https://storage.zego.im/maven") // <- Add this line.
        maven ("https://jitpack.io")
    }
}

rootProject.name = "People"
include(":app")
 