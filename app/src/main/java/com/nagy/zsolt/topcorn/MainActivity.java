package com.nagy.zsolt.topcorn;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.nagy.zsolt.topcorn.data.FavouritesDBHelper;
import com.nagy.zsolt.topcorn.data.FavourtiesContract;
import com.nagy.zsolt.topcorn.data.WatchlistContract;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends AppCompatActivity {

    public static SQLiteDatabase mDb;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    String[] mWatchist;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Display app logo
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Topcorn");
        getSupportActionBar().setLogo(R.drawable.ic_menu_camera);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

//        gridView = (GridView) findViewById(R.id.gridview);

        FavouritesDBHelper dbHelper = new FavouritesDBHelper(this);
        mDb = dbHelper.getWritableDatabase();

        mContext = getApplicationContext();
        mWatchist = getWatchlist();

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .addProfiles(
                        new ProfileDrawerItem().withName("Watchlist")
                )
                .build();

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(false)
                .withAccountHeader(headerResult)
                .build();

        for (int i = 0; i < mWatchist.length; i++) {
            System.out.println("Ezek a favouritek:" + mWatchist[i]);
            System.out.println(mWatchist.length);
            result.addItem(new PrimaryDrawerItem().withName(mWatchist[i]).withIdentifier(i).withSelectable(false));
        }
    }

    /**
     * Display the category fragments in the tabs of the MainActivity
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PopularMovies(), "Popular");
        adapter.addFragment(new TopRatedMovies(), "Top Rated");
        adapter.addFragment(new FavouriteMovies(), "Favourites");
        viewPager.setAdapter(adapter);
    }

    /**
     * Select all movies on Watchlist
     */
    public String[] getWatchlist() {


        String selectQuery = "SELECT " + WatchlistContract.WatchlistEntry.COLUMN_MOVIE_TITLE + " FROM " + WatchlistContract.WatchlistEntry.TABLE_NAME;
        Cursor cursor = mDb.rawQuery(selectQuery, null);
        String[] data = null;
        ArrayList<String> itemIds = new ArrayList<String>();

        while (cursor.moveToNext()) {
            String itemId = cursor.getString(cursor.getColumnIndex(WatchlistContract.WatchlistEntry.COLUMN_MOVIE_TITLE));
            itemIds.add(itemId);
        }
        cursor.close();

        data = itemIds.toArray(new String[0]);
        return data;
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
}
