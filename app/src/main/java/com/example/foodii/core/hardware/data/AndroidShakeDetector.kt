package com.example.foodii.core.hardware.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.foodii.core.hardware.domain.ShakeDetector
import kotlin.math.sqrt

class AndroidShakeDetector(context: Context) : ShakeDetector, SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var onShakeListener: (() -> Unit)? = null

    private var lastShakeTime: Long = 0
    private val shakeThreshold = 12.0f

    override fun startListening(onShake: () -> Unit) {
        onShakeListener = onShake
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun stopListening() {
        sensorManager.unregisterListener(this)
        onShakeListener = null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val acceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat() - SensorManager.GRAVITY_EARTH
            val currentTime = System.currentTimeMillis()

            if (acceleration > shakeThreshold) {
                if (currentTime - lastShakeTime > 1000) {
                    lastShakeTime = currentTime
                    onShakeListener?.invoke()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
