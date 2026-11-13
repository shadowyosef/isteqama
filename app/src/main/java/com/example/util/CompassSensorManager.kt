package com.example.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

class CompassSensorManager(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val accelSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val _azimuthFlow = MutableStateFlow(0f)
    val azimuthFlow = _azimuthFlow.asStateFlow()

    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)
    private var hasGravity = false
    private var hasGeomagnetic = false

    fun startListening() {
        if (rotationSensor != null) {
            sensorManager?.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            accelSensor?.let { sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
            magnetSensor?.let { sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        }
    }

    fun stopListening() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            val orientation = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientation)
            var azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
            if (azimuth < 0) azimuth += 360f
            smoothUpdate(azimuth)
        } else {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(event.values, 0, gravity, 0, 3)
                hasGravity = true
            } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(event.values, 0, geomagnetic, 0, 3)
                hasGeomagnetic = true
            }
            if (hasGravity && hasGeomagnetic) {
                val r = FloatArray(9)
                val i = FloatArray(9)
                if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(r, orientation)
                    var azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    if (azimuth < 0) azimuth += 360f
                    smoothUpdate(azimuth)
                }
            }
        }
    }

    private fun smoothUpdate(newAzimuth: Float) {
        val cur = _azimuthFlow.value
        val diff = ((newAzimuth - cur + 540) % 360) - 180
        val smoothed = (cur + diff * 0.25f + 360) % 360
        _azimuthFlow.value = smoothed
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
