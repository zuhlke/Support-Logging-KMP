package com.zuhlke.logging.viewer.ui.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.zuhlke.logging.viewer.R

fun Context.startShare(uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/json"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(
        Intent.createChooser(
            intent,
            getString(R.string.share_via)
        )
    )
}