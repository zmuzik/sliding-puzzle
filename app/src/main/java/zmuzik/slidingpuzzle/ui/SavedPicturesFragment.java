package zmuzik.slidingpuzzle.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import zmuzik.slidingpuzzle.App;
import zmuzik.slidingpuzzle.R;
import zmuzik.slidingpuzzle.adapters.GridViewAdapter;

public class SavedPicturesFragment extends Fragment {

    GridView gridView;

    public SavedPicturesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_saved_pictures, container, false);

        gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridView.setAdapter(new GridViewAdapter());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startGame(i);
            }
        });
        return rootView;
    }

    public void startGame(int pictureNumber) {
        Intent intent = new Intent(App.get(), GameActivity.class);
        intent.putExtra("FILE_URI", (String) gridView.getAdapter().getItem(pictureNumber));
        startActivity(intent);
    }
}
