package com.bdjr.mercapp.data.local

import com.bdjr.mercapp.data.local.db.Establishments
import com.bdjr.mercapp.data.local.db.Products
import com.bdjr.mercapp.domain.model.Establishment
import com.bdjr.mercapp.domain.model.Product

internal fun Establishments.toDomain(): Establishment {
    return Establishment(
        id = id,
        name = name,
        createdAt = created_at,
        updatedAt = updated_at,
        isDirty = is_dirty,
        isDeleted = is_deleted,
    )
}

internal fun Products.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        establishmentId = establishment_id,
        isInShoppingList = is_in_shopping_list,
        shoppingDetail = shopping_detail,
        createdAt = created_at,
        updatedAt = updated_at,
        isDirty = is_dirty,
        isDeleted = is_deleted,
    )
}
