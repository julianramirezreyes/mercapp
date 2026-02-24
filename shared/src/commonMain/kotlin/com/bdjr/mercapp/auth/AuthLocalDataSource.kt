package com.bdjr.mercapp.auth

import com.bdjr.mercapp.data.local.db.MercappDatabase
import com.bdjr.mercapp.data.local.db.Auth_session
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class AuthLocalDataSource(
    private val database: MercappDatabase,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend fun getSession(): Auth_session? {
        return withContext(dispatcher) {
            database.mercappDatabaseQueries
                .selectAuthSession()
                .executeAsOneOrNull()
        }
    }

    suspend fun upsertSession(
        accessToken: String,
        refreshToken: String,
        tokenType: String,
        expiresAt: Long,
        userId: String,
        email: String?,
    ) {
        withContext(dispatcher) {
            database.mercappDatabaseQueries.upsertAuthSession(
                accessToken = accessToken,
                refreshToken = refreshToken,
                tokenType = tokenType,
                expiresAt = expiresAt,
                userId = userId,
                email = email,
            )
        }
    }

    suspend fun clearSession() {
        withContext(dispatcher) {
            database.mercappDatabaseQueries.clearAuthSession()
        }
    }
}
