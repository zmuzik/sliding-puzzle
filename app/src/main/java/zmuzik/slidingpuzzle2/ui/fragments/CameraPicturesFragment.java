package zmuzik.slidingpuzzle2.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.adapters.PicturesGridAdapter;
import zmuzik.slidingpuzzle2.helpers.BitmapHelper;
import zmuzik.slidingpuzzle2.helpers.PrefsHelper;

public class CameraPicturesFragment extends SavedPicturesFragment {

    final String TAG = this.getClass().getSimpleName();
    public static final int REQUEST_PERMISSION_CAMERA = 100;
    public static final int REQUEST_PERMISSION_STORAGE = 101;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    FloatingActionButton mFab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mFab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntentWrapper();
            }
        });

        return rootView;
    }

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

    private void dispatchTakePictureIntentWrapper() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = BitmapHelper.getOutputPictureFile();
            // Continue only if the File was successfully created
            if (photoFile != null) {
                PrefsHelper.get().setPhotoFilePath(photoFile.getAbsolutePath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                PrefsHelper.get().setPhotoFilePath(null);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            String filePath = PrefsHelper.get().getPhotoFilePath();
            if (filePath == null) return;
            String fileUriStr = BitmapHelper.FILE_PREFIX + filePath;
            mRecyclerView.getLayoutManager().scrollToPosition(0);
            ((PicturesGridAdapter) mRecyclerView.getAdapter()).add(fileUriStr, 0);
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
            return (((FileContainer) another).lastModified - lastModified) > 0 ? 1 : -1;
        }
    }
}
