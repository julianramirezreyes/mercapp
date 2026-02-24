package com.bdjr.mercapp.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.bdjr.mercapp.data.local.db.MercappDatabase
import com.bdjr.mercapp.platform.PlatformContext

actual class DatabaseDriverFactory actual constructor(
    private val context: PlatformContext,
) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = MercappDatabase.Schema,
            context = context.appContext,
            name = "mercapp.db",
        )
    }
}
