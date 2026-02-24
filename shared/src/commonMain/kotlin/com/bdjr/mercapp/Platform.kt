package com.bdjr.mercapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform