package zmuzik.slidingpuzzle.ui;

import android.os.Bundle;
import android.app.Activity;

import zmuzik.slidingpuzzle.gfx.PicturePuzzleBoardView;

public class GameActivity extends Activity {

	final String TAG = this.getClass().getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PicturePuzzleBoardView board = new PicturePuzzleBoardView(this, getIntent());
		setContentView(board);
	}
}
