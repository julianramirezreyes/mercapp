package com.bdjr.mercapp.auth

import com.bdjr.mercapp.auth.model.AuthSession
import com.bdjr.mercapp.auth.model.toAuthSession
import com.bdjr.mercapp.util.currentTimeMillis
import kotlinx.coroutines.flow.StateFlow

class AuthRepositoryImpl(
    private val remote: SupabaseAuthRemoteDataSource,
    private val local: AuthLocalDataSource,
    private val sessionStore: SessionStore,
) : AuthRepository {

    override val session: StateFlow<AuthSession?> = sessionStore.session

    override suspend fun restore() {
        if (sessionStore.session.value != null) return

        val stored = local.getSession() ?: return
        sessionStore.set(
            AuthSession(
                accessToken = stored.access_token,
                refreshToken = stored.refresh_token,
                tokenType = stored.token_type,
                expiresAt = stored.expires_at,
                userId = stored.user_id,
                email = stored.email,
            ),
        )
    }

    override suspend fun signUp(email: String, password: String): AuthSession {
        val session = remote.signUp(email = email, password = password).toAuthSession()
        local.upsertSession(
            accessToken = session.accessToken,
            refreshToken = session.refreshToken,
            tokenType = session.tokenType,
            expiresAt = session.expiresAt,
            userId = session.userId,
            email = session.email,
        )
        sessionStore.set(session)
        return session
    }

    override suspend fun signIn(email: String, password: String): AuthSession {
        val session = remote.signInWithPassword(email = email, password = password).toAuthSession()
        local.upsertSession(
            accessToken = session.accessToken,
            refreshToken = session.refreshToken,
            tokenType = session.tokenType,
            expiresAt = session.expiresAt,
            userId = session.userId,
            email = session.email,
        )
        sessionStore.set(session)
        return session
    }

    override suspend fun refresh(): AuthSession? {
        val current = sessionStore.session.value ?: return null
        val refreshed = remote.refreshSession(refreshToken = current.refreshToken).toAuthSession()
        local.upsertSession(
            accessToken = refreshed.accessToken,
            refreshToken = refreshed.refreshToken,
            tokenType = refreshed.tokenType,
            expiresAt = refreshed.expiresAt,
            userId = refreshed.userId,
            email = refreshed.email,
        )
        sessionStore.set(refreshed)
        return refreshed
    }

    override suspend fun ensureFresh(minValidityMs: Long) {
        val current = sessionStore.session.value ?: return
        val now = currentTimeMillis()
        val remaining = current.expiresAt - now
        if (remaining > minValidityMs) return

        refresh()
    }

    override suspend fun signOut() {
        sessionStore.clear()
        local.clearSession()
    }
}
