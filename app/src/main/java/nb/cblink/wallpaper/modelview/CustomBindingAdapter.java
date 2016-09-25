package nb.cblink.wallpaper.modelview;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

/**
 * Created by phanirajabhandari on 7/8/15.
 */
public class CustomBindingAdapter {

    private static Picasso picasso = null;

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView imageView, String url) {
        if (picasso == null) {
            int maxSize = 1024 * 1024 * 100;
            picasso = new Picasso.Builder(imageView.getContext())
                    .memoryCache(new LruCache(maxSize))
                    .build();
        }
        picasso.load(url).into(imageView);
    }
}
