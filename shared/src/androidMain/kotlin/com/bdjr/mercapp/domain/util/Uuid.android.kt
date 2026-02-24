package com.bdjr.mercapp.domain.util

actual object Uuid {
    actual fun random(): String = java.util.UUID.randomUUID().toString()
}
