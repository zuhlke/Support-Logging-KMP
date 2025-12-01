package com.zuhlke.logger.logviewer.core.export

import android.net.Uri

class AndroidShareableFile(uri: Uri) : ShareableFile {
    override val uriString: UriString = uri.toString()
}
