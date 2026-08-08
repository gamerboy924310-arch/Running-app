package com.quickfit.data.repository

import com.quickfit.data.database.AppDatabase
import com.quickfit.data.database.Session
import com.quickfit.data.database.SessionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FitnessRepository(private val db: AppDatabase) {
    fun getAllSessions(): Flow<List<Session>> {
        return db.sessionDao().getAllSessions().map { entities ->
            entities.map { it.toSession() }
        }
    }

    fun getRecentSessions(): Flow<List<Session>> {
        return db.sessionDao().getRecentSessions().map { entities ->
            entities.map { it.toSession() }
        }
    }

    suspend fun insertSession(session: Session) {
        db.sessionDao().insertSession(SessionEntity.fromSession(session))
    }

    suspend fun deleteAll() {
        db.sessionDao().deleteAll()
    }
}
