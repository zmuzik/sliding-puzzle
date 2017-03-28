package zmuzik.slidingpuzzle2.ui.fragments;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.helpers.BitmapHelper;
import zmuzik.slidingpuzzle2.helpers.PrefsHelper;

public class CameraPicturesFragment extends SavedPicturesFragment {

    final String TAG = this.getClass().getSimpleName();
    public static final int REQUEST_PERMISSION_READ_STORAGE = 101;
    private boolean mIsUpdating;

    FloatingActionButton mFab;
    ProgressBar mProgressBar;
    private ArrayList<String> mFilesList;
    private LinearLayout mPermissionsCombo;
    private Button mRequestPermissionsButton;

    @Inject PrefsHelper prefsHelper;

    public CameraPicturesFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutId(), container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), getColumnsNumber());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mPermissionsCombo = (LinearLayout) rootView.findViewById(R.id.permissionsCombo);
        mRequestPermissionsButton = (Button) rootView.findViewById(R.id.requestPermissionsButton);
        mFab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        if (!isReadExternalGranted()) {
            mPermissionsCombo.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mFab.setVisibility(View.GONE);
        } else {
            mPermissionsCombo.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mFab.setVisibility(View.VISIBLE);
        }

        mRequestPermissionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_READ_STORAGE);
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchRunCameraIntent();
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        maybeRequestUpdate();
    }

    private void maybeRequestUpdate() {
        if (!mIsUpdating) {
            mIsUpdating = true;
            if (!isReadExternalGranted() && prefsHelper.shouldAskReadStoragePerm()) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_READ_STORAGE);
            } else {
                new UpdateCameraFilesTask().execute();
            }
        }
    }

    private boolean isReadExternalGranted() {
        return ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public List<String> getPictures() {
        return mFilesList;
    }

    public int getLayoutId() {
        return R.layout.fragment_camera_pictures_grid;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_READ_STORAGE) {
            prefsHelper.setShouldAskReadStoragePerm(false);
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionsCombo.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mFab.setVisibility(View.VISIBLE);
                new UpdateCameraFilesTask().execute();
            } else {
                mIsUpdating = false;
            }
        }
    }

    private void dispatchRunCameraIntent() {
        Intent auxIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            PackageManager pm = getContext().getPackageManager();
            ResolveInfo mInfo = pm.resolveActivity(auxIntent, 0);
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(mInfo.activityInfo.packageName, mInfo.activityInfo.name));
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(intent);
        } catch (Exception e) {
            Log.i(TAG, "Unable to launch camera: " + e);
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

    private class UpdateCameraFilesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
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
                } else if (BitmapHelper.isPicture(f)) {
                    filePaths.add(new FileContainer(BitmapHelper.FILE_PREFIX +
                            f.getAbsolutePath(), f.lastModified()));
                }
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isAdded() & isResumed()) {
                initData();
            }
            mIsUpdating = false;
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
