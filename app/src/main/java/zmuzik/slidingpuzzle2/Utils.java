package zmuzik.slidingpuzzle2;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class Utils {

    public static final String ASSET_PREFIX = "file:///android_asset/";
    public static final String FILE_PREFIX = "file://";

    public static boolean isBitmapHorizontal(String filePath) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        if (isAsset(filePath)) {
            try {
                InputStream stream = App.get().getAssets().open(getAssetName(filePath));
                BitmapFactory.decodeStream(stream, null, bmOptions);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else if (isFile(filePath)) {
            BitmapFactory.decodeFile(getFileName(filePath), bmOptions);
        }

        return bmOptions.outWidth > bmOptions.outHeight;
    }

    public static boolean isAsset(String filePath) {
        return filePath.startsWith(ASSET_PREFIX);
    }

    public static String getAssetName(String filePath) {
        return filePath.substring(ASSET_PREFIX.length());
    }

    public static boolean isFile(String filePath) {
        return filePath.startsWith(FILE_PREFIX) && !filePath.startsWith(ASSET_PREFIX);
    }

    public static String getFileName(String filePath) {
        return filePath.substring(FILE_PREFIX.length());
    }

    public static boolean isPicture(File file) {
        if (file == null) return false;
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return (type != null) && type.startsWith("image");
    }

    public static boolean isDebuggable(Context context) {
        return 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static boolean isTablet(Context context) {
        return context.getResources().getBoolean(R.bool.isTablet);
    }
}

