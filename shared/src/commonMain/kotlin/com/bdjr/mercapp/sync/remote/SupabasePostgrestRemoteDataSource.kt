package com.bdjr.mercapp.sync.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class SupabasePostgrestRemoteDataSource(
    private val httpClient: HttpClient,
    private val supabaseUrl: String,
    private val anonKey: String,
) {
    private val restBaseUrl: String = "$supabaseUrl/rest/v1"

    suspend fun fetchEstablishmentsSince(
        accessToken: String,
        updatedAfter: Long,
    ): List<RemoteEstablishment> {
        return httpClient.get(
            "$restBaseUrl/mercapp_establishments" +
                "?select=id,user_id,name,created_at,updated_at,is_deleted" +
                "&updated_at=gt.$updatedAfter" +
                "&order=updated_at.asc",
        ) {
            header("apikey", anonKey)
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }.body()
    }

    suspend fun fetchProductsSince(
        accessToken: String,
        updatedAfter: Long,
    ): List<RemoteProduct> {
        return httpClient.get(
            "$restBaseUrl/mercapp_products" +
                "?select=id,user_id,establishment_id,name,is_in_shopping_list,created_at,updated_at,is_deleted" +
                "&updated_at=gt.$updatedAfter" +
                "&order=updated_at.asc",
        ) {
            header("apikey", anonKey)
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }.body()
    }

    suspend fun upsertEstablishments(
        accessToken: String,
        items: List<RemoteEstablishment>,
    ): List<RemoteEstablishment> {
        if (items.isEmpty()) return emptyList()

        return httpClient.post("$restBaseUrl/mercapp_establishments?on_conflict=id") {
            header("apikey", anonKey)
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            header("Prefer", "resolution=merge-duplicates,return=representation")
            contentType(ContentType.Application.Json)
            setBody(items)
        }.body()
    }

    suspend fun upsertProducts(
        accessToken: String,
        items: List<RemoteProduct>,
    ): List<RemoteProduct> {
        if (items.isEmpty()) return emptyList()

        return httpClient.post("$restBaseUrl/mercapp_products?on_conflict=id") {
            header("apikey", anonKey)
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            header("Prefer", "resolution=merge-duplicates,return=representation")
            contentType(ContentType.Application.Json)
            setBody(items)
        }.body()
    }
}
