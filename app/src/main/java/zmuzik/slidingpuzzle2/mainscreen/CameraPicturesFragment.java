package zmuzik.slidingpuzzle2.mainscreen;

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
import zmuzik.slidingpuzzle2.Utils;
import zmuzik.slidingpuzzle2.common.PreferencesHelper;

public class CameraPicturesFragment extends SavedPicturesFragment {

    final String TAG = this.getClass().getSimpleName();
    public static final int REQUEST_PERMISSION_READ_STORAGE = 101;
    private boolean mIsUpdating;

    FloatingActionButton mFab;
    ProgressBar mProgressBar;
    private ArrayList<String> mFilesList;
    private LinearLayout mPermissionsCombo;
    private Button mRequestPermissionsButton;

    @Inject
    PreferencesHelper prefsHelper;

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
}
