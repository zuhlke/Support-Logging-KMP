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
        }

        androidMain.dependencies {
            implementation(libs.room.runtime)
            implementation(libs.sqlite.framework)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.coroutines.test)
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
    add("kspAndroid", libs.room.compiler)
}

group = "com.zuhlke"
version = "0.1.1"

publishing {

    repositories {
        maven {
            val localProps = Properties()
            val localPropsFile = rootProject.file("local.properties")
            if (localPropsFile.exists()) {
                localProps.load(localPropsFile.inputStream())
            }

            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/zuhlke/Support-Logging-KMP")
            credentials {
                username =
                    localProps.getProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR")
                        ?: throw IllegalStateException("GitHub username not provided")
                password = localProps.getProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
