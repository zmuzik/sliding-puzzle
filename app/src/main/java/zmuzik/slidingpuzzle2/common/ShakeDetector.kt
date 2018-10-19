package zmuzik.slidingpuzzle2.common

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import java.lang.ref.WeakReference

class ShakeDetector(private val context: Context) : SensorEventListener {

    private val SHAKE_FORCE_THRESHOLD = 2f
    private val SHAKE_SLOP_TIME_MS = 500

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var listener: WeakReference<OnShakeListener>? = null
    private var shakeTimestamp: Long = 0
    private var isRegistered: Boolean = false

    fun setOnShakeListener(listener: OnShakeListener) {
        this.listener = WeakReference(listener)
    }

    fun register() {
        if (!isRegistered) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            isRegistered = true
        }
    }

    fun unRegister() {
        if (isRegistered) {
            sensorManager.unregisterListener(this)
            isRegistered = false
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        listener?.get()?.let {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val gX = x / SensorManager.GRAVITY_EARTH
            val gY = y / SensorManager.GRAVITY_EARTH
            val gZ = z / SensorManager.GRAVITY_EARTH

            val totalForce = Math.sqrt((gX * gX + gY * gY + gZ * gZ).toDouble())

            if (totalForce > SHAKE_FORCE_THRESHOLD) {
                val now = System.currentTimeMillis()
                if (shakeTimestamp + SHAKE_SLOP_TIME_MS > now) return
                shakeTimestamp = now
                it.onShake()
            }
        }
    }

    interface OnShakeListener {
        fun onShake()
    }
}