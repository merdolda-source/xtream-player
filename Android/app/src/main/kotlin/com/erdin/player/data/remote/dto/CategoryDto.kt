// Android/app/src/main/kotlin/com/xtream/player/data/remote/dto/CategoryDto.kt
package com.erdin.player.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.erdin.player.domain.entities.Category

/** Response item shape for `get_live_categories` / `get_vod_categories` / `get_series_categories`. */
data class CategoryDto(
    @SerializedName("category_id") val categoryId: String? = null,
    @SerializedName("category_name") val categoryName: String? = null,
    @SerializedName("parent_id") val parentId: Int? = null
)

fun CategoryDto.toDomain(): Category = Category(
    categoryId = categoryId.orEmpty(),
    categoryName = categoryName.orEmpty(),
    parentId = parentId?.toString()
)
