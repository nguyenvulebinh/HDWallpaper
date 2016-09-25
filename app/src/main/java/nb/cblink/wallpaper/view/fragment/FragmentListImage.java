package nb.cblink.wallpaper.view.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nb.cblink.wallpaper.R;
import nb.cblink.wallpaper.databinding.ListImageDataBindding;
import nb.cblink.wallpaper.modelview.ListImageViewModel;
import nb.cblink.wallpaper.view.activity.MainActivity;

/**
 * Created by nguyenbinh on 22/09/2016.
 */

public class FragmentListImage extends Fragment{
    ListImageViewModel modelView;
    private View layout;
    private MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ListImageDataBindding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_image, container, false);
        layout = binding.getRoot();
        modelView = new ListImageViewModel(layout.getContext(), (RecyclerView) layout.findViewById(R.id.recycleview_list_image));
        binding.setListIMVM(modelView);
        return layout;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();

        if (!isNetworkConnected(mainActivity)) {
            //Ban ra dialog de thong bao ko cos ket noi
            showDialogNotConnectInternet(mainActivity);
        }

        if(bundle != null && bundle.containsKey(ListImageViewModel.URL_ID)) {
            String initUrl = bundle.getString(ListImageViewModel.URL_ID);
            if (initUrl.equals("myfavorites")) {
                modelView.showImage(initUrl, ListImageViewModel.TYPE_FAVORITES);
            } else if (initUrl.equals("myhistory")){
                modelView.showImage(initUrl, ListImageViewModel.TYPE_HISTORY);
            }else {
                modelView.showImage(initUrl, ListImageViewModel.TYPE_NORMAL);
            }
        }
    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    void showDialogNotConnectInternet(final MainActivity activity) {
        AlertDialog.Builder builder = new
                AlertDialog.Builder(activity);
        builder.setTitle("There is no internet connection")
                .setMessage("Turn on internet connection?")
                .setPositiveButton("Setting",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                                activity.finish();
                            }
                        })
                .setNegativeButton("Exit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                activity.finish();
                            }
                        });
        builder.create().show();
    }
}
