package com.bdjr.mercapp.domain.usecase

import com.bdjr.mercapp.domain.model.Product
import com.bdjr.mercapp.domain.repository.MercappRepository
import kotlinx.coroutines.flow.Flow

class ObserveProducts(
    private val repository: MercappRepository,
) {
    operator fun invoke(
        establishmentId: String,
        includeDeleted: Boolean = false,
    ): Flow<List<Product>> {
        return repository.observeProducts(
            establishmentId = establishmentId,
            includeDeleted = includeDeleted,
        )
    }
}
