package com.bdjr.mercapp.domain.usecase

import com.bdjr.mercapp.domain.repository.MercappRepository

class SoftDeleteEstablishment(
    private val repository: MercappRepository,
) {
    suspend operator fun invoke(
        id: String,
        now: Long,
    ) {
        repository.softDeleteEstablishment(
            id = id,
            now = now,
        )
    }
}
