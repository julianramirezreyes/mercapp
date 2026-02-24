package com.bdjr.mercapp.domain.usecase

import com.bdjr.mercapp.domain.repository.MercappRepository

class SoftDeleteProduct(
    private val repository: MercappRepository,
) {
    suspend operator fun invoke(
        id: String,
        now: Long,
    ) {
        repository.softDeleteProduct(
            id = id,
            now = now,
        )
    }
}
