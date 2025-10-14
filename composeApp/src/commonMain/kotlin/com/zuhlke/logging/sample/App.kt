package com.zuhlke.logging.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import co.touchlab.kermit.Logger
import com.zuhlke.logging.SafeLogger
import com.zuhlke.logging.hash
import com.zuhlke.logging.public
import com.zuhlke.logging.safeString
import kotlin.random.Random
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val logger = remember { SafeLogger("SampleApp") }

    MaterialTheme {
        var counter by remember { mutableIntStateOf(0) }
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                counter += 1
                val password = Random.nextInt().toString()
                Logger.v { "Kermit: password = $password" }
                logger.v {
                    safeString(
                        "ZuhlkeLogger: password = $password, counter (public) = ${public(
                            counter
                        )} hash = ${
                            hash(
                                password
                            )
                        }"
                    )
                }
            }) {
                Text("Add verbose log")
            }
            Button(onClick = {
                counter += 1
                val password = Random.nextInt().toString()
                Logger.d { "Kermit: password = $password" }
                logger.d {
                    safeString(
                        "ZuhlkeLogger: password = $password, counter (public) = ${public(
                            counter
                        )} hash = ${
                            hash(
                                password
                            )
                        }"
                    )
                }
            }) {
                Text("Add debug log")
            }
            Button(onClick = {
                counter += 1
                val password = Random.nextInt().toString()
                Logger.i { "Kermit: password = $password" }
                logger.i {
                    safeString(
                        "ZuhlkeLogger: password = $password, counter (public) = ${public(
                            counter
                        )} hash = ${
                            hash(
                                password
                            )
                        }"
                    )
                }
            }) {
                Text("Add info log")
            }
            Button(onClick = {
                counter += 1
                val password = Random.nextInt().toString()
                try {
                    throw IllegalStateException("Sample exception $counter")
                } catch (e: Exception) {
                    Logger.e(e) { "Kermit: password = $password" }
                    logger.e(e) {
                        safeString(
                            "ZuhlkeLogger: password = $password, counter (public) = ${public(
                                counter
                            )} hash = ${
                                hash(password)
                            }"
                        )
                    }
                }
            }) {
                Text("Add error log")
            }
            Button(onClick = {
                counter += 1
                val password = Random.nextInt().toString()
                try {
                    throw AssertionError("Sample assertion exception $counter")
                } catch (e: Error) {
                    Logger.a(e) { "Kermit: password = $password" }
                    logger.a(e) {
                        safeString(
                            "ZuhlkeLogger: password = $password, counter (public) = ${public(
                                counter
                            )} hash = ${
                                hash(password)
                            }"
                        )
                    }
                }
            }) {
                Text("Add assertion log")
            }
        }
    }
}
