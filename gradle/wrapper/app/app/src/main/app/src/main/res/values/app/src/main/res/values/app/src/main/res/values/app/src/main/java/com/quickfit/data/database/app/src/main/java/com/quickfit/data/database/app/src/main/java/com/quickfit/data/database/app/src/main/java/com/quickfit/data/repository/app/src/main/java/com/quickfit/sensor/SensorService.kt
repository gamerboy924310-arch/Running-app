package com.quickfit.sensor

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class SensorService(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    // Live Data Streams
    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps.asStateFlow()

    private val _heartRate = MutableStateFlow(0)
    val heartRate: StateFlow<Int> = _heartRate.asStateFlow()

    private val _speed = MutableStateFlow(0f)
    val speed: StateFlow<Float> = _speed.asStateFlow()

    private val _distance = MutableStateFlow(0f)
    val distance: StateFlow<Float> = _distance.asStateFlow()

    private var lastLocation: Location? = null
    private var totalDistance = 0f
    private var isTracking = false
    private var baseStepCount = 0L
    private var stepCounterInitialized = false

    // Fallback simulation
    private var useSimulation = false
    private var simSteps = 0
    private var simBpm = 80
    private var simSpeed = 0f
    private var simDist = 0f

    init {
        startSensors()
    }

    fun startTracking() {
        isTracking = true
        totalDistance = 0f
        lastLocation = null
        _distance.value = 0f
        _speed.value = 0f
        if (useSimulation) {
            simSteps = 0
            simDist = 0f
            simSpeed = 0f
        }
    }

    fun stopTracking() {
        isTracking = false
    }

    private fun startSensors() {
        // Step Counter
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor != null && context.checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            useSimulation = true
            startSimulation()
        }

        // Heart Rate
        val heartSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        if (heartSensor != null && context.checkSelfPermission(Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED) {
            sensorManager.registerListener(this, heartSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

        // GPS
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(
                com.google.android.gms.location.LocationRequest.Builder(
                    com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 1000
                ).setMinUpdateIntervalMillis(500).build(),
                locationCallback,
                null
            )
        }
    }

    private val locationCallback = object : com.google.android.gms.location.LocationCallback() {
        override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
            if (!isTracking) return
            val location = locationResult.lastLocation ?: return
            if (lastLocation != null) {
                val distanceMeters = location.distanceTo(lastLocation!!)
                totalDistance += distanceMeters / 1000f
                _distance.value = totalDistance

                val timeDiff = (location.time - lastLocation!!.time) / 1000.0
                if (timeDiff > 0) {
                    val speedKmh = (distanceMeters / 1000f) / (timeDiff / 3600f)
                    _speed.value = speedKmh.toFloat()
                }
            }
            lastLocation = location
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                if (!stepCounterInitialized) {
                    baseStepCount = event.values[0].toLong()
                    stepCounterInitialized = true
                }
                val totalSteps = event.values[0].toInt() - baseStepCount.toInt()
                _steps.value = totalSteps.coerceAtLeast(0)
            }
            Sensor.TYPE_HEART_RATE -> {
                val hr = event.values[0].toInt()
                if (hr > 0 && hr < 300) {
                    _heartRate.value = hr
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun startSimulation() {
        scope.launch {
            while (true) {
                delay(1000)
                if (isTracking) {
                    simSteps += Random.nextInt(1, 5)
                    _steps.value = simSteps
                    simDist += Random.nextFloat() * 0.01f
                    _distance.value = simDist
                    simSpeed = simDist / (simSteps * 0.002f)
                    _speed.value = simSpeed.coerceIn(0f, 25f)
                    simBpm += Random.nextInt(-5, 8)
                    simBpm = simBpm.coerceIn(60, 180)
                    _heartRate.value = simBpm
                } else {
                    simBpm += Random.nextInt(-3, 3)
                    simBpm = simBpm.coerceIn(55, 180)
                    _heartRate.value = simBpm
                    simSpeed += Random.nextFloat() - 0.5f
                    simSpeed = simSpeed.coerceIn(0f, 25f)
                    _speed.value = simSpeed
                }
            }
        }
    }

    fun cleanup() {
        sensorManager.unregisterListener(this)
        fusedLocationClient.removeLocationUpdates(locationCallback)
        scope.cancel()
    }
}
