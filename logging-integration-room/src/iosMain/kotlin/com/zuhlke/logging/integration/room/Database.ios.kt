package com.zuhlke.logging.integration.room

import androidx.room.Room
import androidx.room.RoomDatabase
import com.zuhlke.logging.integration.room.data.LOG_DB_FILENAME
import com.zuhlke.logging.integration.room.data.LogDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

internal fun getDatabaseBuilder(): RoomDatabase.Builder<LogDatabase> {
    val dbFilePath = documentDirectory() + "/" + LOG_DB_FILENAME
    return Room.databaseBuilder<LogDatabase>(
        name = dbFilePath,
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
  val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
    directory = NSDocumentDirectory,
    inDomain = NSUserDomainMask,
    appropriateForURL = null,
    create = false,
    error = null,
  )
  return requireNotNull(documentDirectory?.path)
}