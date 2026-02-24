package com.bdjr.mercapp.di

import com.bdjr.mercapp.platform.PlatformContext
import org.koin.core.KoinApplication

expect fun initKoin(platformContext: PlatformContext): KoinApplication
