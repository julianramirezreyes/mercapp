package com.bdjr.mercapp.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.bdjr.mercapp.data.local.db.MercappDatabase
import com.bdjr.mercapp.platform.PlatformContext
import java.io.File

actual class DatabaseDriverFactory actual constructor(
    private val context: PlatformContext,
) {
    actual fun createDriver(): SqlDriver {
        val dbFile = File("mercapp.db")
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
        if (!dbFile.exists()) {
            MercappDatabase.Schema.create(driver)
        } else {
            MercappDatabase.Schema.migrate(driver, 1, MercappDatabase.Schema.version)
        }
        return driver
    }
}
