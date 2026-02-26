package com.bdjr.mercapp.sync.remote

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteEstablishment(
    val id: String,
    @SerialName("user_id") val userId: String,
    val name: String,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("updated_at") val updatedAt: Long,
    @SerialName("is_deleted") val isDeleted: Boolean,
)

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class RemoteProduct(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("establishment_id") val establishmentId: String,
    val name: String,
    @SerialName("is_in_shopping_list") val isInShoppingList: Boolean,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    @SerialName("shopping_detail") val shoppingDetail: String? = null,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("updated_at") val updatedAt: Long,
    @SerialName("is_deleted") val isDeleted: Boolean,
)
