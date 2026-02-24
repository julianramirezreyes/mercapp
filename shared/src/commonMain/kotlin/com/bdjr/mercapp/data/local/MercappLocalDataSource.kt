package com.bdjr.mercapp.data.local

import com.bdjr.mercapp.data.local.db.MercappDatabase
import com.bdjr.mercapp.data.local.db.Products
import com.bdjr.mercapp.data.local.db.Establishments
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList

class MercappLocalDataSource(
    private val database: MercappDatabase,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend fun getSyncState(key: String): Long? {
        return withContext(dispatcher) {
            database.mercappDatabaseQueries
                .selectSyncState(key = key)
                .executeAsOneOrNull()
        }
    }

    suspend fun setSyncState(key: String, value: Long) {
        withContext(dispatcher) {
            database.mercappDatabaseQueries.upsertSyncState(
                key = key,
                value = value,
            )
        }
    }

    suspend fun getEstablishmentById(id: String): Establishments? {
        return withContext(dispatcher) {
            database.mercappDatabaseQueries
                .selectEstablishmentById(id = id)
                .executeAsOneOrNull()
        }
    }

    suspend fun getProductById(id: String): Products? {
        return withContext(dispatcher) {
            database.mercappDatabaseQueries
                .selectProductById(id = id)
                .executeAsOneOrNull()
        }
    }

    suspend fun getDirtyEstablishments(): List<Establishments> {
        return withContext(dispatcher) {
            database.mercappDatabaseQueries
                .selectDirtyEstablishments()
                .executeAsList()
        }
    }

    suspend fun getDirtyProducts(): List<Products> {
        return withContext(dispatcher) {
            database.mercappDatabaseQueries
                .selectDirtyProducts()
                .executeAsList()
        }
    }

    fun observeEstablishments(includeDeleted: Boolean): Flow<List<Establishments>> {
        return database.mercappDatabaseQueries
            .selectAllEstablishments(includeDeleted = includeDeleted)
            .asFlow()
            .mapToList(dispatcher)
    }

    fun observeProducts(
        establishmentId: String,
        includeDeleted: Boolean,
    ): Flow<List<Products>> {
        return database.mercappDatabaseQueries
            .selectProductsByEstablishment(
                establishmentId = establishmentId,
                includeDeleted = includeDeleted,
            )
            .asFlow()
            .mapToList(dispatcher)
    }

    fun upsertEstablishment(
        id: String,
        name: String,
        createdAt: Long,
        updatedAt: Long,
        isDirty: Boolean,
        isDeleted: Boolean,
    ) {
        database.transaction {
            database.mercappDatabaseQueries.insertEstablishmentIfAbsent(
                id = id,
                name = name,
                createdAt = createdAt,
                updatedAt = updatedAt,
                isDirty = isDirty,
                isDeleted = isDeleted,
            )
            database.mercappDatabaseQueries.updateEstablishment(
                id = id,
                name = name,
                updatedAt = updatedAt,
                isDirty = isDirty,
                isDeleted = isDeleted,
            )
        }
    }

    fun markEstablishmentClean(
        id: String,
        updatedAt: Long,
    ) {
        database.mercappDatabaseQueries.markEstablishmentClean(
            id = id,
            updatedAt = updatedAt,
        )
    }

    fun markEstablishmentDeleted(
        id: String,
        updatedAt: Long,
    ) {
        database.mercappDatabaseQueries.markEstablishmentDeleted(
            id = id,
            updatedAt = updatedAt,
        )
    }

    fun upsertProduct(
        id: String,
        establishmentId: String,
        name: String,
        isInShoppingList: Boolean,
        createdAt: Long,
        updatedAt: Long,
        isDirty: Boolean,
        isDeleted: Boolean,
    ) {
        database.transaction {
            database.mercappDatabaseQueries.insertProductIfAbsent(
                id = id,
                establishmentId = establishmentId,
                name = name,
                isInShoppingList = isInShoppingList,
                createdAt = createdAt,
                updatedAt = updatedAt,
                isDirty = isDirty,
                isDeleted = isDeleted,
            )
            database.mercappDatabaseQueries.updateProduct(
                id = id,
                establishmentId = establishmentId,
                name = name,
                isInShoppingList = isInShoppingList,
                updatedAt = updatedAt,
                isDirty = isDirty,
                isDeleted = isDeleted,
            )
        }
    }

    fun markProductClean(
        id: String,
        updatedAt: Long,
    ) {
        database.mercappDatabaseQueries.markProductClean(
            id = id,
            updatedAt = updatedAt,
        )
    }

    fun setProductInShoppingList(
        id: String,
        isInShoppingList: Boolean,
        updatedAt: Long,
    ) {
        database.mercappDatabaseQueries.setProductInShoppingList(
            id = id,
            isInShoppingList = isInShoppingList,
            updatedAt = updatedAt,
        )
    }

    fun markProductDeleted(
        id: String,
        updatedAt: Long,
    ) {
        database.mercappDatabaseQueries.markProductDeleted(
            id = id,
            updatedAt = updatedAt,
        )
    }
}
