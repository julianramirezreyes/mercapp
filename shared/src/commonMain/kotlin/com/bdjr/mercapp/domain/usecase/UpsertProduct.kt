package com.bdjr.mercapp.domain.usecase

import com.bdjr.mercapp.domain.repository.MercappRepository

class UpsertProduct(
    private val repository: MercappRepository,
) {
    suspend operator fun invoke(
        id: String?,
        establishmentId: String,
        name: String,
        isInShoppingList: Boolean,
        now: Long,
    ): String {
        return repository.upsertProduct(
            id = id,
            establishmentId = establishmentId,
            name = name,
            isInShoppingList = isInShoppingList,
            now = now,
        )
    }
}
