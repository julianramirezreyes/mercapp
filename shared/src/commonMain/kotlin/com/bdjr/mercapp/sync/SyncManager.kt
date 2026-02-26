package com.bdjr.mercapp.sync

import com.bdjr.mercapp.auth.SessionStore
import com.bdjr.mercapp.data.local.MercappLocalDataSource
import com.bdjr.mercapp.sync.remote.RemoteEstablishment
import com.bdjr.mercapp.sync.remote.RemoteProduct
import com.bdjr.mercapp.sync.remote.SupabasePostgrestRemoteDataSource
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SyncManager(
    private val local: MercappLocalDataSource,
    private val remote: SupabasePostgrestRemoteDataSource,
    private val sessionStore: SessionStore,
) {
    private val mutex = Mutex()

    suspend fun sync() {
        mutex.withLock {
            val session = sessionStore.session.value ?: return

            pushDirty(session.accessToken, session.userId)
            pullChanges(session.accessToken)
        }
    }

    private suspend fun pushDirty(accessToken: String, userId: String) {
        val dirtyEstablishments = local.getDirtyEstablishments()
        val dirtyProducts = local.getDirtyProducts()

        if (dirtyEstablishments.isNotEmpty()) {
            val payload = dirtyEstablishments.map {
                RemoteEstablishment(
                    id = it.id,
                    userId = userId,
                    name = it.name,
                    createdAt = it.created_at,
                    updatedAt = it.updated_at,
                    isDeleted = it.is_deleted,
                )
            }
            val saved = remote.upsertEstablishments(accessToken = accessToken, items = payload)
            saved.forEach { item ->
                local.markEstablishmentClean(id = item.id, updatedAt = item.updatedAt)
            }
        }

        if (dirtyProducts.isNotEmpty()) {
            val payload = dirtyProducts.map {
                RemoteProduct(
                    id = it.id,
                    userId = userId,
                    establishmentId = it.establishment_id,
                    name = it.name,
                    isInShoppingList = it.is_in_shopping_list,
                    shoppingDetail = it.shopping_detail,
                    createdAt = it.created_at,
                    updatedAt = it.updated_at,
                    isDeleted = it.is_deleted,
                )
            }
            val saved = remote.upsertProducts(accessToken = accessToken, items = payload)
            saved.forEach { item ->
                local.markProductClean(id = item.id, updatedAt = item.updatedAt)
            }
        }
    }

    private suspend fun pullChanges(accessToken: String) {
        val establishmentsCursorKey = "establishments_last_updated_at"
        val productsCursorKey = "products_last_updated_at"

        val lastEst = local.getSyncState(establishmentsCursorKey) ?: 0L
        val lastProd = local.getSyncState(productsCursorKey) ?: 0L

        val remoteEstablishments = remote.fetchEstablishmentsSince(
            accessToken = accessToken,
            updatedAfter = lastEst,
        )
        val remoteProducts = remote.fetchProductsSince(
            accessToken = accessToken,
            updatedAfter = lastProd,
        )

        var maxEst = lastEst
        remoteEstablishments.forEach { item ->
            applyRemoteEstablishmentLww(item)
            if (item.updatedAt > maxEst) maxEst = item.updatedAt
        }
        if (maxEst != lastEst) {
            local.setSyncState(establishmentsCursorKey, maxEst)
        }

        var maxProd = lastProd
        remoteProducts.forEach { item ->
            applyRemoteProductLww(item)
            if (item.updatedAt > maxProd) maxProd = item.updatedAt
        }
        if (maxProd != lastProd) {
            local.setSyncState(productsCursorKey, maxProd)
        }
    }

    private suspend fun applyRemoteEstablishmentLww(item: RemoteEstablishment) {
        val localRow = local.getEstablishmentById(item.id)
        if (localRow == null || item.updatedAt > localRow.updated_at) {
            local.upsertEstablishment(
                id = item.id,
                name = item.name,
                createdAt = item.createdAt,
                updatedAt = item.updatedAt,
                isDirty = false,
                isDeleted = item.isDeleted,
            )
        }
    }

    private suspend fun applyRemoteProductLww(item: RemoteProduct) {
        val localRow = local.getProductById(item.id)
        if (localRow == null || item.updatedAt > localRow.updated_at) {
            local.upsertProduct(
                id = item.id,
                establishmentId = item.establishmentId,
                name = item.name,
                isInShoppingList = item.isInShoppingList,
                shoppingDetail = item.shoppingDetail,
                createdAt = item.createdAt,
                updatedAt = item.updatedAt,
                isDirty = false,
                isDeleted = item.isDeleted,
            )
        }
    }
}
