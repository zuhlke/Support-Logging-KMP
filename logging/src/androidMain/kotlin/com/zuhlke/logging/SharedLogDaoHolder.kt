package com.zuhlke.logging

import com.zuhlke.logging.integrations.room.data.LogDao

internal object SharedLogDaoHolder {
    // TODO: safeguard it
    lateinit var logDao: LogDao
}
