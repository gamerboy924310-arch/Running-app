package com.quickfit.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions ORDER BY startTime DESC LIMIT 5")
    fun getRecentSessions(): Flow<List<SessionEntity>>

    @Insert
    suspend fun insertSession(session: SessionEntity)

    @Query("DELETE FROM sessions")
    suspend fun deleteAll()
}
