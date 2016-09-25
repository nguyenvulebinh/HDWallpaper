package nb.cblink.wallpaper.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

/**
 * Created by nguyenbinh on 23/09/2016.
 */

public class SharePreData {
    public static String SAVE_FILE_NAME = "HDWallpaper";

    public static void saveResolution(Context context, int width, int height){
        SharedPreferences preferences = context.getSharedPreferences(SAVE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("width", width);
        editor.putInt("height", height);
        editor.apply();
    }

    public static int getWidth(Context context){
        SharedPreferences preferences = context.getSharedPreferences(SAVE_FILE_NAME, Context.MODE_PRIVATE);
        return preferences.getInt("width", 0);
    }

    public static int getHeight(Context context){
        SharedPreferences preferences = context.getSharedPreferences(SAVE_FILE_NAME, Context.MODE_PRIVATE);
        return preferences.getInt("height", 0);
    }
}
