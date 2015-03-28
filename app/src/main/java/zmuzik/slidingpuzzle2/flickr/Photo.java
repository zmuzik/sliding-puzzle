package zmuzik.slidingpuzzle2.flickr;

import java.util.Collections;
import java.util.List;

public class Photo {
    String id;
    String secret;
    String server;
    int farm;
    String title;
    int height_l;
    int width_l;
    String url_c;
    String url_o;


    public int getHeight_l() {
        return height_l;
    }

    public int getWidth_l() {
        return width_l;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

//    s	small square 75x75
//    q	large square 150x150
//    t	thumbnail, 100 on longest side
//    m	small, 240 on longest side
//    n	small, 320 on longest side
//    -	medium, 500 on longest side
//    z	medium 640, 640 on longest side
//    c	medium 800, 800 on longest side†
//    b	large, 1024 on longest side*
//    h	large 1600, 1600 on longest side†
//    k	large 2048, 2048 on longest side†
//    o	original image, either a jpg, gif or png, depending on source format

    public String getThumbUrl() {
        return "http://farm" + farm + ".staticflickr.com/"
                + server + "/" + getId() + "_" + secret + "_q.jpg";
    }

    public String getFullPicUrl(int maxScreenDim, List<Size> sizes) {
        String result = getThumbUrl();
        int prevSize = 0;
        Collections.sort(sizes);
        for (Size size : sizes) {
            if (size.getMaxDim() > prevSize && size.getMaxDim() <= maxScreenDim) {
                result = size.getSource();
            }
        }
        return result;
    }
}
