package zmuzik.slidingpuzzle2.common;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import zmuzik.slidingpuzzle2.App;

/**
 * Created by Zbynek Muzik on 2017-03-30.
 * Convenience class for showing toast messages from any thread
 * using the app context to prevent crashes.
 */

public class Toaster {

    private static Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private static int DEFAULT_TOAST_LENGTH = Toast.LENGTH_SHORT;

    public static void toast(final String message) {
        toast(message, DEFAULT_TOAST_LENGTH);
    }

    public static void toast(final String message, final int length) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(App.get(), message, length).show();
        } else {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(App.get(), message, length).show();
                }
            });
        }
    }
}
