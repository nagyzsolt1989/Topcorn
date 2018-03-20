package com.nagy.zsolt.topcorn;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.nagy.zsolt.topcorn.api.FetchDataListener;
import com.nagy.zsolt.topcorn.api.GETAPIRequest;
import com.nagy.zsolt.topcorn.api.RequestQueueService;
import com.nagy.zsolt.topcorn.utils.MovieAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    String[] moviePosterPath;
    JSONArray moviesJsonArray;
    Context mContext;

    ArrayList<String> list = new ArrayList<String>();
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

//        gridView = (GridView) findViewById(R.id.gridview);

        mContext = getApplicationContext();
//        getPopularMovies();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PopularMovies(), "Popular");
        adapter.addFragment(new TopRatedMovies(), "Top Rated");
        adapter.addFragment(new FavouriteMovies(), "Favourites");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void getPopularMovies() {

        try {
            //Create Instance of GETAPIRequest and call it's
            //request() method
            String url =  "http://api.themoviedb.org/3/movie/popular?api_key="+mContext.getString(R.string.movie_db_api_key);
            GETAPIRequest getapiRequest = new GETAPIRequest();
            getapiRequest.request(MainActivity.this, fetchGetResultListener, url);
            Toast.makeText(MainActivity.this, "GET API called", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Implementing interfaces of FetchDataListener for GET api request
    FetchDataListener fetchGetResultListener = new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject data) {
            //Fetch Complete. Now stop progress bar  or loader
            //you started in onFetchStart
            RequestQueueService.cancelProgressDialog();
            try {
                //Check result sent by our GETAPIRequest class
                if (data != null) {
                    moviesJsonArray = data.getJSONArray("results");
                    moviePosterPath = new String[moviesJsonArray.length()];
                    for (int i = 0; i < moviesJsonArray.length(); i++) {
                        JSONObject obj = moviesJsonArray.getJSONObject(i);
                        moviePosterPath[i] = obj.optString("poster_path");
                    }
                    MovieAdapter movieAdapter = new MovieAdapter(mContext, moviePosterPath);
                    gridView.setAdapter(movieAdapter);
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            launchDetailActivity(position);
                        }
                    });

                } else {
                    RequestQueueService.showAlert("Error! No data fetched", MainActivity.this);
                }
            } catch (
                    Exception e) {
                RequestQueueService.showAlert("Something went wrong", MainActivity.this);
                e.printStackTrace();
            }

        }

        @Override
        public void onFetchFailure(String msg) {
            RequestQueueService.cancelProgressDialog();
            //Show if any error message is there called from GETAPIRequest class
            RequestQueueService.showAlert(msg, MainActivity.this);
        }

        @Override
        public void onFetchStart() {
            //Start showing progressbar or any loader you have
            RequestQueueService.showProgressDialog(MainActivity.this);
        }
    };

    private void launchDetailActivity(int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_POSITION, position);
        intent.putExtra(DetailActivity.EXTRA_JSONARRAY, moviesJsonArray.toString());
        startActivity(intent);
    }
}
