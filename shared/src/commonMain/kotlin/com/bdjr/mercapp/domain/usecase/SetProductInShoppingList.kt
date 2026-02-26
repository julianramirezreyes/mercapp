package com.bdjr.mercapp.domain.usecase

import com.bdjr.mercapp.domain.repository.MercappRepository

class SetProductInShoppingList(
    private val repository: MercappRepository,
) {
    suspend operator fun invoke(
        id: String,
        isInShoppingList: Boolean,
        shoppingDetail: String?,
        now: Long,
    ) {
        repository.setProductInShoppingList(
            id = id,
            isInShoppingList = isInShoppingList,
            shoppingDetail = shoppingDetail,
            now = now,
        )
    }
}
