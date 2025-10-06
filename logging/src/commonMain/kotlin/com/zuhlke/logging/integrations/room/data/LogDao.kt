package com.zuhlke.logging.integrations.room.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
internal interface LogDao {
    @Query("SELECT * FROM log WHERE id > :id")
    suspend fun getLogsAfter(id: Int): List<Log>

    @Insert
    suspend fun insert(log: Log)

    @Insert
    suspend fun insert(appRun: AppRun): Long

    @Query("SELECT id FROM apprun WHERE rowid = :rowId")
    suspend fun getAppRunIdByRowId(rowId: Long): Int

    @Query("SELECT * FROM apprun WHERE id > :id")
    suspend fun getAppRunsAfter(id: Int): List<AppRun>
}
