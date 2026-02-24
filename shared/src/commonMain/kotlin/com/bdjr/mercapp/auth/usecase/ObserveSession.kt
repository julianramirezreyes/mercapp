package com.bdjr.mercapp.auth.usecase

import com.bdjr.mercapp.auth.AuthRepository
import com.bdjr.mercapp.auth.model.AuthSession
import kotlinx.coroutines.flow.StateFlow

class ObserveSession(
    private val repository: AuthRepository,
) {
    operator fun invoke(): StateFlow<AuthSession?> = repository.session
}
