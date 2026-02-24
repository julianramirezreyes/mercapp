package com.bdjr.mercapp.data.local

import app.cash.sqldelight.db.SqlDriver
import com.bdjr.mercapp.platform.PlatformContext

expect class DatabaseDriverFactory(context: PlatformContext) {
    fun createDriver(): SqlDriver
}
