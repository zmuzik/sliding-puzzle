package zmuzik.slidingpuzzle2.mainscreen;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;

import zmuzik.slidingpuzzle2.Utils;

/**
 * Created by Zbynek Muzik on 2017-03-31.
 */

class UpdateCameraFilesTask extends AsyncTask<Void, Void, Void> {

    ArrayList<String> mFilesList;
    WeakReference<MainScreenPresenter> mPresenter;

    UpdateCameraFilesTask(MainScreenPresenter presenter) {
        mPresenter = new WeakReference<MainScreenPresenter>(presenter);
    }

    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<FileContainer> foundFiles = new ArrayList<>();
        File cameraDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        scanDirectoryForPictures(cameraDir, foundFiles);
        //sort - most recent pictures first
        Collections.sort(foundFiles);
        ArrayList<String> result = new ArrayList<>();
        for (FileContainer fileContainer : foundFiles) {
            result.add(fileContainer.filePath);
        }
        mFilesList = result;
        return null;
    }

    private void scanDirectoryForPictures(File root, final ArrayList<FileContainer> filePaths) {
        if (root == null) return;
        File[] list = root.listFiles();
        if (list == null) return;

        for (File f : list) {
            if (f.isHidden()) continue;
            if (f.isDirectory()) {
                scanDirectoryForPictures(f, filePaths);
            } else if (Utils.isPicture(f)) {
                filePaths.add(new FileContainer(Utils.FILE_PREFIX +
                        f.getAbsolutePath(), f.lastModified()));
            }
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mPresenter.get() != null) {
            mPresenter.get().updateCameraPictures(mFilesList);
        }
    }

    private class FileContainer implements Comparable {
        String filePath;
        long lastModified;

        private FileContainer(String filePath, long lastModified) {
            this.filePath = filePath;
            this.lastModified = lastModified;
        }

        @Override
        public int compareTo(Object another) {
            long diff = ((FileContainer) another).lastModified - lastModified;
            if (diff == 0) return 0;
            return diff > 0 ? 1 : -1;
        }
    }
}