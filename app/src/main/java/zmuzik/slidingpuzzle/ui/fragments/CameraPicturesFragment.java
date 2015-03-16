package zmuzik.slidingpuzzle.ui.fragments;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zmuzik.slidingpuzzle.helpers.BitmapHelper;

public class CameraPicturesFragment extends SavedPicturesFragment {

    final String TAG = this.getClass().getSimpleName();

    @Override
    public List<String> getPictures() {
        ArrayList<FileContainer> foundFiles = new ArrayList<>();
        File cameraDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        scanDirectoryForPictures(cameraDir, foundFiles);
        //sort - most recent pictures first
        Collections.sort(foundFiles);
        ArrayList<String> result = new ArrayList<>();
        for (FileContainer fileContainer : foundFiles) {
            result.add(fileContainer.filePath);
        }
        return result;
    }

    public void scanDirectoryForPictures(File root, final ArrayList<FileContainer> filePaths) {
        Log.d(TAG, "scanning dir: " + root.getAbsolutePath());
        File[] list = root.listFiles();

        for (File f : list) {
            if (f.isHidden()) continue;
            if (f.isDirectory()) {
                scanDirectoryForPictures(f, filePaths);
            } else if (BitmapHelper.isPicture(f)) {
                filePaths.add(new FileContainer(BitmapHelper.FILE_PREFIX +
                        f.getAbsolutePath(), f.lastModified()));
            }
        }
    }

    private class FileContainer implements Comparable {
        String filePath;
        long lastModified;

        public FileContainer(String filePath, long lastModified) {
            this.filePath = filePath;
            this.lastModified = lastModified;
        }

        @Override public int compareTo(Object another) {
            //most recent pictures first
            return (((FileContainer) another).lastModified - lastModified) > 0 ? 1 : -1;
        }
    }
}
