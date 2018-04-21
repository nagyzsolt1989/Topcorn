package com.nagy.zsolt.topcorn;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.nagy.zsolt.topcorn.data.TopcornDBHelper;
import com.nagy.zsolt.topcorn.data.TopcornContract;
import com.nagy.zsolt.topcorn.utils.MovieAdapter;

import java.util.ArrayList;

/**
 * Created by Zsolti on 2018.03.19..
 */

public class FavouriteMovies extends Fragment {

    GridView gridView;
    Context mContext;
    String[] moviePosterPath;
    static MovieAdapter movieAdapter;
    Parcelable state, restore;

    public FavouriteMovies() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();

        TopcornDBHelper dbHelper = new TopcornDBHelper(mContext);
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
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                launchDetailActivity(position);
//            }
//        });
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        moviePosterPath = getFavourites();
        movieAdapter = new MovieAdapter(getContext(), moviePosterPath);
        gridView.setAdapter(movieAdapter);
        if(restore != null){
            gridView.onRestoreInstanceState(restore);
        }
    }

    public String[] getFavourites(){

        Cursor cursor =  mContext.getContentResolver().query(
                TopcornContract.FavouritesEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        String[] data      = null;
        ArrayList<String> itemIds = new ArrayList<String>();



        while(cursor.moveToNext()) {
                String itemId = cursor.getString(cursor.getColumnIndex(TopcornContract.FavouritesEntry.COLUMN_MOVIE_POSTER));
                itemIds.add(itemId);
            }
        cursor.close();

        data = itemIds.toArray(new String[0]);
        return data;

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        state = gridView.onSaveInstanceState();
        outState.putParcelable("STATE", state);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            restore = savedInstanceState.getParcelable("STATE");
        }
    }

    private void launchDetailActivity(int position) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_POSITION, position);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.fade_out);
    }

}