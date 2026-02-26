package com.bdjr.mercapp.`data`.local.db

import kotlin.Boolean
import kotlin.Long
import kotlin.String

public data class Products(
  public val id: String,
  public val name: String,
  public val establishment_id: String,
  public val is_in_shopping_list: Boolean,
  public val shopping_detail: String?,
  public val created_at: Long,
  public val updated_at: Long,
  public val is_dirty: Boolean,
  public val is_deleted: Boolean,
)
