package nb.cblink.wallpaper.data;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by nguyenbinh on 12/09/2016.
 */

public interface MobileswallFactory {
    @POST("/")
    Call<String> loadResource(@Header("content") String content);
}
