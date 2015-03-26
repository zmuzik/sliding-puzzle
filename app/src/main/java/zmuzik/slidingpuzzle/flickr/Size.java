package zmuzik.slidingpuzzle.flickr;

public class Size implements Comparable {
    int width;
    int height;
    String source;

    public int getMaxDim() {
        return (width > height) ? width : height;
    }

    public String getSource() {
        return source;
    }

    @Override public int compareTo(Object another) {
        return getMaxDim() > ((Size)another).getMaxDim() ? 1 : -1;
    }
}
