package com.bdjr.mercapp.domain.repository

import com.bdjr.mercapp.domain.model.Establishment
import com.bdjr.mercapp.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface MercappRepository {
    fun observeEstablishments(includeDeleted: Boolean = false): Flow<List<Establishment>>

    fun observeProducts(
        establishmentId: String,
        includeDeleted: Boolean = false,
    ): Flow<List<Product>>

    suspend fun upsertEstablishment(
        id: String?,
        name: String,
        now: Long,
    ): String

    suspend fun softDeleteEstablishment(
        id: String,
        now: Long,
    )

    suspend fun upsertProduct(
        id: String?,
        establishmentId: String,
        name: String,
        isInShoppingList: Boolean,
        now: Long,
    ): String

    suspend fun setProductInShoppingList(
        id: String,
        isInShoppingList: Boolean,
        shoppingDetail: String?,
        now: Long,
    )

    suspend fun softDeleteProduct(
        id: String,
        now: Long,
    )
}
