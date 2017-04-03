package zmuzik.slidingpuzzle2.common;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import javax.inject.Inject;

/**
 * Created by Zbynek Muzik on 2017-03-30.
 * Convenience class for showing show messages from any thread
 * using the app context to prevent crashes.
 */

public class Toaster {

    Application mApplication;

    @Inject
    public Toaster(Application application) {
        mApplication = application;
    }

    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private int DEFAULT_TOAST_LENGTH = Toast.LENGTH_SHORT;

    public void show(int stringId) {
        show(mApplication.getString(stringId));
    }

    public void show(int stringId, int length) {
        show(mApplication.getString(stringId), length);
    }

    public void show(final String message) {
        show(message, DEFAULT_TOAST_LENGTH);
    }

    public void show(final String message, final int length) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(mApplication, message, length).show();
        } else {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mApplication, message, length).show();
                }
            });
        }
    }
}
