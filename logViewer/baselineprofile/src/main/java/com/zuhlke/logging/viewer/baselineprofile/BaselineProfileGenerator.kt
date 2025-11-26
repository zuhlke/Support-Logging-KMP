package com.zuhlke.logging.viewer.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This test class generates a basic startup baseline profile for the target package.
 *
 * We recommend you start with this but add important user flows to the profile to improve their performance.
 * Refer to the [baseline profile documentation](https://d.android.com/topic/performance/baselineprofiles)
 * for more information.
 *
 * You can run the generator with the "Generate Baseline Profile" run configuration in Android Studio or
 * the equivalent `generateBaselineProfile` gradle task:
 * ```
 * ./gradlew :app:generateReleaseBaselineProfile
 * ```
 * The run configuration runs the Gradle task and applies filtering to run only the generators.
 *
 * Check [documentation](https://d.android.com/topic/performance/benchmarking/macrobenchmark-instrumentation-args)
 * for more information about available instrumentation arguments.
 *
 * After you run the generator, you can verify the improvements running the [StartupBenchmarks] benchmark.
 *
 * When using this class to generate a baseline profile, only API 33+ or rooted API 28+ are supported.
 *
 * The minimum required version of androidx.benchmark to generate a baseline profile is 1.2.0.
 **/
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() {
        // The application id for the running build variant is read from the instrumentation arguments.
        rule.collect(
            packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
                ?: throw Exception("targetAppId not passed as instrumentation runner arg"),

            // See: https://d.android.com/topic/performance/baselineprofiles/dex-layout-optimizations
            includeInStartupProfile = true
        ) {
            // This block defines the app's critical user journey
            pressHome()
            startActivityAndWait()

            waitForAsyncContent()
            chooseApp()
            scrollLogs()

            // Check UiAutomator documentation for more information how to interact with the app.
            // https://d.android.com/training/testing/other-components/ui-automator
        }
    }
}

fun MacrobenchmarkScope.waitForAsyncContent() {
    device.wait(Until.hasObject(By.res("app_list")), 5_000)
    val providerList = device.findObject(By.res("app_list"))
    providerList.wait(
        Until.hasObject(By.text("Name: com.zuhlke.logging.viewer.baselineprofile")),
        5_000
    )
}

fun MacrobenchmarkScope.chooseApp() {
    device.findObject(By.text("Name: com.zuhlke.logging.viewer.baselineprofile")).click()
    device.wait(Until.gone(By.res("app_list")), 5_000)
}

fun MacrobenchmarkScope.scrollLogs() {
    val logList = device.findObject(By.res("log_list"))
    logList.wait(Until.hasObject(By.text("App closed")), 5_000)
    // Set gesture margin to avoid triggering gesture navigation.
    logList.setGestureMargin(device.displayWidth / 5)
    logList.fling(Direction.UP)
    device.waitForIdle()
}