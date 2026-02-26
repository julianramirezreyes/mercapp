package com.bdjr.mercapp.domain.model

data class Product(
    val id: String,
    val name: String,
    val establishmentId: String,
    val isInShoppingList: Boolean,
    val shoppingDetail: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val isDirty: Boolean,
    val isDeleted: Boolean,
)
