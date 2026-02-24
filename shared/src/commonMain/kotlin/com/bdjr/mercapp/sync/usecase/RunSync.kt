package com.bdjr.mercapp.sync.usecase

import com.bdjr.mercapp.sync.SyncManager

class RunSync(
    private val syncManager: SyncManager,
) {
    suspend operator fun invoke() {
        syncManager.sync()
    }
}
