package nb.cblink.wallpaper.modelview;

import android.support.v7.widget.RecyclerView;

import nb.cblink.wallpaper.databinding.ItemImageBinding;
import nb.cblink.wallpaper.databinding.ListImageDataBindding;

/**
 * Created by nguyenbinh on 22/09/2016.
 */

public class ListImageRecycleViewHolder extends RecyclerView.ViewHolder {
    private ItemImageBinding imageDataBindding;
    public ListImageRecycleViewHolder(ItemImageBinding imageDataBindding) {
        super(imageDataBindding.getRoot());
        this.imageDataBindding = imageDataBindding;
    }
    public ItemImageBinding getImageDataBindding(){
        return imageDataBindding;
    }
}
