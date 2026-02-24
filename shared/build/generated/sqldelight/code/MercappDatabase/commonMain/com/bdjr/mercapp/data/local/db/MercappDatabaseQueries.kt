package com.bdjr.mercapp.`data`.local.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Boolean
import kotlin.Long
import kotlin.String

public class MercappDatabaseQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> selectAllEstablishments(includeDeleted: Boolean, mapper: (
    id: String,
    name: String,
    created_at: Long,
    updated_at: Long,
    is_dirty: Boolean,
    is_deleted: Boolean,
  ) -> T): Query<T> = SelectAllEstablishmentsQuery(includeDeleted) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getLong(2)!!,
      cursor.getLong(3)!!,
      cursor.getBoolean(4)!!,
      cursor.getBoolean(5)!!
    )
  }

  public fun selectAllEstablishments(includeDeleted: Boolean): Query<Establishments> =
      selectAllEstablishments(includeDeleted) { id, name, created_at, updated_at, is_dirty,
      is_deleted ->
    Establishments(
      id,
      name,
      created_at,
      updated_at,
      is_dirty,
      is_deleted
    )
  }

  public fun <T : Any> selectEstablishmentById(id: String, mapper: (
    id: String,
    name: String,
    created_at: Long,
    updated_at: Long,
    is_dirty: Boolean,
    is_deleted: Boolean,
  ) -> T): Query<T> = SelectEstablishmentByIdQuery(id) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getLong(2)!!,
      cursor.getLong(3)!!,
      cursor.getBoolean(4)!!,
      cursor.getBoolean(5)!!
    )
  }

  public fun selectEstablishmentById(id: String): Query<Establishments> =
      selectEstablishmentById(id) { id_, name, created_at, updated_at, is_dirty, is_deleted ->
    Establishments(
      id_,
      name,
      created_at,
      updated_at,
      is_dirty,
      is_deleted
    )
  }

  public fun <T : Any> selectProductsByEstablishment(
    establishmentId: String,
    includeDeleted: Boolean,
    mapper: (
      id: String,
      name: String,
      establishment_id: String,
      is_in_shopping_list: Boolean,
      created_at: Long,
      updated_at: Long,
      is_dirty: Boolean,
      is_deleted: Boolean,
    ) -> T,
  ): Query<T> = SelectProductsByEstablishmentQuery(establishmentId, includeDeleted) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getBoolean(3)!!,
      cursor.getLong(4)!!,
      cursor.getLong(5)!!,
      cursor.getBoolean(6)!!,
      cursor.getBoolean(7)!!
    )
  }

  public fun selectProductsByEstablishment(establishmentId: String, includeDeleted: Boolean):
      Query<Products> = selectProductsByEstablishment(establishmentId, includeDeleted) { id, name,
      establishment_id, is_in_shopping_list, created_at, updated_at, is_dirty, is_deleted ->
    Products(
      id,
      name,
      establishment_id,
      is_in_shopping_list,
      created_at,
      updated_at,
      is_dirty,
      is_deleted
    )
  }

  public fun <T : Any> selectProductById(id: String, mapper: (
    id: String,
    name: String,
    establishment_id: String,
    is_in_shopping_list: Boolean,
    created_at: Long,
    updated_at: Long,
    is_dirty: Boolean,
    is_deleted: Boolean,
  ) -> T): Query<T> = SelectProductByIdQuery(id) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getBoolean(3)!!,
      cursor.getLong(4)!!,
      cursor.getLong(5)!!,
      cursor.getBoolean(6)!!,
      cursor.getBoolean(7)!!
    )
  }

  public fun selectProductById(id: String): Query<Products> = selectProductById(id) { id_, name,
      establishment_id, is_in_shopping_list, created_at, updated_at, is_dirty, is_deleted ->
    Products(
      id_,
      name,
      establishment_id,
      is_in_shopping_list,
      created_at,
      updated_at,
      is_dirty,
      is_deleted
    )
  }

  public fun <T : Any> selectDirtyEstablishments(mapper: (
    id: String,
    name: String,
    created_at: Long,
    updated_at: Long,
    is_dirty: Boolean,
    is_deleted: Boolean,
  ) -> T): Query<T> = Query(246_350_195, arrayOf("establishments"), driver, "MercappDatabase.sq",
      "selectDirtyEstablishments", """
  |SELECT establishments.id, establishments.name, establishments.created_at, establishments.updated_at, establishments.is_dirty, establishments.is_deleted
  |FROM establishments
  |WHERE is_dirty = 1
  """.trimMargin()) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getLong(2)!!,
      cursor.getLong(3)!!,
      cursor.getBoolean(4)!!,
      cursor.getBoolean(5)!!
    )
  }

  public fun selectDirtyEstablishments(): Query<Establishments> = selectDirtyEstablishments { id,
      name, created_at, updated_at, is_dirty, is_deleted ->
    Establishments(
      id,
      name,
      created_at,
      updated_at,
      is_dirty,
      is_deleted
    )
  }

  public fun <T : Any> selectDirtyProducts(mapper: (
    id: String,
    name: String,
    establishment_id: String,
    is_in_shopping_list: Boolean,
    created_at: Long,
    updated_at: Long,
    is_dirty: Boolean,
    is_deleted: Boolean,
  ) -> T): Query<T> = Query(-1_665_521_957, arrayOf("products"), driver, "MercappDatabase.sq",
      "selectDirtyProducts", """
  |SELECT products.id, products.name, products.establishment_id, products.is_in_shopping_list, products.created_at, products.updated_at, products.is_dirty, products.is_deleted
  |FROM products
  |WHERE is_dirty = 1
  """.trimMargin()) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getBoolean(3)!!,
      cursor.getLong(4)!!,
      cursor.getLong(5)!!,
      cursor.getBoolean(6)!!,
      cursor.getBoolean(7)!!
    )
  }

  public fun selectDirtyProducts(): Query<Products> = selectDirtyProducts { id, name,
      establishment_id, is_in_shopping_list, created_at, updated_at, is_dirty, is_deleted ->
    Products(
      id,
      name,
      establishment_id,
      is_in_shopping_list,
      created_at,
      updated_at,
      is_dirty,
      is_deleted
    )
  }

  public fun selectSyncState(key: String): Query<Long> = SelectSyncStateQuery(key) { cursor ->
    cursor.getLong(0)!!
  }

  public fun <T : Any> selectAuthSession(mapper: (
    singleton_id: Long,
    access_token: String,
    refresh_token: String,
    token_type: String,
    expires_at: Long,
    user_id: String,
    email: String?,
  ) -> T): Query<T> = Query(-363_631_469, arrayOf("auth_session"), driver, "MercappDatabase.sq",
      "selectAuthSession", """
  |SELECT auth_session.singleton_id, auth_session.access_token, auth_session.refresh_token, auth_session.token_type, auth_session.expires_at, auth_session.user_id, auth_session.email
  |FROM auth_session
  |WHERE singleton_id = 1
  """.trimMargin()) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getLong(4)!!,
      cursor.getString(5)!!,
      cursor.getString(6)
    )
  }

  public fun selectAuthSession(): Query<Auth_session> = selectAuthSession { singleton_id,
      access_token, refresh_token, token_type, expires_at, user_id, email ->
    Auth_session(
      singleton_id,
      access_token,
      refresh_token,
      token_type,
      expires_at,
      user_id,
      email
    )
  }

  /**
   * @return The number of rows updated.
   */
  public fun markEstablishmentClean(updatedAt: Long, id: String): QueryResult<Long> {
    val result = driver.execute(912_292_190, """
        |UPDATE establishments
        |SET is_dirty = 0,
        |    updated_at = ?
        |WHERE id = ?
        """.trimMargin(), 2) {
          bindLong(0, updatedAt)
          bindString(1, id)
        }
    notifyQueries(912_292_190) { emit ->
      emit("establishments")
    }
    return result
  }

  /**
   * @return The number of rows updated.
   */
  public fun markProductClean(updatedAt: Long, id: String): QueryResult<Long> {
    val result = driver.execute(-1_827_293_562, """
        |UPDATE products
        |SET is_dirty = 0,
        |    updated_at = ?
        |WHERE id = ?
        """.trimMargin(), 2) {
          bindLong(0, updatedAt)
          bindString(1, id)
        }
    notifyQueries(-1_827_293_562) { emit ->
      emit("products")
    }
    return result
  }

  /**
   * @return The number of rows updated.
   */
  public fun upsertSyncState(key: String, `value`: Long): QueryResult<Long> {
    val result = driver.execute(-1_695_870_488, """
        |INSERT OR REPLACE INTO sync_state(key, value)
        |VALUES (?, ?)
        """.trimMargin(), 2) {
          bindString(0, key)
          bindLong(1, value)
        }
    notifyQueries(-1_695_870_488) { emit ->
      emit("sync_state")
    }
    return result
  }

  /**
   * @return The number of rows updated.
   */
  public fun upsertAuthSession(
    accessToken: String,
    refreshToken: String,
    tokenType: String,
    expiresAt: Long,
    userId: String,
    email: String?,
  ): QueryResult<Long> {
    val result = driver.execute(-1_416_037_760, """
        |INSERT OR REPLACE INTO auth_session(
        |  singleton_id,
        |  access_token,
        |  refresh_token,
        |  token_type,
        |  expires_at,
        |  user_id,
        |  email
        |)
        |VALUES (
        |  1,
        |  ?,
        |  ?,
        |  ?,
        |  ?,
        |  ?,
        |  ?
        |)
        """.trimMargin(), 6) {
          bindString(0, accessToken)
          bindString(1, refreshToken)
          bindString(2, tokenType)
          bindLong(3, expiresAt)
          bindString(4, userId)
          bindString(5, email)
        }
    notifyQueries(-1_416_037_760) { emit ->
      emit("auth_session")
    }
    return result
  }

  /**
   * @return The number of rows updated.
   */
  public fun clearAuthSession(): QueryResult<Long> {
    val result = driver.execute(-1_367_576_640, """
        |DELETE FROM auth_session
        |WHERE singleton_id = 1
        """.trimMargin(), 0)
    notifyQueries(-1_367_576_640) { emit ->
      emit("auth_session")
    }
    return result
  }

  /**
   * @return The number of rows updated.
   */
  public fun insertEstablishmentIfAbsent(
    id: String,
    name: String,
    createdAt: Long,
    updatedAt: Long,
    isDirty: Boolean,
    isDeleted: Boolean,
  ): QueryResult<Long> {
    val result = driver.execute(-1_667_508_523, """
        |INSERT OR IGNORE INTO establishments(id, name, created_at, updated_at, is_dirty, is_deleted)
        |VALUES (?, ?, ?, ?, ?, ?)
        """.trimMargin(), 6) {
          bindString(0, id)
          bindString(1, name)
          bindLong(2, createdAt)
          bindLong(3, updatedAt)
          bindBoolean(4, isDirty)
          bindBoolean(5, isDeleted)
        }
    notifyQueries(-1_667_508_523) { emit ->
      emit("establishments")
    }
    return result
  }

  /**
   * @return The number of rows updated.
   */
  public fun updateEstablishment(
    name: String,
    updatedAt: Long,
    isDirty: Boolean,
    isDeleted: Boolean,
    id: String,
  ): QueryResult<Long> {
    val result = driver.execute(-746_261_777, """
        |UPDATE establishments
        |SET name = ?,
        |    updated_at = ?,
        |    is_dirty = ?,
        |    is_deleted = ?
        |WHERE id = ?
        """.trimMargin(), 5) {
          bindString(0, name)
          bindLong(1, updatedAt)
          bindBoolean(2, isDirty)
          bindBoolean(3, isDeleted)
          bindString(4, id)
        }
    notifyQueries(-746_261_777) { emit ->
      emit("establishments")
    }
    return result
  }

  /**
   * @return The number of rows updated.
   */
  public fun markEstablishmentDeleted(updatedAt: Long, id: String): QueryResult<Long> {
    val result = driver.execute(1_233_158_638, """
        |UPDATE establishments
        |SET is_deleted = 1,
        |    is_dirty = 1,
        |    updated_at = ?
        |WHERE id = ?
        """.trimMargin(), 2) {
          bindLong(0, updatedAt)
          bindString(1, id)
        }
    notifyQueries(1_233_158_638) { emit ->
      emit("establishments")
    }
    return result
  }

  /**
   * @return The number of rows updated.
   */
  public fun insertProductIfAbsent(
    id: String,
    name: String,
    establishmentId: String,
    isInShoppingList: Boolean,
    createdAt: Long,
    updatedAt: Long,
    isDirty: Boolean,
    isDeleted: Boolean,
  ): QueryResult<Long> {
    val result = driver.execute(-745_556_051, """
        |INSERT OR IGNORE INTO products(id, name, establishment_id, is_in_shopping_list, created_at, updated_at, is_dirty, is_deleted)
        |VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimMargin(), 8) {
          bindString(0, id)
          bindString(1, name)
          bindString(2, establishmentId)
          bindBoolean(3, isInShoppingList)
          bindLong(4, createdAt)
          bindLong(5, updatedAt)
          bindBoolean(6, isDirty)
          bindBoolean(7, isDeleted)
        }
    notifyQueries(-745_556_051) { emit ->
      emit("products")
    }
    return result
  }

  /**
   * @return The number of rows updated.
   */
  public fun updateProduct(
    name: String,
    establishmentId: String,
    isInShoppingList: Boolean,
    updatedAt: Long,
    isDirty: Boolean,
    isDeleted: Boolean,
    id: String,
  ): QueryResult<Long> {
    val result = driver.execute(656_990_151, """
        |UPDATE products
        |SET name = ?,
        |    establishment_id = ?,
        |    is_in_shopping_list = ?,
        |    updated_at = ?,
        |    is_dirty = ?,
        |    is_deleted = ?
        |WHERE id = ?
        """.trimMargin(), 7) {
          bindString(0, name)
          bindString(1, establishmentId)
          bindBoolean(2, isInShoppingList)
          bindLong(3, updatedAt)
          bindBoolean(4, isDirty)
          bindBoolean(5, isDeleted)
          bindString(6, id)
        }
    notifyQueries(656_990_151) { emit ->
      emit("products")
    }
    return result
  }

  /**
   * @return The number of rows updated.
   */
  public fun setProductInShoppingList(
    isInShoppingList: Boolean,
    updatedAt: Long,
    id: String,
  ): QueryResult<Long> {
    val result = driver.execute(-1_569_209_097, """
        |UPDATE products
        |SET is_in_shopping_list = ?,
        |    is_dirty = 1,
        |    updated_at = ?
        |WHERE id = ?
        """.trimMargin(), 3) {
          bindBoolean(0, isInShoppingList)
          bindLong(1, updatedAt)
          bindString(2, id)
        }
    notifyQueries(-1_569_209_097) { emit ->
      emit("products")
    }
    return result
  }

  /**
   * @return The number of rows updated.
   */
  public fun markProductDeleted(updatedAt: Long, id: String): QueryResult<Long> {
    val result = driver.execute(1_306_203_414, """
        |UPDATE products
        |SET is_deleted = 1,
        |    is_dirty = 1,
        |    updated_at = ?
        |WHERE id = ?
        """.trimMargin(), 2) {
          bindLong(0, updatedAt)
          bindString(1, id)
        }
    notifyQueries(1_306_203_414) { emit ->
      emit("products")
    }
    return result
  }

  private inner class SelectAllEstablishmentsQuery<out T : Any>(
    public val includeDeleted: Boolean,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("establishments", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("establishments", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(595_144_258, """
    |SELECT establishments.id, establishments.name, establishments.created_at, establishments.updated_at, establishments.is_dirty, establishments.is_deleted
    |FROM establishments
    |WHERE (? OR is_deleted = 0)
    |ORDER BY name ASC
    """.trimMargin(), mapper, 1) {
      bindBoolean(0, includeDeleted)
    }

    override fun toString(): String = "MercappDatabase.sq:selectAllEstablishments"
  }

  private inner class SelectEstablishmentByIdQuery<out T : Any>(
    public val id: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("establishments", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("establishments", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(1_416_381_326, """
    |SELECT establishments.id, establishments.name, establishments.created_at, establishments.updated_at, establishments.is_dirty, establishments.is_deleted
    |FROM establishments
    |WHERE id = ?
    """.trimMargin(), mapper, 1) {
      bindString(0, id)
    }

    override fun toString(): String = "MercappDatabase.sq:selectEstablishmentById"
  }

  private inner class SelectProductsByEstablishmentQuery<out T : Any>(
    public val establishmentId: String,
    public val includeDeleted: Boolean,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("products", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("products", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-1_089_185_215, """
    |SELECT products.id, products.name, products.establishment_id, products.is_in_shopping_list, products.created_at, products.updated_at, products.is_dirty, products.is_deleted
    |FROM products
    |WHERE establishment_id = ?
    |  AND (? OR is_deleted = 0)
    |ORDER BY name ASC
    """.trimMargin(), mapper, 2) {
      bindString(0, establishmentId)
      bindBoolean(1, includeDeleted)
    }

    override fun toString(): String = "MercappDatabase.sq:selectProductsByEstablishment"
  }

  private inner class SelectProductByIdQuery<out T : Any>(
    public val id: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("products", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("products", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(1_160_047_142, """
    |SELECT products.id, products.name, products.establishment_id, products.is_in_shopping_list, products.created_at, products.updated_at, products.is_dirty, products.is_deleted
    |FROM products
    |WHERE id = ?
    """.trimMargin(), mapper, 1) {
      bindString(0, id)
    }

    override fun toString(): String = "MercappDatabase.sq:selectProductById"
  }

  private inner class SelectSyncStateQuery<out T : Any>(
    public val key: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("sync_state", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("sync_state", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-206_508_869, """
    |SELECT value
    |FROM sync_state
    |WHERE key = ?
    """.trimMargin(), mapper, 1) {
      bindString(0, key)
    }

    override fun toString(): String = "MercappDatabase.sq:selectSyncState"
  }
}
