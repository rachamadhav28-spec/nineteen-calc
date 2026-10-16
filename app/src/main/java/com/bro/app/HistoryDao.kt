package com.bro.app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: HistoryEntity)

    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<HistoryEntity>>
}
