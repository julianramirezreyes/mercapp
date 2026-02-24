package com.bdjr.mercapp.auth

import com.bdjr.mercapp.auth.model.AuthSession
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val session: StateFlow<AuthSession?>

    suspend fun restore()
    suspend fun signUp(email: String, password: String): AuthSession
    suspend fun signIn(email: String, password: String): AuthSession
    suspend fun refresh(): AuthSession?
    suspend fun ensureFresh(minValidityMs: Long)
    suspend fun signOut()
}
