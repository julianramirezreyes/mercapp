package com.bdjr.mercapp.auth

import com.bdjr.mercapp.auth.model.PasswordGrantRequest
import com.bdjr.mercapp.auth.model.RefreshTokenGrantRequest
import com.bdjr.mercapp.auth.model.SignUpRequest
import com.bdjr.mercapp.auth.model.SupabaseSessionResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class SupabaseAuthRemoteDataSource(
    private val httpClient: HttpClient,
    private val supabaseUrl: String,
    private val anonKey: String,
) {
    suspend fun signUp(email: String, password: String): SupabaseSessionResponse {
        return httpClient.post("$supabaseUrl/auth/v1/signup") {
            header("apikey", anonKey)
            header(HttpHeaders.Authorization, "Bearer $anonKey")
            contentType(ContentType.Application.Json)
            setBody(SignUpRequest(email = email, password = password))
        }.body()
    }

    suspend fun signInWithPassword(email: String, password: String): SupabaseSessionResponse {
        return httpClient.post("$supabaseUrl/auth/v1/token?grant_type=password") {
            header("apikey", anonKey)
            header(HttpHeaders.Authorization, "Bearer $anonKey")
            contentType(ContentType.Application.Json)
            setBody(PasswordGrantRequest(email = email, password = password))
        }.body()
    }

    suspend fun refreshSession(refreshToken: String): SupabaseSessionResponse {
        return httpClient.post("$supabaseUrl/auth/v1/token?grant_type=refresh_token") {
            header("apikey", anonKey)
            header(HttpHeaders.Authorization, "Bearer $anonKey")
            contentType(ContentType.Application.Json)
            setBody(RefreshTokenGrantRequest(refreshToken = refreshToken))
        }.body()
    }
}
