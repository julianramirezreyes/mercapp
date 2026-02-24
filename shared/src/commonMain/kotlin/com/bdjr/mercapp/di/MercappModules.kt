package com.bdjr.mercapp.di

import com.bdjr.mercapp.auth.AuthRepository
import com.bdjr.mercapp.auth.AuthLocalDataSource
import com.bdjr.mercapp.auth.AuthRepositoryImpl
import com.bdjr.mercapp.auth.SessionStore
import com.bdjr.mercapp.auth.SupabaseAuthRemoteDataSource
import com.bdjr.mercapp.auth.SupabaseConfig
import com.bdjr.mercapp.auth.usecase.EnsureFreshSession
import com.bdjr.mercapp.auth.usecase.ObserveSession
import com.bdjr.mercapp.auth.usecase.RestoreSession
import com.bdjr.mercapp.auth.usecase.SignIn
import com.bdjr.mercapp.auth.usecase.SignOut
import com.bdjr.mercapp.auth.usecase.SignUp
import com.bdjr.mercapp.data.local.DatabaseDriverFactory
import com.bdjr.mercapp.data.local.MercappLocalDataSource
import com.bdjr.mercapp.data.local.db.MercappDatabase
import com.bdjr.mercapp.data.repository.MercappRepositoryImpl
import com.bdjr.mercapp.domain.repository.MercappRepository
import com.bdjr.mercapp.domain.usecase.ObserveEstablishments
import com.bdjr.mercapp.domain.usecase.ObserveProducts
import com.bdjr.mercapp.domain.usecase.SetProductInShoppingList
import com.bdjr.mercapp.domain.usecase.SoftDeleteEstablishment
import com.bdjr.mercapp.domain.usecase.SoftDeleteProduct
import com.bdjr.mercapp.domain.usecase.UpsertEstablishment
import com.bdjr.mercapp.domain.usecase.UpsertProduct
import com.bdjr.mercapp.network.createHttpClient
import com.bdjr.mercapp.platform.PlatformContext
import com.bdjr.mercapp.sync.SyncManager
import com.bdjr.mercapp.sync.remote.SupabasePostgrestRemoteDataSource
import com.bdjr.mercapp.sync.usecase.RunSync
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

fun mercappSharedModule(platformContext: PlatformContext): Module {
    return module {
        single { platformContext }

        single { DatabaseDriverFactory(get()) }
        single { MercappDatabase(get<DatabaseDriverFactory>().createDriver()) }

        single { Dispatchers.Default }
        single { MercappLocalDataSource(database = get(), dispatcher = get()) }

        single { AuthLocalDataSource(database = get(), dispatcher = get()) }

        single { createHttpClient() }
        single {
            SupabaseAuthRemoteDataSource(
                httpClient = get(),
                supabaseUrl = SupabaseConfig.url,
                anonKey = SupabaseConfig.anonKey,
            )
        }
        single { SessionStore() }
        single { AuthRepositoryImpl(remote = get(), local = get(), sessionStore = get()) } bind AuthRepository::class

        factory { ObserveSession(repository = get()) }
        factory { RestoreSession(repository = get()) }
        factory { EnsureFreshSession(repository = get()) }
        factory { SignIn(repository = get()) }
        factory { SignUp(repository = get()) }
        factory { SignOut(repository = get()) }

        single {
            SupabasePostgrestRemoteDataSource(
                httpClient = get(),
                supabaseUrl = SupabaseConfig.url,
                anonKey = SupabaseConfig.anonKey,
            )
        }
        single { SyncManager(local = get(), remote = get(), sessionStore = get()) }
        factory { RunSync(syncManager = get()) }

        single { MercappRepositoryImpl(local = get()) } bind MercappRepository::class

        factory { ObserveEstablishments(repository = get()) }
        factory { ObserveProducts(repository = get()) }
        factory { UpsertEstablishment(repository = get()) }
        factory { SoftDeleteEstablishment(repository = get()) }
        factory { UpsertProduct(repository = get()) }
        factory { SoftDeleteProduct(repository = get()) }
        factory { SetProductInShoppingList(repository = get()) }
    }
}
