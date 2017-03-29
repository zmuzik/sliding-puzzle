package zmuzik.slidingpuzzle2.view;

import android.content.Context;
import android.util.AttributeSet;

import java.util.Arrays;
import java.util.List;

import zmuzik.slidingpuzzle2.Utils;

/**
 * Created by Zbynek Muzik on 2017-03-28.
 */

public class SavedPicturesGridView extends BasePicturesGridView {

    final String[] originalPictures = {
            Utils.ASSET_PREFIX + "game_pic_0.jpg",
            Utils.ASSET_PREFIX + "game_pic_1.jpg",
            Utils.ASSET_PREFIX + "game_pic_7.jpg",
            Utils.ASSET_PREFIX + "game_pic_2.jpg",
            Utils.ASSET_PREFIX + "game_pic_3.jpg",
            Utils.ASSET_PREFIX + "game_pic_4.jpg",
            Utils.ASSET_PREFIX + "game_pic_5.jpg",
            Utils.ASSET_PREFIX + "game_pic_6.jpg",
            Utils.ASSET_PREFIX + "game_pic_8.jpg",
            Utils.ASSET_PREFIX + "game_pic_9.jpg",
            Utils.ASSET_PREFIX + "game_pic_10.jpg",
            Utils.ASSET_PREFIX + "game_pic_11.jpg"};

    public SavedPicturesGridView(Context context) {
        super(context);
        mFab.setVisibility(GONE);
    }

    public SavedPicturesGridView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mFab.setVisibility(GONE);
    }

    @Override
    public void requestUpdate() {
        // synchronous update, no need to load pictures list in the background
        update();
    }

    @Override
    public List<String> getPictures() {
        return Arrays.asList(originalPictures);
    }
}
