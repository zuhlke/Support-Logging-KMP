import java.net.URI
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.terpal)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    id("maven-publish")
}

kotlin {
    explicitApi()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(libs.terpal.runtime)
            implementation(libs.kermit)
            implementation(libs.coroutines.core)
            implementation(libs.room.runtime)
        }

        androidMain.dependencies {
            implementation(libs.sqlite.framework)
        }

        iosMain.dependencies {
            implementation(libs.sqlite.bundled)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
    }

    androidLibrary {
        compileSdk = libs.versions.library.compileSdk.get().toInt()
        minSdk = libs.versions.library.minSdk.get().toInt()
        namespace = "com.zuhlke.logging"
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    ksp(libs.room.compiler)
}

group = "com.zuhlke"
version = "0.1.0"

publishing {
    publications {
        create<MavenPublication>("LoggingLibrary") {
            artifactId = "logging"
        }
    }

    repositories {
        maven {
            val localProps = Properties()
            val localPropsFile = rootProject.file("local.properties")
            if (localPropsFile.exists()) {
                localProps.load(localPropsFile.inputStream())
            }


            name = "GitHubPackages"
            url = URI.create("https://maven.pkg.github.com/zuhlke/Support-Logging-KMP")
            credentials {
                username = localProps.getProperty("gpr.user") ?: System.getenv("GITHUB_USER") ?: throw IllegalStateException("GitHub username not provided")
                password = localProps.getProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}