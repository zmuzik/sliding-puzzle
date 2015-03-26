package zmuzik.slidingpuzzle.flickr;


import java.util.List;

public class Photos {
    private int page;
    private int pages;
    private int perpage;
    private int total;
    private List<Photo> photo;

    public List<Photo> getPhoto() {
        return photo;
    }
}
