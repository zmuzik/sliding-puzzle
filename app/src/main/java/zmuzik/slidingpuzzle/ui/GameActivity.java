package zmuzik.slidingpuzzle.ui;

import android.os.Bundle;
import android.app.Activity;

import zmuzik.slidingpuzzle.R;
import zmuzik.slidingpuzzle.gfx.PicturePuzzleBoardView;

public class GameActivity extends Activity {

	final String TAG = this.getClass().getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        PicturePuzzleBoardView board = (PicturePuzzleBoardView) findViewById(R.id.board);
        String fileUri = getIntent().getExtras().getString("FILE_URI");
        board.setFile(fileUri);
	}
}
