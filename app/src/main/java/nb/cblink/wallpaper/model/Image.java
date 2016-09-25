package nb.cblink.wallpaper.model;

import android.databinding.BaseObservable;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by nguyenbinh on 22/09/2016.
 */

public class Image extends BaseObservable implements Serializable{
    private String name;
    private String thumbUrl;
    private String urlWeb;
    private String countView;
    private HashMap<String, String>listUrl;
    private int id;

    public Image(){
        listUrl = new HashMap<>();
    }

    public Image(String thumbUrl,String name, String countView, String urlWeb) {
        this();
        this.thumbUrl = thumbUrl;
        this.countView = countView.substring(0, countView.length() - 5);
        this.urlWeb = urlWeb;
        this.id = this.getUrlWeb().hashCode();
        this.name = name;
    }

    public String getCountView() {
        return countView;
    }

    public void setCountView(String countView) {
        this.countView = countView;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public HashMap<String, String> getListUrl() {
        return listUrl;
    }

    public void setListUrl(HashMap<String, String> listUrl) {
        this.listUrl = listUrl;
    }

    public String getUrlWeb() {
        return urlWeb;
    }

    public void setUrlWeb(String urlWeb) {
        this.urlWeb = urlWeb;
        this.id = this.getUrlWeb().hashCode();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
