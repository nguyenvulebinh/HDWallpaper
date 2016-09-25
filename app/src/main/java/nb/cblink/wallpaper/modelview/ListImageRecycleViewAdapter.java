package nb.cblink.wallpaper.modelview;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import nb.cblink.wallpaper.R;
import nb.cblink.wallpaper.databinding.ItemImageBinding;
import nb.cblink.wallpaper.model.Image;

/**
 * Created by nguyenbinh on 22/09/2016.
 */

public class ListImageRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private Context context;
    private ListImageViewModel modelView;
    private OnLoadMoreListener mOnLoadMoreListener;
    private List<Image> mDocs = new ArrayList<>();
    private int lastVisibleItem, totalItemCount;
    private int visibleThreshold = 20;
    List<Image> data;


    private boolean isLoading;

    public ListImageRecycleViewAdapter(Context context, ListImageViewModel modelView, final List<Image> data, RecyclerView recyclerView) {
        this.context = context;
        this.modelView = modelView;
        for (Image image : data) {
            mDocs.add(image);
        }
        this.data = data;

        final GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        //Cai dat ScollListener cho recycler view
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            ItemImageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_list_image, parent, false);
            return new ListImageRecycleViewHolder(binding);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        Log.d("GetItem", position + "");
        return mDocs.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ListImageRecycleViewHolder) {
            ItemImageBinding binding = ((ListImageRecycleViewHolder) holder).getImageDataBindding();

            binding.setListItemIMVM(modelView);
            binding.setImageItem(mDocs.get(position));
        } else {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return mDocs == null ? 0 : mDocs.size();
    }


    public void addToLoadMore() {
        mDocs.add(null);
        this.notifyItemInserted(mDocs.size() - 1);
    }

    public void setLoaded() {
        isLoading = false;
    }

    public void addMoreSongToShow() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mDocs.size() > 0) {
                    mDocs.remove(mDocs.size() - 1);
                    ListImageRecycleViewAdapter.this.notifyItemRemoved(mDocs.size());
                }
                //Load data
                int index = mDocs.size();

                for (int i = index; i < data.size(); i++) {
                    if (i < data.size()) {
                        mDocs.add(data.get(i));
                    } else {
                        break;
                    }
                }

                ListImageRecycleViewAdapter.this.notifyDataSetChanged();
                ListImageRecycleViewAdapter.this.setLoaded();
            }

        }, 1000);
    }


    interface OnLoadMoreListener {
        void onLoadMore();
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }
}
