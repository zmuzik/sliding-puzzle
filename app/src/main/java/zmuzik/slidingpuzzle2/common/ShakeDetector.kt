package zmuzik.slidingpuzzle2.common

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class ShakeDetector(private val mContext: Context) : SensorEventListener {

    companion object {
        private val SHAKE_FORCE_THRESHOLD = 2f
        private val SHAKE_SLOP_TIME_MS = 500
    }

    private val mSensorManager: SensorManager
    private val mAccelerometer: Sensor
    private var mListener: OnShakeListener? = null
    private var mShakeTimestamp: Long = 0
    private var mIsRegistered: Boolean = false

    init {
        mSensorManager = mContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    fun setOnShakeListener(listener: OnShakeListener) {
        this.mListener = listener
    }

    fun register() {
        if (!mIsRegistered) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI)
            mIsRegistered = true
        }
    }

    fun unRegister() {
        if (mIsRegistered) {
            mSensorManager.unregisterListener(this)
            mIsRegistered = false
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        if (mListener != null) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val gX = x / SensorManager.GRAVITY_EARTH
            val gY = y / SensorManager.GRAVITY_EARTH
            val gZ = z / SensorManager.GRAVITY_EARTH

            val totalForce = Math.sqrt((gX * gX + gY * gY + gZ * gZ).toDouble())

            if (totalForce > SHAKE_FORCE_THRESHOLD) {
                val now = System.currentTimeMillis()
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) return
                mShakeTimestamp = now
                mListener!!.onShake()
            }
        }
    }

    interface OnShakeListener {
        fun onShake()
    }
}