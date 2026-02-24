package com.bdjr.mercapp.auth

import com.bdjr.mercapp.auth.model.AuthSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionStore {
    private val _session = MutableStateFlow<AuthSession?>(null)
    val session: StateFlow<AuthSession?> = _session.asStateFlow()

    fun set(session: AuthSession?) {
        _session.value = session
    }

    fun clear() {
        _session.value = null
    }
}
