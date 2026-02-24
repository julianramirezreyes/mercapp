package com.bdjr.mercapp.di

import com.bdjr.mercapp.platform.PlatformContext
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

actual fun initKoin(platformContext: PlatformContext): KoinApplication {
    return startKoin {
        modules(mercappSharedModule(platformContext))
    }
}
