// Android/app/src/main/kotlin/com/xtream/player/data/local/db/AppDatabase.kt
package xtream.emin.player.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import xtream.emin.player.data.local.dao.FavoriteDao
import xtream.emin.player.data.local.dao.WatchHistoryDao
import xtream.emin.player.data.local.entities.FavoriteEntity
import xtream.emin.player.data.local.entities.WatchHistoryEntity

@Database(
    entities = [FavoriteEntity::class, WatchHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun watchHistoryDao(): WatchHistoryDao

    companion object {
        const val DATABASE_NAME = "xtream_player.db"
    }
}
