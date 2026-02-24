package com.bdjr.mercapp.domain.usecase

import com.bdjr.mercapp.domain.model.Establishment
import com.bdjr.mercapp.domain.repository.MercappRepository
import kotlinx.coroutines.flow.Flow

class ObserveEstablishments(
    private val repository: MercappRepository,
) {
    operator fun invoke(includeDeleted: Boolean = false): Flow<List<Establishment>> {
        return repository.observeEstablishments(includeDeleted = includeDeleted)
    }
}
