package com.bdjr.mercapp.`data`.local.db.shared

import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.bdjr.mercapp.`data`.local.db.MercappDatabase
import com.bdjr.mercapp.`data`.local.db.MercappDatabaseQueries
import kotlin.Long
import kotlin.Unit
import kotlin.reflect.KClass

internal val KClass<MercappDatabase>.schema: SqlSchema<QueryResult.Value<Unit>>
  get() = MercappDatabaseImpl.Schema

internal fun KClass<MercappDatabase>.newInstance(driver: SqlDriver): MercappDatabase =
    MercappDatabaseImpl(driver)

private class MercappDatabaseImpl(
  driver: SqlDriver,
) : TransacterImpl(driver),
    MercappDatabase {
  override val mercappDatabaseQueries: MercappDatabaseQueries = MercappDatabaseQueries(driver)

  public object Schema : SqlSchema<QueryResult.Value<Unit>> {
    override val version: Long
      get() = 3

    override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
      driver.execute(null, """
          |CREATE TABLE establishments (
          |  id TEXT NOT NULL PRIMARY KEY,
          |  name TEXT NOT NULL,
          |  created_at INTEGER NOT NULL,
          |  updated_at INTEGER NOT NULL,
          |  is_dirty INTEGER NOT NULL,
          |  is_deleted INTEGER NOT NULL
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE products (
          |  id TEXT NOT NULL PRIMARY KEY,
          |  name TEXT NOT NULL,
          |  establishment_id TEXT NOT NULL,
          |  is_in_shopping_list INTEGER NOT NULL,
          |  created_at INTEGER NOT NULL,
          |  updated_at INTEGER NOT NULL,
          |  is_dirty INTEGER NOT NULL,
          |  is_deleted INTEGER NOT NULL
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE sync_state (
          |  key TEXT NOT NULL PRIMARY KEY,
          |  value INTEGER NOT NULL
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE auth_session (
          |  singleton_id INTEGER NOT NULL PRIMARY KEY,
          |  access_token TEXT NOT NULL,
          |  refresh_token TEXT NOT NULL,
          |  token_type TEXT NOT NULL,
          |  expires_at INTEGER NOT NULL,
          |  user_id TEXT NOT NULL,
          |  email TEXT
          |)
          """.trimMargin(), 0)
      return QueryResult.Unit
    }

    private fun migrateInternal(
      driver: SqlDriver,
      oldVersion: Long,
      newVersion: Long,
    ): QueryResult.Value<Unit> {
      if (oldVersion <= 2 && newVersion > 2) {
        driver.execute(null, """
            |CREATE TABLE IF NOT EXISTS sync_state (
            |  key TEXT NOT NULL PRIMARY KEY,
            |  value INTEGER NOT NULL
            |)
            """.trimMargin(), 0)
        driver.execute(null, """
            |CREATE TABLE IF NOT EXISTS auth_session (
            |  singleton_id INTEGER NOT NULL PRIMARY KEY,
            |  access_token TEXT NOT NULL,
            |  refresh_token TEXT NOT NULL,
            |  token_type TEXT NOT NULL,
            |  expires_at INTEGER NOT NULL,
            |  user_id TEXT NOT NULL,
            |  email TEXT
            |)
            """.trimMargin(), 0)
      }
      return QueryResult.Unit
    }

    override fun migrate(
      driver: SqlDriver,
      oldVersion: Long,
      newVersion: Long,
      vararg callbacks: AfterVersion,
    ): QueryResult.Value<Unit> {
      var lastVersion = oldVersion

      callbacks.filter { it.afterVersion in oldVersion until newVersion }
      .sortedBy { it.afterVersion }
      .forEach { callback ->
        migrateInternal(driver, oldVersion = lastVersion, newVersion = callback.afterVersion + 1)
        callback.block(driver)
        lastVersion = callback.afterVersion + 1
      }

      if (lastVersion < newVersion) {
        migrateInternal(driver, lastVersion, newVersion)
      }
      return QueryResult.Unit
    }
  }
}
