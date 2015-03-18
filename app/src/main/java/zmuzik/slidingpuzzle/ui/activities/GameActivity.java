package zmuzik.slidingpuzzle.ui.activities;

import android.app.Activity;
import android.os.Bundle;

import zmuzik.slidingpuzzle.R;
import zmuzik.slidingpuzzle.gfx.NewPuzzleBoardView;

public class GameActivity extends Activity {

	final String TAG = this.getClass().getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        NewPuzzleBoardView board = (NewPuzzleBoardView) findViewById(R.id.board);
        String fileUri = getIntent().getExtras().getString("FILE_URI");
        board.setFile(fileUri);
	}
}
