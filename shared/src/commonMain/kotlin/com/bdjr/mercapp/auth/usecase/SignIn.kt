package com.bdjr.mercapp.auth.usecase

import com.bdjr.mercapp.auth.AuthRepository

class SignIn(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String) {
        repository.signIn(email = email, password = password)
    }
}
