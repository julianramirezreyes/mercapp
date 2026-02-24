package com.bdjr.mercapp.auth.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.bdjr.mercapp.util.currentTimeMillis

@Serializable
data class SupabaseUser(
    val id: String,
    val email: String? = null,
)

@Serializable
data class SupabaseSessionResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Long,
    @SerialName("refresh_token") val refreshToken: String,
    val user: SupabaseUser,
)

@Serializable
data class SignUpRequest(
    val email: String,
    val password: String,
)

@Serializable
data class PasswordGrantRequest(
    val email: String,
    val password: String,
)

@Serializable
data class RefreshTokenGrantRequest(
    @SerialName("refresh_token") val refreshToken: String,
)

@Serializable
data class AuthSession(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresAt: Long,
    val userId: String,
    val email: String? = null,
)

fun SupabaseSessionResponse.toAuthSession(): AuthSession {
    val now = currentTimeMillis()
    return AuthSession(
        accessToken = accessToken,
        refreshToken = refreshToken,
        tokenType = tokenType,
        expiresAt = now + (expiresIn * 1000),
        userId = user.id,
        email = user.email,
    )
}
