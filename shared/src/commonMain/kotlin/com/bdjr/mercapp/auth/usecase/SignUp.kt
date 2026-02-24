package com.bdjr.mercapp.auth.usecase

import com.bdjr.mercapp.auth.AuthRepository

class SignUp(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String) {
        repository.signUp(email = email, password = password)
    }
}
