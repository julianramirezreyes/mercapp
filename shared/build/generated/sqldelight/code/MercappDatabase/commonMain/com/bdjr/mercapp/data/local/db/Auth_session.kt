package com.bdjr.mercapp.`data`.local.db

import kotlin.Long
import kotlin.String

public data class Auth_session(
  public val singleton_id: Long,
  public val access_token: String,
  public val refresh_token: String,
  public val token_type: String,
  public val expires_at: Long,
  public val user_id: String,
  public val email: String?,
)
