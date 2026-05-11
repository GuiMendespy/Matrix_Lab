pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Se for .kts (Kotlin), use esta linha:
        maven { url = uri("https://jitpack.io") }

        // Se for .gradle (Groovy), use esta:
        // maven { url 'https://jitpack.io' }
    }
}

rootProject.name = "MatrixLab"
include(":app")