package com.bdjr.mercapp.auth.usecase

import com.bdjr.mercapp.auth.AuthRepository

class EnsureFreshSession(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(minValidityMs: Long = 60_000) {
        repository.ensureFresh(minValidityMs = minValidityMs)
    }
}
