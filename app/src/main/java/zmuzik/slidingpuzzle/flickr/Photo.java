package zmuzik.slidingpuzzle.flickr;

public class Photo {
    String id;
    String owner;
    String secret;
    String server;
    int farm;
    String title;
    int ispublic;
    int isfriend;
    int isfamily;
    int height_l;
    int width_l;
    String url_c;
    String url_o;

    public String getUrl_c() {
        return url_c;
    }

    public void setUrl_c(String url_c) {
        this.url_c = url_c;
    }

    public String getUrl_o() {
        return url_o;
    }

    public void setUrl_o(String url_o) {
        this.url_o = url_o;
    }

    public int getHeight_l() {
        return height_l;
    }

    public void setHeight_l(int height_l) {
        this.height_l = height_l;
    }

    public int getWidth_l() {
        return width_l;
    }

    public void setWidth_l(int width_l) {
        this.width_l = width_l;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getFarm() {
        return farm;
    }

    public void setFarm(int farm) {
        this.farm = farm;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIspublic() {
        return ispublic;
    }

    public void setIspublic(int ispublic) {
        this.ispublic = ispublic;
    }

    public int getIsfriend() {
        return isfriend;
    }

    public void setIsfriend(int isfriend) {
        this.isfriend = isfriend;
    }

    public int getIsfamily() {
        return isfamily;
    }

    public void setIsfamily(int isfamily) {
        this.isfamily = isfamily;
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
        return "http://farm" + getFarm() + ".staticflickr.com/"
                + getServer() + "/" + getId() + "_" + getSecret() + "_q.jpg";
    }

    public String getFullPicUrl() {
        return "http://farm" + getFarm() + ".staticflickr.com/"
                + getServer() + "/" + getId() + "_" + getSecret() + "_b.jpg";
    }
}
