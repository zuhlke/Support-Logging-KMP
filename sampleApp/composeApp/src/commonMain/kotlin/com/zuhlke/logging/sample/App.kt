package com.zuhlke.logging.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zuhlke.logging.SafeLogger
import com.zuhlke.logging.data.Severity
import com.zuhlke.logging.hash
import com.zuhlke.logging.integrations.kermit.toKermitSeverity
import com.zuhlke.logging.public
import com.zuhlke.logging.safeString
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.random.Random

typealias KermitLogger = co.touchlab.kermit.Logger

@Composable
@Preview
fun App() {
    val logger = remember { SafeLogger("SampleApp") }
    val loggerWithDifferentTag = remember { SafeLogger("SampleApp2") }
    var counter by remember { mutableIntStateOf(0) }

    fun addFakeLogs(severity: Severity, throwable: Throwable? = null) {
        counter += 1
        val password = Random.nextInt().toString()
        KermitLogger.logBlock(
            severity = severity.toKermitSeverity(),
            tag = "KermitLogs",
            throwable = null
        ) { "Kermit: password = $password" }
        logger.log(severity, throwable) {
            safeString(
                """
ZuhlkeLogger: password = $password, 
counter (public) = ${public(counter)} 
hash = ${hash(password)}"""
            )
        }
        loggerWithDifferentTag.log(
            severity,
            safeString("ZuhlkeLogger: password = $password"),
            throwable
        )
    }

    MaterialTheme {
        // TODO: use different colors
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .padding(vertical = 24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                addFakeLogs(Severity.Verbose)
            }) {
                Text("Add verbose log")
            }
            Button(onClick = {
                addFakeLogs(Severity.Debug)
            }) {
                Text("Add debug log")
            }
            Button(onClick = {
                addFakeLogs(Severity.Info)
            }) {
                Text("Add info log")
            }
            Button(onClick = {
                addFakeLogs(Severity.Warn)
            }) {
                Text("Add warning log")
            }
            Button(onClick = {
                addFakeLogs(
                    Severity.Error,
                    throwable = IllegalStateException("Sample exception $counter")
                )
            }) {
                Text("Add error log")
            }
            Button(onClick = {
                addFakeLogs(
                    Severity.Assert,
                    throwable = IllegalStateException("Sample exception $counter")
                )
            }) {
                Text("Add assertion log")
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = {
                throw IllegalStateException("Crash test $counter")
            }) {
                Text("Crash app")
            }
        }
    }
}
