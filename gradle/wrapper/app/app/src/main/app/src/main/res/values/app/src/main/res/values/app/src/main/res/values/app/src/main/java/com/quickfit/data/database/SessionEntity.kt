package com.quickfit.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val activity: String,
    val startTime: Long,
    val durationSeconds: Int,
    val distanceKm: Double,
    val speedKmh: Double,
    val calories: Int,
    val bpmAvg: Int,
    val steps: Int
) {
    fun toSession(): Session {
        return Session(
            id = id,
            activity = activity,
            startTime = Date(startTime),
            durationSeconds = durationSeconds,
            distanceKm = distanceKm,
            speedKmh = speedKmh,
            calories = calories,
            bpmAvg = bpmAvg,
            steps = steps
        )
    }

    companion object {
        fun fromSession(session: Session): SessionEntity {
            return SessionEntity(
                id = session.id,
                activity = session.activity,
                startTime = session.startTime.time,
                durationSeconds = session.durationSeconds,
                distanceKm = session.distanceKm,
                speedKmh = session.speedKmh,
                calories = session.calories,
                bpmAvg = session.bpmAvg,
                steps = session.steps
            )
        }
    }
}

data class Session(
    val id: Long = 0,
    val activity: String,
    val startTime: Date,
    val durationSeconds: Int,
    val distanceKm: Double,
    val speedKmh: Double,
    val calories: Int,
    val bpmAvg: Int,
    val steps: Int
) {
    val durationStr: String
        get() {
            val h = durationSeconds / 3600
            val m = (durationSeconds % 3600) / 60
            val s = durationSeconds % 60
            return when {
                h > 0 -> "${h}h ${m}m"
                m > 0 -> "${m}m ${s}s"
                else -> "${s}s"
            }
        }

    val timeAgo: String
        get() {
            val diff = System.currentTimeMillis() - startTime.time
            val sec = diff / 1000
            return when {
                sec < 60 -> "${sec}s ago"
                sec < 3600 -> "${sec / 60}m ago"
                sec < 86400 -> "${sec / 3600}h ago"
                else -> "${sec / 86400}d ago"
            }
        }

    val pace: String
        get() {
            if (distanceKm > 0 && durationSeconds > 0) {
                val paceMin = (durationSeconds / 60.0) / distanceKm
                val mins = paceMin.toInt()
                val secs = ((paceMin - mins) * 60).toInt()
                return String.format("%d:%02d/km", mins, secs)
            }
            return "--:--/km"
        }
}
