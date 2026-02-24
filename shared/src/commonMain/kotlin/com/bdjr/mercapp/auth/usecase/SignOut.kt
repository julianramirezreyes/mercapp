package com.bdjr.mercapp.auth.usecase

import com.bdjr.mercapp.auth.AuthRepository

class SignOut(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke() {
        repository.signOut()
    }
}
