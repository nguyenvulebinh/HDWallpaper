package nb.cblink.wallpaper.modelview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import nb.cblink.wallpaper.data.FavoritesDatabase;
import nb.cblink.wallpaper.data.MobileswallClient;
import nb.cblink.wallpaper.data.MobileswallFactory;
import nb.cblink.wallpaper.model.Image;
import nb.cblink.wallpaper.view.activity.ImageDetailActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nguyenbinh on 22/09/2016.
 */

public class ListImageViewModel {
    public static final String URL_ID = "inti_url";
    private Context context;
    private RecyclerView recyclerView;
    private ListImageRecycleViewAdapter adapter = null;
    private List<Image> data;
    private String nextUrl;
    private MobileswallFactory factory;
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_FAVORITES = 2;
    public static final int TYPE_HISTORY = 3;
    private ProgressDialog progress = null;

    public ListImageViewModel(final Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
        data = new ArrayList<>();
        factory = MobileswallClient.getClient().create(MobileswallFactory.class);
    }


    public void showImage(String intiUrl, int type) {
        this.nextUrl = intiUrl;
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        if(type == TYPE_NORMAL) {
            progress = ProgressDialog.show(context, "Get image data",
                    "Processing...", true);
            adapter = new ListImageRecycleViewAdapter(context, this, data, recyclerView);
            recyclerView.setAdapter(adapter);
            adapter.setOnLoadMoreListener(new ListImageRecycleViewAdapter.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (!nextUrl.equals("")) {
                        adapter.addToLoadMore();
                        loadMoreImage();
                    }
                }
            });
            loadMoreImage();
        }else if(type == TYPE_FAVORITES){
            data = FavoritesDatabase.getInstance(context).getList(FavoritesDatabase.TYPE_FAVORITES);
            adapter = new ListImageRecycleViewAdapter(context, this, data, recyclerView);
            recyclerView.setAdapter(adapter);
        }else if(type == TYPE_HISTORY){
            data = FavoritesDatabase.getInstance(context).getList(FavoritesDatabase.TYPE_HISTORY);
            adapter = new ListImageRecycleViewAdapter(context, this, data, recyclerView);
            recyclerView.setAdapter(adapter);
        }
    }

    private void loadMoreImage() {
        Call<String> call = factory.loadResource(nextUrl);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null && response.body() != null) {
                    Document doc = Jsoup.parse(response.body());
                    Elements subjectElements = doc.select("div.pic-body");
                    Elements subjectNext = doc.select("a.nextpostslink");
                    for (Element element : subjectElements) {
                        Elements subjectView = element.select("div.views");
                        data.add(new Image(element.getElementsByTag("img").get(0).attr("src"), element.getElementsByTag("img").get(0).attr("alt"), subjectView.get(0).text(), element.getElementsByTag("a").get(0).attr("href")));
                    }
                    adapter.addMoreSongToShow();
                    if (subjectNext.size() > 0) {
                        nextUrl = subjectNext.first().attr("href");
                        nextUrl = nextUrl.substring(27, nextUrl.length());
                    } else {
                        nextUrl = "";
                    }
                    if(progress != null){
                        progress.dismiss();
                        progress = null;
                    }
                }else if(response.body() == null){
                    Toast.makeText(context, "Server error!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


    public void clickImage(Image image){
        Intent intent = new Intent(context, ImageDetailActivity.class);
        intent.putExtra(ImageDetailActivity.KEY_IMAGE_SEND, image);
        context.startActivity(intent);
    }
}
