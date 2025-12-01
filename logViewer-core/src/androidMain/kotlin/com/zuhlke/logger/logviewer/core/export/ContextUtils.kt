package com.zuhlke.logger.logviewer.core.export

import android.content.Context
import android.content.Intent
import android.net.Uri
import supportloggingkmp.logviewer_core.generated.resources.Res
import supportloggingkmp.logviewer_core.generated.resources.share_via

suspend fun Context.startShare(uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/json"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(
        Intent.createChooser(
            intent,
            org.jetbrains.compose.resources.getString(Res.string.share_via)
        )
    )
}
