package com.bdjr.mercapp.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import com.bdjr.mercapp.data.local.db.MercappDatabase
import com.bdjr.mercapp.platform.PlatformContext
import java.io.File

actual class DatabaseDriverFactory actual constructor(
    private val context: PlatformContext,
) {
    actual fun createDriver(): SqlDriver {
        val dbFile = File("mercapp.db")
        val existedBeforeOpen = dbFile.exists()
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")

        if (!existedBeforeOpen) {
            MercappDatabase.Schema.create(driver)
            setUserVersion(driver, MercappDatabase.Schema.version)
            return driver
        }

        val currentUserVersion = getUserVersion(driver)
        if (currentUserVersion == 0L && hasShoppingDetailColumn(driver)) {
            setUserVersion(driver, MercappDatabase.Schema.version)
            return driver
        }

        if (currentUserVersion < MercappDatabase.Schema.version) {
            MercappDatabase.Schema.migrate(driver, currentUserVersion, MercappDatabase.Schema.version)
            setUserVersion(driver, MercappDatabase.Schema.version)
        }
        return driver
    }

    private fun getUserVersion(driver: SqlDriver): Long {
        return driver
            .executeQuery(null, "PRAGMA user_version", { cursor: SqlCursor ->
                QueryResult.Value(cursor.getLong(0) ?: 0L)
            }, 0, null)
            .value
    }

    private fun setUserVersion(driver: SqlDriver, version: Long) {
        driver.execute(null, "PRAGMA user_version = $version", 0)
    }

    private fun hasShoppingDetailColumn(driver: SqlDriver): Boolean {
        return driver
            .executeQuery(null, "PRAGMA table_info(products)", { cursor: SqlCursor ->
                var found = false
                while (cursor.next().value) {
                    val name = cursor.getString(1)
                    if (name == "shopping_detail") {
                        found = true
                        break
                    }
                }
                QueryResult.Value(found)
            }, 0, null)
            .value
    }
}
