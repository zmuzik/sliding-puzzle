package zmuzik.slidingpuzzle2.gamescreen;

/**
 * Created by Zbynek Muzik on 2017-04-03.
 */

public interface GameScreenView {

    void finishWithMessage(int stringId);

    void loadPicture(String pictureUri);

    int getMaxScreenDim();

    void hideShuffleIcon();
}
