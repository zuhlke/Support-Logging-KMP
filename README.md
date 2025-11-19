# Zuhlke Logging KMP library

This is a Zuhlke Logging Kotlin Multiplatform (KMP) project targeting Android, iOS.

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.zuhlke/logging)

## Using the `com.zuhlke.logging` Library in Your Own KMP Project

The library provides a Kotlin Multiplatform logging abstraction with:

* Pluggable writers (Kermit on Android/iOS, and Room on Android are hardcoded for now)
* Safe string interpolation (via the Terpal compiler plugin) with redaction, hashing (`hash(...)`)
  and explicit publication (`public(...)`)
* Unified initialization API: `ZuhlkeLogger.initialize(...)`
* A lightweight API wrapper: `SafeLogger(tag)`

Current published coordinates:

```
Group:    com.zuhlke
Artifact: logging
Version:  0.3.0
```

### 1. Add GitHub Packages Maven Repository

The artifacts are published to GitHub Packages at:
`https://maven.pkg.github.com/zuhlke/Support-Logging-KMP`.
In your root `settings.gradle.kts` add:

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven {

            val localProps = java.util.Properties()

            val localPropsFile = File(settingsDir, "local.properties")
            if (localPropsFile.exists()) {
                localProps.load(localPropsFile.inputStream())
            }

            url = uri("https://maven.pkg.github.com/zuhlke/Support-Logging-KMP")
            credentials {
                username =
                    System.getenv("GITHUB_ACTOR") ?: project.findProperty("gpr.user") as String?
                            ?: error("GITHUB_ACTOR/gpr.user not set")
                password =
                    System.getenv("GITHUB_TOKEN") ?: project.findProperty("gpr.key") as String?
                            ?: error("GITHUB_TOKEN/gpr.key not set")
            }
        }
    }
}
```

You should store `gpr.user` and `gpr.key` in the project `local.properties`.

### 2. Add the Dependency in Your Shared Module

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.zuhlke:logging:0.3.0")
        }
    }
}
```

The library declares `api(io.exoquery:terpal-runtime:...)`, so you automatically receive the
runtime. If you want to use `safeString("...")` interpolation in your own module, you must also
apply the Terpal Gradle plugin so the interpolator function is transformed at compile time.

### 3. Apply the Terpal Plugin

Add to your module plugins block:

```kotlin
plugins {
    id("io.exoquery.terpal-plugin") version "2.2.20-2.0.1.PL"
}
```

(Version matches the one used internally; keep them aligned.)

### 4. Initialize the Logger

Initialization must happen exactly once per process before logging.

#### Android

Update your `Application` subclass:

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // useSafeInterpolation parameter is omitted and defaults to TRUE in release (non-debuggable) builds.
        ZuhlkeLogger.initialize(application = this)
    }
}
```

Internally this sets up:

* A `DelegatingLogDispatcher` with Kermit + Room log writers
* Safe or unsafe interpolation strategy (safe redacts unless marked `public`)

#### iOS (Swift / SwiftUI)

Call the initializer early (e.g., in your App struct or AppDelegate):

```swift
import Shared // The generated framework that includes the logging library

@main
struct MyApp: App {
    init() {
        // Pass true in production to enforce safe interpolation.
        ZuhlkeLogger.shared.initialize(useSafeInterpolation: true)
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

On iOS the current implementation wires only the Kermit log writer (OSLog).

### 5. Creating and Using a `SafeLogger`

```kotlin
val authLogger = SafeLogger("Auth")

authLogger.d(safeString("User login starting for id={public(user.id)} token={hash(token)}"))

try {
    // risky stuff
} catch (t: Throwable) {
    authLogger.e(t) {
        safeString(
            "Login with ${hash(login)} and $password failed for reason=${
                public(
                    t.message
                )
            }"
        )
    }
}
```

Notes:

* `public(value)` marks data safe for plain logging.
* `hash(value)` emits a stable hash so events can be correlated without exposing raw data.
* If unsafe interpolation is enabled (debug builds), the raw string & params are logged to aid
  debugging.

### 6. Exporting the Library to iOS Consumers

If you have a shared KMP module (e.g. `:composeApp`) that produces an iOS framework and you want the
logging API visible to Swift:

```kotlin
kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            export(libs.zuhlke.logging)
        }
    }
}

dependencies {
    api(libs.zuhlke.logging)
}
```

Regenerate the framework (`./gradlew :composeApp:assemble`) then import the umbrella framework in
Swift. The exported `ZuhlkeLogger` object and `SafeLogger` class will be available.

### 7. Typical Troubleshooting

* Exception: `InnerLogger is not initialized. Call init() first.` – Ensure
  `ZuhlkeLogger.initialize(...)` ran before any log call.
* Exception: `InnerLogger is already initialized` – Remove duplicate initialization (e.g., multiple
  test runners or multiple Application instances in instrumentation tests). For tests you can expose
  an internal `reset()` via `@VisibleForTesting` (already present internally) in a test-only source
  set if required.
* Interpolation still shows placeholders – Confirm the Terpal plugin is applied in the module using
  `safeString`.

### 8. Running All Tests

```shell
./gradlew allTests
```

### 9. Pre-Push Git Hook Installation

A Gradle task `installGitHook` copies `scripts/pre-push` into `.git/hooks/pre-push` with execute
permissions.
Install once (re-run if the script changes):

```shell
./gradlew installGitHook
```

Confirm:

```shell
ls -l .git/hooks/pre-push
```

If you need custom behavior, modify `scripts/pre-push` then re-run the task.

---
