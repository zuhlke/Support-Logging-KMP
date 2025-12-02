package com.zuhlke.logging.integration.room.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface LogDao {
    @Query("SELECT * FROM log WHERE id > :id")
    fun getLogsAfter(id: Int): Flow<List<Log>>

    @Insert
    suspend fun insert(log: Log)

    @Insert
    suspend fun insert(appRun: AppRun): Long

    @Query("SELECT id FROM apprun WHERE rowid = :rowId")
    suspend fun getAppRunIdByRowId(rowId: Long): Int

    @Query("SELECT * FROM apprun WHERE id > :id")
    fun getAppRunsAfter(id: Int): Flow<List<AppRun>>
}
