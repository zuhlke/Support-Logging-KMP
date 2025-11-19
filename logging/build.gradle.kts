import java.util.Properties
import org.gradle.kotlin.dsl.dokka

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.terpal)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.binaryCompatibilityValidator)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
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
version = "0.3.0"

publishing {

    repositories {
        maven {
            name = "githubPackages"
            url = uri("https://maven.pkg.github.com/zuhlke/Support-Logging-KMP")
            credentials(PasswordCredentials::class)
        }
    }
}

dokka {
    moduleName.set("Zuhkle Logger")
    dokkaPublications.html {
        suppressInheritedMembers.set(true)
        failOnWarning.set(true)
    }
    dokkaSourceSets.forEach {
        it.reportUndocumented.set(true)
    }
}

tasks.named("check").configure { dependsOn(":logging:dokkaGenerateHtml") }
