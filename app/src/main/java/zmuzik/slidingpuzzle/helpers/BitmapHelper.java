package zmuzik.slidingpuzzle.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import zmuzik.slidingpuzzle.App;
import zmuzik.slidingpuzzle.Conf;


public class BitmapHelper {
    private static BitmapHelper instance = new BitmapHelper();
    public static final String ASSET_PREFIX = "file:///android_asset/";

    public static BitmapHelper get() {
        return instance;
    }


    private BitmapHelper() {
    }

    public static Bitmap getScaledBitmap(String filePath, int targetW, int targetH) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        if (isAsset(filePath)) {
            try {
                BitmapFactory.decodeStream(App.get().getAssets().open(filePath), null, bmOptions);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            BitmapFactory.decodeFile(filePath, bmOptions);
        }

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = (targetH == 0 || targetW == 0) ? 1 : Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        if (isAsset(filePath)) {
            try {
                return BitmapFactory.decodeStream(App.get().getAssets().open(filePath), null, bmOptions);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return BitmapFactory.decodeFile(filePath, bmOptions);
        }
    }

    public static boolean isBitmapHorizontal(String filePath) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

         if (isAsset(filePath)) {
            try {
                InputStream stream = App.get().getAssets().open(getAssetFileName(filePath));
                BitmapFactory.decodeStream(stream, null, bmOptions);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            BitmapFactory.decodeFile(filePath, bmOptions);
        }

        return bmOptions.outWidth > bmOptions.outHeight;
    }

    public static Bitmap decodeFile(String filePath) {
        if (isAsset(filePath)) {
            try {
                InputStream stream = App.get().getAssets().open(getAssetFileName(filePath));
                return BitmapFactory.decodeStream(stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return BitmapFactory.decodeFile(filePath);
        }
        return null;
    }

    public static boolean isAsset(String filePath) {
        return filePath.startsWith(ASSET_PREFIX);
    }

    public static String getAssetFileName(String filePath) {
        return filePath.substring(ASSET_PREFIX.length());
    }

    public static void saveBitmapAsFile(Bitmap bitmap, String fileName) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, Conf.PIC_QUALITY, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

