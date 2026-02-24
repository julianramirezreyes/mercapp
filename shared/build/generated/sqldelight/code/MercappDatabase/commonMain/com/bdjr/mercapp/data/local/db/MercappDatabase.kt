package com.bdjr.mercapp.`data`.local.db

import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.bdjr.mercapp.`data`.local.db.shared.newInstance
import com.bdjr.mercapp.`data`.local.db.shared.schema
import kotlin.Unit

public interface MercappDatabase : Transacter {
  public val mercappDatabaseQueries: MercappDatabaseQueries

  public companion object {
    public val Schema: SqlSchema<QueryResult.Value<Unit>>
      get() = MercappDatabase::class.schema

    public operator fun invoke(driver: SqlDriver): MercappDatabase =
        MercappDatabase::class.newInstance(driver)
  }
}
