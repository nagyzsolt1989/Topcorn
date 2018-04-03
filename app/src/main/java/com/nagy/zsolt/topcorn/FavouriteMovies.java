package com.nagy.zsolt.topcorn;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.nagy.zsolt.topcorn.data.FavouritesDBHelper;
import com.nagy.zsolt.topcorn.data.FavourtiesContract;
import com.nagy.zsolt.topcorn.utils.MovieAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zsolti on 2018.03.19..
 */

public class FavouriteMovies extends Fragment {

    GridView gridView;
    Context mContext;
    SQLiteDatabase mDb;
    String[] moviePosterPath;
    static MovieAdapter movieAdapter;

    public FavouriteMovies() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();

        FavouritesDBHelper dbHelper = new FavouritesDBHelper(mContext);
        mDb = dbHelper.getReadableDatabase();

        moviePosterPath = getFavourites();

        for (int i = 0; i < moviePosterPath.length; i++) {
            System.out.println("Oncreate:" + moviePosterPath[i]);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View layout = inflater.inflate(R.layout.fragment_favourite_movies, container, false);
        gridView = layout.findViewById(R.id.favourites_gridview);

//        for (int i = 0; i < moviePosterPath.length; i++) {
//            System.out.println("Ez van benne" + moviePosterPath[i]);
//        }

        movieAdapter = new MovieAdapter(getContext(), moviePosterPath);
        gridView.setAdapter(movieAdapter);

        movieAdapter.notifyDataSetChanged();
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                            @Override
//                                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                                                launchDetailActivity(position);
//                                            }
//                                        }
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        moviePosterPath = getFavourites();
        movieAdapter = new MovieAdapter(getContext(), moviePosterPath);
        gridView.setAdapter(movieAdapter);

//        movieAdapter.notifyDataSetChanged();
//        gridView.setAdapter(movieAdapter);
    }

    public String[] getFavourites(){


        String selectQuery = "SELECT " + FavourtiesContract.FavouritesEntry.COLUMN_MOVIE_POSTER +  " FROM " + FavourtiesContract.FavouritesEntry.TABLE_NAME;
        Cursor cursor      = mDb.rawQuery(selectQuery, null);
        String[] data      = null;
        ArrayList<String> itemIds = new ArrayList<String>();



        while(cursor.moveToNext()) {
//                System.out.println("ez van a cursorban" + cursor.getString(0));
                String itemId = cursor.getString(cursor.getColumnIndex(FavourtiesContract.FavouritesEntry.COLUMN_MOVIE_POSTER));
                itemIds.add(itemId);
            }
        cursor.close();

        data = itemIds.toArray(new String[0]);
        return data;

    }
}