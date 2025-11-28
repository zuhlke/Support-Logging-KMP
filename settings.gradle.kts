rootProject.name = "SupportLoggingKmp"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":composeApp")
project(":composeApp").projectDir = File("sampleApp/composeApp")
include(":logging")
include(":logViewerApp")
project(":logViewerApp").projectDir = File("logViewer/app")
include(":logViewerBaselineprofile")
project(":logViewerBaselineprofile").projectDir = File("logViewer/baselineprofile")
include(":logging-core")
