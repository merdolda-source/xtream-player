// Android/app/src/main/kotlin/com/xtream/player/data/local/dao/WatchHistoryDao.kt
package xtream.emin.player.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xtream.emin.player.data.local.entities.WatchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchHistoryDao {

    @Query("SELECT * FROM watch_history ORDER BY watchedAt DESC LIMIT 200")
    fun observeHistory(): Flow<List<WatchHistoryEntity>>

    @Query("SELECT * FROM watch_history ORDER BY watchedAt DESC LIMIT 200")
    suspend fun getHistory(): List<WatchHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: WatchHistoryEntity)

    @Query("DELETE FROM watch_history WHERE streamId = :streamId")
    suspend fun deleteByStreamId(streamId: String)

    @Query("DELETE FROM watch_history")
    suspend fun clear()
}
