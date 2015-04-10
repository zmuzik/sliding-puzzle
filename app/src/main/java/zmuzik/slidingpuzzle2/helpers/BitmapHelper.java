package zmuzik.slidingpuzzle2.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import zmuzik.slidingpuzzle2.App;
import zmuzik.slidingpuzzle2.AppConf;


public class BitmapHelper {
    private static BitmapHelper instance = new BitmapHelper();
    public static final String ASSET_PREFIX = "file:///android_asset/";
    public static final String FILE_PREFIX = "file://";

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

    public static Bitmap decodeFile(String filePath) {
        if (isAsset(filePath)) {
            try {
                InputStream stream = App.get().getAssets().open(getAssetName(filePath));
                return BitmapFactory.decodeStream(stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (isFile(filePath)) {
            return BitmapFactory.decodeFile(getFileName(filePath));
        }
        return null;
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

    public static void saveBitmapAsFile(Bitmap bitmap, String fileName) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, AppConf.PIC_QUALITY, out);
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

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static boolean isPicture(File file) {
        String path = file.getAbsolutePath();
        String mime = BitmapHelper.getMimeType(path);
        return (mime != null) && mime.startsWith("image");
    }

    public static Uri getOutputPictureFileUri(){
        return Uri.fromFile(getOutputPictureFile());
    }

    public static File getOutputPictureFile(){
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                AppConf.DCIM_APP_SUBDIR);

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
        return mediaFile;
    }
}

