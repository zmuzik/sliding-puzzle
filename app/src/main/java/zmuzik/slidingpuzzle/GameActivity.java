package zmuzik.slidingpuzzle;

import android.os.Bundle;
import android.app.Activity;

public class GameActivity extends Activity {

	final String TAG = this.getClass().getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PicturePuzzleBoardView board = new PicturePuzzleBoardView(this, getIntent());
		setContentView(board);
	}
}
