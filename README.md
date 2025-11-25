# Zuhlke Safe Logging KMP library

iOS‑style privacy redaction for Kotlin Multiplatform logging (Android & iOS). When `useSafeInterpolation = true`, every interpolated value is redacted unless wrapped with `public(...)` or `hash(...)`. String interpolation lets you safely keep production logs on: sanitized output appears in Android Logcat + a signature‑protected ContentProvider, and in OSLog on iOS.

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.zuhlke/logging)

## Technical Implementation

- **Custom String Interpolation:**
  The library uses the Terpal compiler plugin to transform log messages. Developers use `safeString` with `public(...)`, `hash(...)`, or default redaction to control what appears in logs. This guarantees that sensitive data is never accidentally logged.

- **Android Log Exposure:**
  On Android, logs are made available to external tools via a custom `ContentProvider`. This allows log collection and analysis even in production environments.

- **Security via Signature-Based Permissions:**
  The ContentProvider is protected using signature-based permissions. Only apps signed with the same certificate as the host app can access the logs, ensuring that sensitive log data is not exposed to unauthorized third-party apps or users.

- **iOS Integration:**
  On iOS, the library delegates to OSLog, taking advantage of its built-in redaction and privacy features.

## Using the Safe Library in Your Own KMP Project

The library provides a Kotlin Multiplatform logging abstraction with:

### 1. Add the Dependency in Your Shared Module

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.zuhlke:logging:<version>")
        }
    }
}
```

The library declares `api(io.exoquery:terpal-runtime:...)`, so you automatically receive the
runtime. You must also apply the Terpal Gradle plugin so the interpolator function is transformed at
compile time.

### 2. Apply the Terpal Plugin

Add to your module plugins block:

```kotlin
plugins {
    id("io.exoquery.terpal-plugin") version "2.2.20-2.0.1.PL"
}
```

(Version matches the one used internally; keep them aligned.)

### 3. Initialize the Logger

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

### 4. Creating and Using a `SafeLogger`

```kotlin
val authLogger = SafeLogger("Auth")

authLogger.d(safeString("User login starting for id=${public(user.id)} token=${hash(token)}"))

try {
    // risky stuff
} catch (t: Throwable) {
    authLogger.e(t) {
        safeString("Login with ${hash(login)} and $password failed for reason=${public(t.message)}")
    }
}
```

Notes:

* `public(value)` marks data safe for plain logging.
* `hash(value)` emits a stable hash so events can be correlated without exposing raw data.
* If unsafe interpolation is enabled (debug builds), the raw string & params are logged to aid
  debugging.

### Example Output

Below are illustrative (trimmed) outputs. Actual formatting may vary slightly by platform/tooling.

#### Android (useSafeInterpolation = true)
```kotlin
val logger = SafeLogger("Auth")
val userId = "U12345"
val token = "abcd-efgh"
val password = "SuperSecret!"
logger.d(safeString("Login attempt id=${public(userId)} token=${hash(token)} password=$password"))
```
Logcat (sanitized):
```
D/Auth: Login attempt id=U12345 token=5f26c9e1 password=<redacted>
```
Where:
* `public(userId)` -> U12345
* `hash(token)` -> stable hash (example: 5f26c9e1) for consistent correlation
* Plain `$password` -> redacted (`<redacted>`) because not wrapped

### 5. Exporting the Library to iOS Consumers

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

### 6. Typical Troubleshooting

* Exception: `InnerLogger is not initialized. Call init() first.` – Ensure
  `ZuhlkeLogger.initialize(...)` ran before any log call.
* Exception: `InnerLogger is already initialized` – Remove duplicate initialization (e.g., multiple
  test runners or multiple Application instances in instrumentation tests). For tests you can expose
  an internal `reset()` via `@VisibleForTesting` (already present internally) in a test-only source
  set if required.

## Library Development & Maintenance (for contributors / maintainers)

The following tasks are only relevant if you are developing or contributing to this library itself. Consumers using the published artifact do not need them.

### Running All Tests

Execute the full test suite across all targets:

```shell
./gradlew allTests
```

### Pre-Push Git Hook Installation

Installs a pre-push hook that runs quality checks before pushing. (Optional; for contributors.)

```shell
./gradlew installGitHook
```

Confirm installation:

```shell
ls -l .git/hooks/pre-push
```

If you need custom behavior, modify `scripts/pre-push` then re-run the task.

---
