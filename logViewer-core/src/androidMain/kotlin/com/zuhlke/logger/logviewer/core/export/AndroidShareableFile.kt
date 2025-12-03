package com.zuhlke.logger.logviewer.core.export

import android.net.Uri

internal class AndroidShareableFile(uri: Uri) : ShareableFile {
    override val uriString: UriString = uri.toString()
}
