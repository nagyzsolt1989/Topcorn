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
import com.nagy.zsolt.topcorn.data.TopcornDBHelper;
import com.nagy.zsolt.topcorn.data.TopcornContract;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    String[] mWatchist;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getSupportActionBar().setIcon(R.drawable.ic_laun);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

//        gridView = (GridView) findViewById(R.id.gridview);

        TopcornDBHelper dbHelper = new TopcornDBHelper(this);

        mContext = getApplicationContext();
        mWatchist = getWatchlist();

//        // Create the AccountHeader
//        AccountHeader headerResult = new AccountHeaderBuilder()
//                .withActivity(this)
//                .withHeaderBackground(R.layout.navigation_drawer_header)
//                .addProfiles(
//                        new ProfileDrawerItem().withName("Watchlist")
//                )
//                .build();
//
//        Drawer result = new DrawerBuilder()
//                .withActivity(this)
//                .withTranslucentStatusBar(false)
//                .withActionBarDrawerToggle(false)
//                .withAccountHeader(headerResult)
//                .build();
//
//        for (int i = 0; i < mWatchist.length; i++) {
//            System.out.println(mWatchist.length);
//            result.addItem(new PrimaryDrawerItem().withName(mWatchist[i]).withIdentifier(i).withSelectable(false));
//        }
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

        Cursor cursor =  mContext.getContentResolver().query(
                TopcornContract.WatchlistEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        String[] data = null;
        ArrayList<String> itemIds = new ArrayList<String>();

        while (cursor.moveToNext()) {
            String itemId = cursor.getString(cursor.getColumnIndex(TopcornContract.WatchlistEntry.COLUMN_MOVIE_TITLE));
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
