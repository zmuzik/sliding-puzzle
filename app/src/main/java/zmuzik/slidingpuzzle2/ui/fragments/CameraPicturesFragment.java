package zmuzik.slidingpuzzle2.ui.fragments;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.helpers.BitmapHelper;
import zmuzik.slidingpuzzle2.helpers.PrefsHelper;

public class CameraPicturesFragment extends SavedPicturesFragment {

    final String TAG = this.getClass().getSimpleName();
    public static final int REQUEST_PERMISSION_READ_STORAGE = 101;

    FloatingActionButton mFab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutId(), container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), getColumnsNumber());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mFab = (FloatingActionButton) rootView.findViewById(R.id.fab);
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
        initData();
    }

    @Override
    public List<String> getPictures() {
        if (ContextCompat.checkSelfPermission(getActivity(),  Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && PrefsHelper.get().shouldAskReadStoragePerm()) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_STORAGE);
        } else {
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
        return null;
    }

    public void scanDirectoryForPictures(File root, final ArrayList<FileContainer> filePaths) {
        if (root == null) return;
        Log.d(TAG, "scanning dir: " + root.getAbsolutePath());
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

    public int getLayoutId() {
        return R.layout.fragment_camera_pictures_grid;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_READ_STORAGE) {
            PrefsHelper.get().setShouldAskReadStoragePerm(false);
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initData();
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

        public FileContainer(String filePath, long lastModified) {
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
