package com.bdjr.mercapp.data.repository

import com.bdjr.mercapp.data.local.MercappLocalDataSource
import com.bdjr.mercapp.data.local.toDomain
import com.bdjr.mercapp.domain.model.Establishment
import com.bdjr.mercapp.domain.model.Product
import com.bdjr.mercapp.domain.repository.MercappRepository
import com.bdjr.mercapp.domain.util.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MercappRepositoryImpl(
    private val local: MercappLocalDataSource,
) : MercappRepository {

    override fun observeEstablishments(includeDeleted: Boolean): Flow<List<Establishment>> {
        return local.observeEstablishments(includeDeleted)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun observeProducts(
        establishmentId: String,
        includeDeleted: Boolean,
    ): Flow<List<Product>> {
        return local.observeProducts(establishmentId, includeDeleted)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun upsertEstablishment(
        id: String?,
        name: String,
        now: Long,
    ): String {
        val resolvedId = id ?: Uuid.random()
        val createdAt = if (id == null) {
            now
        } else {
            local.getEstablishmentById(resolvedId)?.created_at ?: now
        }

        local.upsertEstablishment(
            id = resolvedId,
            name = name,
            createdAt = createdAt,
            updatedAt = now,
            isDirty = true,
            isDeleted = false,
        )

        return resolvedId
    }

    override suspend fun softDeleteEstablishment(
        id: String,
        now: Long,
    ) {
        local.markEstablishmentDeleted(
            id = id,
            updatedAt = now,
        )
    }

    override suspend fun upsertProduct(
        id: String?,
        establishmentId: String,
        name: String,
        isInShoppingList: Boolean,
        now: Long,
    ): String {
        val resolvedId = id ?: Uuid.random()
        val createdAt = if (id == null) {
            now
        } else {
            local.getProductById(resolvedId)?.created_at ?: now
        }

        local.upsertProduct(
            id = resolvedId,
            establishmentId = establishmentId,
            name = name,
            isInShoppingList = isInShoppingList,
            createdAt = createdAt,
            updatedAt = now,
            isDirty = true,
            isDeleted = false,
        )

        return resolvedId
    }

    override suspend fun setProductInShoppingList(
        id: String,
        isInShoppingList: Boolean,
        now: Long,
    ) {
        local.setProductInShoppingList(
            id = id,
            isInShoppingList = isInShoppingList,
            updatedAt = now,
        )
    }

    override suspend fun softDeleteProduct(
        id: String,
        now: Long,
    ) {
        local.markProductDeleted(
            id = id,
            updatedAt = now,
        )
    }
}
