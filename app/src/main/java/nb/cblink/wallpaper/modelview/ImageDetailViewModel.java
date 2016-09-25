package nb.cblink.wallpaper.modelview;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nb.cblink.wallpaper.R;
import nb.cblink.wallpaper.data.FavoritesDatabase;
import nb.cblink.wallpaper.data.MobileswallClient;
import nb.cblink.wallpaper.data.MobileswallFactory;
import nb.cblink.wallpaper.data.SharePreData;
import nb.cblink.wallpaper.model.Image;
import nb.cblink.wallpaper.view.activity.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nguyenbinh on 23/09/2016.
 */

public class ImageDetailViewModel extends BaseObservable {
    private Context context;
    private ProgressDialog progress;
    private DownloadManager downloadManager;
    private static final String[] listRes = {"320 x 480", "480 x 800", "480 x 854", "540 x 960", "600 x 800", "600 x 1024", "640 x 960", "640 x 1136", "720 x 720", "720 x 1280", "768 x 1024", "768 x 1280", "800 x 1280", "1080 x 1920", "1200 x 1920", "1536 x 2048"};
    private static final String regex = ".*if\\(my_device==\\'(.*)\\'.*large_image.*\\'(.*)\\'.*";
    private static final String[] mapDevice = {"picasso", "768 x 1280",
            "alcatelultra", "720 x 1280",
            "alcatel995", "480 x 800",
            "kindlefire1", "600 x 1024",
            "kindlefire7", "800 x 1280",
            "kindlefire89", "1200 x 1920",
            "ipad12", "768 x 1024",
            "ipad34", "1536 x 2048",
            "ipadmini", "768 x 1024",
            "asus", "1200 x 1920",
            "blackberryq10", "720 x 720",
            "blackberryz10", "768 x 1280",
            "droid", "480 x 854",
            "droid34", "540 x 960",
            "droidrazr", "540 x 960",
            "droidmaxx", "540 x 960",
            "galaxynexus", "720 x 1280",
            "galaxynote", "800 x 1280",
            "galaxynote10", "800 x 1280",
            "galaxynote2", "720 x 1280",
            "galaxys", "480 x 800",
            "galaxysplus", "480 x 800",
            "galaxysii", "480 x 800",
            "galaxysiii", "720 x 1280",
            "galaxysiv", "1080 x 1920",
            "nexus4", "768 x 1280",
            "nexus7", "800 x 1280",
            "htcdesire", "480 x 800",
            "htcdesirehd", "480 x 800",
            "htcevo", "480 x 800",
            "htcone", "1080 x 1920",
            "iphone3gs", "320 x 480",
            "iphone4", "640 x 960",
            "iphone5", "640 x 1136",
            "iphone5s", "640 x 1136",
            "iphone6", "640 x 1136",
            "iphone6plus", "640 x 1136",
            "lumia710", "480 x 800",
            "lumia1020", "768 x 1280",
            "lumia520", "480 x 800",
            "lumia620", "480 x 800",
            "lumia720", "480 x 800",
            "lumia900", "480 x 800",
            "lumia920", "768 x 1280",
            "lumia925", "768 x 1280",
            "nook", "600 x 800",
            "galaxytab", "600 x 1024",
            "galaxytab10", "800 x 1280",
            "galaxytab2", "600 x 1024",
            "galaxytab210", "800 x 1280",
            "nexuss", "480 x 800",
            "xperiaz", "1080 x 1920"};

    @Bindable
    public boolean favoritesImage;
    @Bindable
    public int showAdvance;

    private Spinner spinner;
    private Image image;
    private MobileswallFactory factory;
    private Map<String, String> mapDeviceList;
    private int width, height;
    private long Image_DownloadId;
    private static WallpaperManager wallpaperManager;

    public ImageDetailViewModel(Context context,Image image, Spinner spinner) {
        this.context = context;
        this.spinner = spinner;
        this.image = image;
        factory = MobileswallClient.getClient().create(MobileswallFactory.class);
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        init();
    }

    private void init() {
        width = SharePreData.getWidth(context);
        height = SharePreData.getHeight(context);
        if(width == 0) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            width = metrics.widthPixels;
            height = metrics.heightPixels;
            SharePreData.saveResolution(context, width, height);
        }

        if(FavoritesDatabase.getInstance(context).existFavorit(image.getId())) {
            favoritesImage = true;
            image = FavoritesDatabase.getInstance(context).getSingle(image.getId());
        }else{
            favoritesImage = false;
            if(FavoritesDatabase.getInstance(context).exist(image.getId())) {
                image = FavoritesDatabase.getInstance(context).getSingle(image.getId());
            }
        }

        showAdvance = View.GONE;
        ArrayList<String> listQuanlity = new ArrayList<>();
        for (String str : listRes) listQuanlity.add(str);
        mapDeviceList = new HashMap<>();
        for(int i = 0; i < mapDevice.length; i+=2){
            mapDeviceList.put(mapDevice[i], mapDevice[i + 1]);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, listQuanlity);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Chon do phan gia phu hop voi man hinh
        int defaultPos = 0;
        double delta = Integer.MAX_VALUE;
        for(int i = listRes.length - 1; i >= 0 ; i--){
            String[] re = listRes[i].split("x");
            int widthTemp = Integer.parseInt(re[0].trim());
            int heightTemp = Integer.parseInt(re[1].trim());
            double temp = widthTemp * 1.0 / heightTemp - width * 1.0 / height;
            if(temp < 0.001){
                defaultPos = i;
                break;
            }else{
                if(delta > temp){
                    delta = temp;
                    defaultPos = i;
                }
            }
        }
        spinner.setSelection(defaultPos);
        notifyChange();
    }

    public void showAdvance() {
        showAdvance = showAdvance == View.GONE ? View.VISIBLE : View.GONE;
        notifyChange();
    }

    public void favoriteClick() {
        if(favoritesImage){
            FavoritesDatabase.getInstance(context).saveImage(image, FavoritesDatabase.TYPE_DELETE);
        }else{
            if(image.getListUrl().size() == 0){
                progress = ProgressDialog.show(context, "Get image data",
                        "Processing...", true);
                progress.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        FavoritesDatabase.getInstance(context).saveImage(image, FavoritesDatabase.TYPE_FAVORITES);
                    }
                });
                loadMoreImage();
            }else {
                FavoritesDatabase.getInstance(context).saveImage(image, FavoritesDatabase.TYPE_FAVORITES);
            }
        }
        favoritesImage = !favoritesImage;
        notifyChange();
    }

    public void clickDownload(){
        if(image.getListUrl().size() == 0){
            progress = ProgressDialog.show(context, "Get image data",
                    "Processing...", true);
            progress.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    processDownload();
                }
            });
            loadMoreImage();
        }else {
            processDownload();
        }
    }

    private void processDownload(){
        Uri uri;
        String url = image.getListUrl().get(spinner.getSelectedItem());
        if(url != null) {
            uri = Uri.parse(url);
        }else{
            int defaultPos = 0;
            double delta = Integer.MAX_VALUE;
            for(int i = listRes.length - 1; i >= 0 ; i--){
                String[] re = listRes[i].split("x");
                int widthTemp = Integer.parseInt(re[0].trim());
                int heightTemp = Integer.parseInt(re[1].trim());
                double temp = widthTemp * 1.0 / heightTemp - width * 1.0 / height;
                if(temp < 0.001 && image.getListUrl().get(listRes[i]) != null){
                    defaultPos = i;
                    break;
                }else{
                    if(delta > temp && image.getListUrl().get(listRes[i]) != null){
                        delta = temp;
                        defaultPos = i;
                    }
                }
            }
            spinner.setSelection(defaultPos);
            uri = Uri.parse(image.getListUrl().get(spinner.getSelectedItem()));
        }

        try {
            DownloadManager.Request request = new DownloadManager.Request(uri);
            //Setting title of request
            request.setTitle(image.getName());
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, image.getName()+spinner.getSelectedItem() + ".jpg");
            Image_DownloadId = downloadManager.enqueue(request);
            //set filter to only when download is complete and register broadcast receiver
            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            context.registerReceiver(downloadReceiver, filter);
            progress = ProgressDialog.show(context, "Download image",
                    "Processing...", true);
        }catch (Exception e){
            showDialogCanntWrite(context);
        }
    }

    public void clickSetBackground(){
        if(image.getListUrl().size() == 0){
            progress = ProgressDialog.show(context, "Get image data",
                    "Processing...", true);
            progress.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    processSetBackground();
                }
            });
            loadMoreImage();
        }else {
            processSetBackground();
        }
    }

    private static String url;
    private void processSetBackground(){

        url = image.getListUrl().get(spinner.getSelectedItem());
        if(url == null) {
            int defaultPos = 0;
            double delta = Integer.MAX_VALUE;
            for(int i = listRes.length - 1; i >= 0 ; i--){
                String[] re = listRes[i].split("x");
                int widthTemp = Integer.parseInt(re[0].trim());
                int heightTemp = Integer.parseInt(re[1].trim());
                double temp = widthTemp * 1.0 / heightTemp - width * 1.0 / height;
                if(temp < 0.001 && image.getListUrl().get(listRes[i]) != null){
                    defaultPos = i;
                    break;
                }else{
                    if(delta > temp && image.getListUrl().get(listRes[i]) != null){
                        delta = temp;
                        defaultPos = i;
                    }
                }
            }
            spinner.setSelection(defaultPos);
            url = image.getListUrl().get(spinner.getSelectedItem());
        }



        FavoritesDatabase.getInstance(context).saveImage(image, FavoritesDatabase.TYPE_HISTORY);

        wallpaperManager = WallpaperManager.getInstance(context);

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = ProgressDialog.show(context, "Set wallpaper",
                        "Processing...", true);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Bitmap result= Picasso.with(context)
                            .load(url)
                            .get();

                    wallpaperManager.setBitmap(result);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progress.dismiss();
                MainActivity.mInterstitialAd.show();
                Toast.makeText(context, "Set wallpaper: " + image.getName(), Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    private void loadMoreImage() {
        Call<String> call = factory.loadResource(image.getUrlWeb().substring(27, image.getUrlWeb().length()));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null) {
                    Matcher m = Pattern.compile(regex)
                            .matcher(response.body());
                    while (m.find()) {
                        if (!m.group(2).equals("")) {
                            image.getListUrl().put(mapDeviceList.get(m.group(1)), m.group(2));
                        }
                    }
                    progress.dismiss();
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }


    void showDialogCanntWrite(Context context) {
        AlertDialog.Builder builder = new
                AlertDialog.Builder(context);
        builder.setTitle("Can not write data")
                .setMessage("The cause may be due:\n" +
                        "\t- Application don't have write permission\n" +
                        "\t- Not enough memory")
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                            }
                        });
        builder.create().show();
    }

    void showDialogDownloadOK(Context context, long referenceId) {
        DownloadManager.Query myDownloadQuery = new DownloadManager.Query();
        //set the query filter to our previously Enqueued download
        myDownloadQuery.setFilterById(referenceId);
        Cursor cursor = downloadManager.query(myDownloadQuery);
        cursor.moveToFirst();
        int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
        String fileName = cursor.getString(filenameIndex);
        AlertDialog.Builder builder = new
                AlertDialog.Builder(context);
        builder.setTitle("Download complete")
                .setMessage("Location: " + fileName)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                MainActivity.mInterstitialAd.show();
                            }
                        });
        builder.create().show();
    }


    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if(referenceId == Image_DownloadId) {
                progress.dismiss();
                showDialogDownloadOK(context, referenceId);
            }
        }
    };
}
