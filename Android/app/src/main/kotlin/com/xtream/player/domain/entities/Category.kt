// Android/app/src/main/kotlin/com/xtream/player/domain/entities/Category.kt
package com.xtream.player.domain.entities

/**
 * A content category (e.g. live channel group, VOD genre) as returned by
 * the Xtream Codes get_*_categories actions.
 */
data class Category(
    val categoryId: String,
    val categoryName: String,
    val parentId: String? = null
)
