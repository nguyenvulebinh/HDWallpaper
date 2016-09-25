package nb.cblink.wallpaper.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Spinner;

import nb.cblink.wallpaper.R;
import nb.cblink.wallpaper.databinding.ImageDetailBinding;
import nb.cblink.wallpaper.model.Image;
import nb.cblink.wallpaper.modelview.ImageDetailViewModel;

/**
 * Created by nguyenbinh on 23/09/2016.
 */

public class ImageDetailActivity extends AppCompatActivity{
    public static final String KEY_IMAGE_SEND = "ImageObject";
    private ImageDetailViewModel viewModel;
    private Image image;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.detail_image_layout);
        View layout = binding.getRoot();
        image = (Image) getIntent().getExtras().getSerializable(KEY_IMAGE_SEND);
        binding.setImageItem(image);
        viewModel = new ImageDetailViewModel(this, image, (Spinner) layout.findViewById(R.id.spinner_res));
        binding.setImageDetailVM(viewModel);
    }
}
