package zmuzik.slidingpuzzle;

import java.util.StringTokenizer;
import zmuzik.slidingpuzzle.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.AdapterView.OnItemClickListener;

public class MainMenuActivity extends Activity {

	final String TAG = this.getClass().getSimpleName();
	Button mStartGamebutton;

	private Integer[] mThumbIds = { R.drawable.game_pic_0, R.drawable.game_pic_1, R.drawable.game_pic_2,
			R.drawable.game_pic_3, R.drawable.game_pic_4, R.drawable.game_pic_5, R.drawable.game_pic_6,
			R.drawable.game_pic_7, R.drawable.game_pic_8, R.drawable.game_pic_9 };

	public static final String DIMEN_DELIMITER = "x";
	final int DEFAULT_CHECKED_RADIO_BUTTON = R.id.gridSize4x4;

	int selectedPicture = 0;
	int gridSizeLonger = 0;
	int gridSizeShorter = 0;
	int selectedRadioButtonId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		RadioGroup group = (RadioGroup) findViewById(R.id.gridSizeGroup);
		selectedRadioButtonId = group.getCheckedRadioButtonId();

		if (savedInstanceState != null && savedInstanceState.containsKey("selectedRadioButtonId")) {
			selectedRadioButtonId = savedInstanceState.getInt("selectedRadioButtonId");
		} else if (selectedRadioButtonId == -1) {
			selectedRadioButtonId = DEFAULT_CHECKED_RADIO_BUTTON;
		}

		group.check(selectedRadioButtonId);
		RadioButton radioButton = (RadioButton) group.findViewById(selectedRadioButtonId);
		setSelectedGridSize(radioButton);

		final OnClickListener radioListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				RadioButton rb = (RadioButton) v;
				setSelectedGridSize(rb);
				selectedRadioButtonId = rb.getId();
			}
		};

		final RadioButton choice3x3 = (RadioButton) findViewById(R.id.gridSize3x3);
		choice3x3.setOnClickListener(radioListener);

		final RadioButton choice4x4 = (RadioButton) findViewById(R.id.gridSize4x4);
		choice4x4.setOnClickListener(radioListener);

		final RadioButton choice5x5 = (RadioButton) findViewById(R.id.gridSize5x5);
		choice5x5.setOnClickListener(radioListener);

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new ImageAdapter(this));

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				selectedPicture = position;
				startGame();
			}
		});
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("selectedRadioButtonId", selectedRadioButtonId);
	}

	void setSelectedGridSize(RadioButton rb) {
		String dimenText = rb.getText().toString();
		StringTokenizer st = new StringTokenizer(dimenText, DIMEN_DELIMITER);
		gridSizeLonger = Integer.parseInt(st.nextToken());
		gridSizeShorter = Integer.parseInt(st.nextToken());
	}

	public void startGame() {
		Intent intent = new Intent(getApplicationContext(), GameActivity.class);
		intent.putExtra("gridSizeLonger", gridSizeLonger);
		intent.putExtra("gridSizeShorter", gridSizeLonger);
		intent.putExtra("picture", selectedPicture);
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
