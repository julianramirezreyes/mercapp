package com.bdjr.mercapp.domain.model

data class Establishment(
    val id: String,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isDirty: Boolean,
    val isDeleted: Boolean,
)
