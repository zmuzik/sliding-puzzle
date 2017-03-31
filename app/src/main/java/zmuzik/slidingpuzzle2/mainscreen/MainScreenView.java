package zmuzik.slidingpuzzle2.mainscreen;

import java.util.List;

/**
 * Created by Zbynek Muzik on 2017-03-30.
 */

public interface MainScreenView {
    void updateSavedPictures(List<String> strings);

    void updateCameraPictures(List<String> strings);

    void updateFlickrPictures(List<String> strings);
}
