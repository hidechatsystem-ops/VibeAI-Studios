package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CreationDao {
    @Query("SELECT * FROM creations ORDER BY timestamp DESC")
    fun getAllCreations(): Flow<List<CreationEntity>>

    @Query("SELECT * FROM creations WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteCreations(): Flow<List<CreationEntity>>

    @Query("SELECT * FROM creations WHERE prompt LIKE '%' || :query || '%' OR title LIKE '%' || :query || '%' OR generatedContent LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchCreations(query: String): Flow<List<CreationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreation(creation: CreationEntity): Long

    @Update
    suspend fun updateCreation(creation: CreationEntity)

    @Query("DELETE FROM creations WHERE id = :id")
    suspend fun deleteCreationById(id: Long)

    @Query("UPDATE creations SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Query("DELETE FROM creations")
    suspend fun clearAll()
}
