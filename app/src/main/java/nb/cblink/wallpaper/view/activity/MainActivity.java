package nb.cblink.wallpaper.view.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import nb.cblink.wallpaper.R;
import nb.cblink.wallpaper.modelview.ListImageViewModel;
import nb.cblink.wallpaper.view.fragment.FragmentListImage;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        displayView(R.id.nav_popular);

        addAds();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void addAds() {
        MobileAds.initialize(this, "ca-app-pub-6742359292736580~7351044554");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6742359292736580/1304510958");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                //Cho nguoi dung tai
            }
        });

        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displayView(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void displayView(int viewId) {
        String title = getString(R.string.app_name);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        FragmentListImage fragment = new FragmentListImage();
        fragment.setMainActivity(this);
        Bundle bundle = new Bundle();
        switch (viewId) {
            case R.id.nav_popular:
                title  = "Most popular";
                bundle.putString(ListImageViewModel.URL_ID, "popular/");
                break;
            case R.id.nav_abstract:
                title  = "Abstract";
                bundle.putString(ListImageViewModel.URL_ID, "tag/abstract/");
                break;
            case R.id.nav_favorites:
                title  = "My favorites";
                bundle.putString(ListImageViewModel.URL_ID, "myfavorites");
                break;
            case R.id.nav_history:
                title  = "My history";
                bundle.putString(ListImageViewModel.URL_ID, "myhistory");
                break;
            case R.id.nav_animals:
                title  = "Animals";
                bundle.putString(ListImageViewModel.URL_ID, "tag/animals/");
                break;
            case R.id.nav_art:
                title  = "Art";
                bundle.putString(ListImageViewModel.URL_ID, "tag/art/");
                break;
            case R.id.nav_cars:
                title  = "Cars";
                bundle.putString(ListImageViewModel.URL_ID, "tag/cars/");
                break;
            case R.id.nav_food_drink:
                title  = "Food & Drink";
                bundle.putString(ListImageViewModel.URL_ID, "tag/food-drink/");
                break;
            case R.id.nav_games:
                title  = "Games";
                bundle.putString(ListImageViewModel.URL_ID, "tag/games/");
                break;
            case R.id.nav_movies:
                title  = "Movies";
                bundle.putString(ListImageViewModel.URL_ID, "tag/movies/");
                break;
            case R.id.nav_music:
                title  = "Music";
                bundle.putString(ListImageViewModel.URL_ID, "tag/music/");
                break;
            case R.id.nav_nature:
                title  = "Nature";
                bundle.putString(ListImageViewModel.URL_ID, "tag/nature/");
                break;
            case R.id.nav_photos:
                title  = "Photos";
                bundle.putString(ListImageViewModel.URL_ID, "tag/photos/");
                break;
            case R.id.nav_places:
                title  = "Places";
                bundle.putString(ListImageViewModel.URL_ID, "tag/places/");
                break;
            case R.id.nav_quotes:
                title  = "Quotes";
                bundle.putString(ListImageViewModel.URL_ID, "tag/quotes/");
                break;
            case R.id.nav_sports:
                title  = "Sports";
                bundle.putString(ListImageViewModel.URL_ID, "tag/sports/");
                break;
            default:
                fragment = null;
        }

        if(fragment!= null) {
            fragment.setArguments(bundle);
            transaction.replace(R.id.content_frame, fragment);
            transaction.commit();

            // set the toolbar title
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }
}