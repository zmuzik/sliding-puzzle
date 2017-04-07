package zmuzik.slidingpuzzle2.mainscreen;

import java.util.List;

import zmuzik.slidingpuzzle2.flickr.Photo;

/**
 * Created by Zbynek Muzik on 2017-03-30.
 */

public interface MainScreenView {
    void updateSavedPictures(List<String> strings);

    void setWaitingForCameraPictures();

    void updateCameraPictures(List<String> strings);

    void setWaitingForFlickrPictures();

    void updateFlickrPictures(List<Photo> strings);

}
