package com.nagy.zsolt.topcorn;

/**
 * Created by Zsolti on 2018.03.19..
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.nagy.zsolt.topcorn.api.FetchDataListener;
import com.nagy.zsolt.topcorn.api.GETAPIRequest;
import com.nagy.zsolt.topcorn.api.RequestQueueService;
import com.nagy.zsolt.topcorn.utils.MovieAdapter;

import org.json.JSONArray;
import org.json.JSONObject;


public class PopularMovies extends Fragment {

    String[] moviePosterPath;
    JSONArray moviesJsonArray;
    Context mContext;
    GridView gridView;
    MovieAdapter movieadapter;

    public PopularMovies() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        getPopularMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_popular_movies, container, false);
        gridView = layout.findViewById(R.id.popular_gridview);
        getPopularMovies();
        return layout;
    }

    public void getPopularMovies() {

        try {
            //Create Instance of GETAPIRequest and call it's
            //request() method
            String url ="http://api.themoviedb.org/3/movie/popular?api_key="+mContext.getString(R.string.movie_db_api_key);
            System.out.println(url);
            GETAPIRequest getapiRequest = new GETAPIRequest();
            getapiRequest.request(getContext(), fetchGetResultListener, url);
//            Toast.makeText(getContext(), "GET API called", Toast.LENGTH_SHORT).show();
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
                    RequestQueueService.showAlert("Error! No data fetched", getActivity());
                }
            } catch (
                    Exception e) {
                RequestQueueService.showAlert("Something went wrong", getActivity());
                e.printStackTrace();
            }

        }

        @Override
        public void onFetchFailure(String msg) {
            RequestQueueService.cancelProgressDialog();
            //Show if any error message is there called from GETAPIRequest class
            RequestQueueService.showAlert(msg, getActivity());
        }

        @Override
        public void onFetchStart() {
            //Start showing progressbar or any loader you have
            RequestQueueService.showProgressDialog(getActivity());
        }
    };

    private void launchDetailActivity(int position) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_POSITION, position);
        intent.putExtra(DetailActivity.EXTRA_JSONARRAY, moviesJsonArray.toString());
        startActivity(intent);
    }

}