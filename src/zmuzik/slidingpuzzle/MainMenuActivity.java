package zmuzik.slidingpuzzle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenuActivity extends Activity {

	final String TAG = this.getClass().getSimpleName();

	final int MIN_GRID_SIZE_1 = 3;
	final int MAX_GRID_SIZE_1 = 10;
	final int MIN_GRID_SIZE_2 = 3;
	final int MAX_GRID_SIZE_2 = 10;

	SeekBar mSeekBar1;
	SeekBar mSeekBar2;
	TextView mGridSizeTV;

	private Integer[] mThumbIds = { R.drawable.game_pic_0, R.drawable.game_pic_1, R.drawable.game_pic_2,
			R.drawable.game_pic_3, R.drawable.game_pic_4, R.drawable.game_pic_5, R.drawable.game_pic_6,
			R.drawable.game_pic_7, R.drawable.game_pic_8, R.drawable.game_pic_9 };

	int mSelectedPicture = 0;
	int mSelectedGridSize1 = 4;
	int mSelectedGridSize2 = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mSelectedGridSize1 = savedInstanceState.getInt("mSelectedGridSize1");
			mSelectedGridSize2 = savedInstanceState.getInt("mSelectedGridSize2");
		}
		
		setContentView(R.layout.activity_main_menu);
		initGridSizeSeekBars();
		initPicturesGridView();
	}

	void initGridSizeSeekBars() {
		mSeekBar1 = (SeekBar) findViewById(R.id.seekBar1);
		mSeekBar2 = (SeekBar) findViewById(R.id.seekBar2);
		mGridSizeTV = (TextView) findViewById(R.id.gridSizeTV);

		mSeekBar1.setMax(MAX_GRID_SIZE_1 - MIN_GRID_SIZE_1);
		mSeekBar2.setMax(MAX_GRID_SIZE_2 - MIN_GRID_SIZE_2);
		mSeekBar1.setProgress(mSelectedGridSize2 - MIN_GRID_SIZE_1);
		mSeekBar2.setProgress(mSelectedGridSize1 - MIN_GRID_SIZE_2);
		
		updateGridSizeTV();

		mSeekBar1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mSelectedGridSize2 = MIN_GRID_SIZE_1 + progress;
				updateGridSizeTV();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
		});

		mSeekBar2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mSelectedGridSize1 = MIN_GRID_SIZE_2 + progress;
				updateGridSizeTV();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
		});
	}

	void initPicturesGridView() {
		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new ImageAdapter(this));

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				mSelectedPicture = position;
				startGame();
			}
		});
	}

	void updateGridSizeTV() {
		mGridSizeTV.setText("" + mSelectedGridSize2 + " X " + mSelectedGridSize1);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("mSelectedGridSize1", mSelectedGridSize1);
		outState.putInt("mSelectedGridSize2", mSelectedGridSize2);
	}

	public void startGame() {
		Intent intent = new Intent(getApplicationContext(), GameActivity.class);
		intent.putExtra("gridSizeLonger", Math.max(mSelectedGridSize2, mSelectedGridSize1));
		intent.putExtra("gridSizeShorter", Math.min(mSelectedGridSize2, mSelectedGridSize1));
		intent.putExtra("picture", mSelectedPicture);
		startActivity(intent);
	}

	class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return mThumbIds.length;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;

			if (convertView == null) {
				imageView = new ImageView(mContext);
				imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			} else {
				imageView = (ImageView) convertView;
			}
			imageView.setImageResource(mThumbIds[position]);
			return imageView;
		}
	}
}
