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

group = "io.github.zuhlke"
version = "0.4.0"

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "logging", version.toString())

    pom {
        name = "Logging library"
        description = "A logging library which is safe to use in production builds."
        inceptionYear = "2025"
        url = "https://github.com/zuhlke/Support-Logging-KMP/"
        licenses {
            license {
                name = "MIT License"
                url = "https://opensource.org/licenses/mit"
            }
        }
        developers {
            developer {
                id = "alexander-mironov"
                name = "Alexander Mironov"
                email = "alexander.mironov@zuhlke.com"
                organization = "Zuhlke Engineering Ltd"
                organizationUrl = "https://www.zuehlke.com"
            }
        }
        scm {
            url = "https://github.com/zuhlke/Support-Logging-KMP/"
            connection = "scm:git:git://github.com/zuhlke/Support-Logging-KMP.git"
            developerConnection = "scm:git:ssh://git@github.com/zuhlke/Support-Logging-KMP.git"
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
