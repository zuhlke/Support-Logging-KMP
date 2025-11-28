package com.zuhlke.logging.viewer

import android.app.ComponentCaller
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zuhlke.logging.viewer.data.contentprovider.ContentProviderAppRunsWithLogsRepository
import com.zuhlke.logging.viewer.export.LogExporter
import com.zuhlke.logging.viewer.navigation.Navigation
import com.zuhlke.logging.viewer.ui.theme.LogsViewerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appRunsWithLogsRepositoryFactory: ContentProviderAppRunsWithLogsRepository.Factory

    @Inject
    lateinit var logExporter: LogExporter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isPermissionGranted =
            checkSelfPermission("com.zuhlke.logging.permission.READ_LOGS") ==
                PackageManager.PERMISSION_GRANTED
        Log.d("MainActivity", "Permission granted: $isPermissionGranted")
        // TODO: handle the case when permission is not granted (the problem has to be fixed and the app has to be reinstalled)

        enableEdgeToEdge()
        setContent {
            LogsViewerTheme {
                Navigation(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding(),
                    appRunsWithLogsRepositoryFactory,
                    logExporter
                )
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller
    ) {
        super.onActivityResult(requestCode, resultCode, data, caller)
        Log.d(
            "MainActivity",
            "onActivityResult: requestCode=$requestCode, resultCode=$resultCode, data=$data"
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LogsViewerTheme {
        Greeting("Android")
    }
}
