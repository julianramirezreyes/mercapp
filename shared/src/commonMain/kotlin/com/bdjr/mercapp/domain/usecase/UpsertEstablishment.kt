package com.bdjr.mercapp.domain.usecase

import com.bdjr.mercapp.domain.repository.MercappRepository

class UpsertEstablishment(
    private val repository: MercappRepository,
) {
    suspend operator fun invoke(
        id: String?,
        name: String,
        now: Long,
    ): String {
        return repository.upsertEstablishment(id = id, name = name, now = now)
    }
}
