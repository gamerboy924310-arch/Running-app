package com.quickfit.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickfit.data.database.Session
import com.quickfit.data.repository.FitnessRepository
import com.quickfit.sensor.SensorService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

data class UserProfile(
    val name: String = "Guest",
    val age: Int = 30,
    val weight: Double = 70.0,
    val height: Double = 175.0,
    val dailyStepGoal: Int = 10000,
    val weeklyRunGoal: Double = 20.0
)

data class LiveMetrics(
    val speed: Float = 0f,
    val steps: Int = 0,
    val bpm: Int = 0,
    val distance: Float = 0f
)

data class WorkoutState(
    val active: Boolean = false,
    val paused: Boolean = false,
    val type: String = "Run",
    val elapsedSeconds: Int = 0,
    val distance: Double = 0.0,
    val steps: Int = 0,
    val calories: Int = 0,
    val bpmList: List<Int> = emptyList()
)

class FitnessViewModel(
    private val repository: FitnessRepository
) : ViewModel() {

    private var sensorService: SensorService? = null

    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    private val _metrics = MutableStateFlow(LiveMetrics())
    val metrics: StateFlow<LiveMetrics> = _metrics.asStateFlow()

    private val _workout = MutableStateFlow(WorkoutState())
    val workout: StateFlow<WorkoutState> = _workout.asStateFlow()

    private val _currentScreen = MutableStateFlow("dashboard")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    val allSessions: StateFlow<List<Session>> = repository.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSessions: StateFlow<List<Session>> = repository.getRecentSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalStats: StateFlow<Triple<Int, Double, Int>> = allSessions.map { sessions ->
        Triple(sessions.size, sessions.sumOf { it.distanceKm }, sessions.sumOf { it.calories })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Triple(0, 0.0, 0))

    fun initSensorService(context: Context) {
        sensorService = SensorService(context)
        viewModelScope.launch {
            sensorService?.steps?.collect { steps ->
                _metrics.value = _metrics.value.copy(steps = steps)
                if (_workout.value.active && !_workout.value.paused) {
                    _workout.value = _workout.value.copy(steps = steps)
                }
            }
        }
        viewModelScope.launch {
            sensorService?.heartRate?.collect { bpm ->
                _metrics.value = _metrics.value.copy(bpm = bpm)
                if (_workout.value.active && !_workout.value.paused) {
                    val newList = _workout.value.bpmList + bpm
                    _workout.value = _workout.value.copy(bpmList = newList)
                }
            }
        }
        viewModelScope.launch {
            sensorService?.speed?.collect { speed ->
                _metrics.value = _metrics.value.copy(speed = speed)
            }
        }
        viewModelScope.launch {
            sensorService?.distance?.collect { distance ->
                _metrics.value = _metrics.value.copy(distance = distance)
                if (_workout.value.active && !_workout.value.paused) {
                    _workout.value = _workout.value.copy(distance = distance.toDouble())
                }
            }
        }
    }

    fun setCurrentScreen(screen: String) {
        _currentScreen.value = screen
    }

    fun startWorkout(type: String) {
        sensorService?.startTracking()
        _workout.value = WorkoutState(
            active = true,
            paused = false,
            type = type,
            elapsedSeconds = 0,
            distance = 0.0,
            steps = 0,
            calories = 0,
            bpmList = emptyList()
        )
        _currentScreen.value = "workout"
        viewModelScope.launch {
            while (_workout.value.active) {
                delay(1000)
                if (!_workout.value.paused) {
                    val current = _workout.value
                    val newElapsed = current.elapsedSeconds + 1
                    val cal = (current.distance * 60 * (_profile.value.weight / 70)).toInt()
                    _workout.value = current.copy(
                        elapsedSeconds = newElapsed,
                        calories = cal
                    )
                }
            }
        }
    }

    fun togglePause() {
        val current = _workout.value
        if (current.active) {
            _workout.value = current.copy(paused = !current.paused)
        }
    }

    fun endWorkout() {
        val workout = _workout.value
        if (!workout.active) return

        val avgBpm = if (workout.bpmList.isNotEmpty()) {
            workout.bpmList.average().toInt()
        } else {
            _metrics.value.bpm
        }

        val distance = _metrics.value.distance.toDouble()
        val steps = _metrics.value.steps
        val duration = workout.elapsedSeconds
        val avgSpeed = if (duration > 0) distance / (duration / 3600.0) else 0.0
        val cal = (distance * 60 * (_profile.value.weight / 70)).toInt()

        val session = Session(
            activity = workout.type,
            startTime = Date(System.currentTimeMillis() - duration * 1000L),
            durationSeconds = duration,
            distanceKm = distance,
            speedKmh = avgSpeed,
            calories = cal,
            bpmAvg = avgBpm,
            steps = steps
        )

        viewModelScope.launch {
            repository.insertSession(session)
        }

        sensorService?.stopTracking()
        _workout.value = WorkoutState()
        _currentScreen.value = "dashboard"
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.deleteAll()
            _profile.value = UserProfile()
            _metrics.value = LiveMetrics()
            _workout.value = WorkoutState()
        }
    }

    override fun onCleared() {
        super.onCleared()
        sensorService?.cleanup()
    }
}

class FitnessViewModelFactory(
    private val repository: FitnessRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FitnessViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FitnessViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
