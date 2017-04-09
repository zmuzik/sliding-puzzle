package zmuzik.slidingpuzzle2.common;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {

    private static final float SHAKE_FORCE_THRESHOLD = 2f;
    private static final int SHAKE_SLOP_TIME_MS = 500;

    private final Context mContext;
    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    private OnShakeListener mListener;
    private long mShakeTimestamp;
    private boolean mIsRegistered;

    public ShakeDetector(Context context) {
        mContext = context;
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void setOnShakeListener(OnShakeListener listener) {
        this.mListener = listener;
    }

    public void register() {
        if (!mIsRegistered) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
            mIsRegistered = true;
        }
    }

    public void unRegister() {
        if (mIsRegistered) {
            mSensorManager.unregisterListener(this);
            mIsRegistered = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mListener != null) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            double totalForce = Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (totalForce > SHAKE_FORCE_THRESHOLD) {
                final long now = System.currentTimeMillis();
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) return;
                mShakeTimestamp = now;
                mListener.onShake();
            }
        }
    }

    public interface OnShakeListener {
        public void onShake();
    }
}