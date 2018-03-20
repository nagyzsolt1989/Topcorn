package com.nagy.zsolt.topcorn;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adroitandroid.chipcloud.ChipCloud;
import com.nagy.zsolt.topcorn.api.FetchDataListener;
import com.nagy.zsolt.topcorn.api.GETAPIRequest;
import com.nagy.zsolt.topcorn.api.RequestQueueService;
import com.nagy.zsolt.topcorn.model.Movie;
import com.nagy.zsolt.topcorn.model.MovieDetails;
import com.nagy.zsolt.topcorn.utils.JsonUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;
    public static final String EXTRA_JSONARRAY = "extra_jsonarray";
    JSONArray array;
    String jsonObject;
    Movie movie;
    MovieDetails movieDetails;

    @BindView(R.id.details_poster_iv) ImageView posterIV;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.movie_title) TextView mMovieTitle;
    @BindView(R.id.ratingbar) RatingBar mRatingBar;
    @BindView(R.id.movie_release_year) TextView mReleaseYear;
    @BindView(R.id.movie_duration) TextView mMovieRuntime;
    @BindView(R.id.movie_overview) TextView mMovieOverview;
    @BindView(R.id.movie_genre) ChipCloud mMovieGenre;
    @BindView(R.id.movie_tagline) TextView mMovieTagline;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        mContext = getApplicationContext();

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        int position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            closeOnError();
            return;
        }

        String jsonArray = intent.getStringExtra(EXTRA_JSONARRAY);
        try {
            array = new JSONArray(jsonArray);
            movie = JsonUtils.parseMovieJson(array.getJSONObject(position).toString());

            getMovieDetailsFromServer();

            System.out.print(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        collapsingToolbarLayout.setTitleEnabled(false);

        System.out.println("http://image.tmdb.org/t/p/w780/" + movie.getPosterPath());
        Picasso.with(this)
                .load("http://image.tmdb.org/t/p/w780/" + movie.getPosterPath())
                .into(posterIV);
    }

    private void populateUI(Movie movie, MovieDetails movieDetails) {
        if ((movie == null) || (movieDetails == null)) {
            // Movie data unavailable
            closeOnError();
            return;
        } else {
            String releaseDate = movie.getReleaseDate();
            String[] releaseDateParts = releaseDate.split("-");
            mMovieTitle.setText(movie.getTitle());
            mReleaseYear.setText(releaseDateParts[0]);
            mRatingBar.setRating(Float.parseFloat(movie.getVoteAvg()) / 2);
            mMovieRuntime.setText(movieDetails.getRuntime() + " min");
            mMovieOverview.setText(movie.getOverview());
//            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < movieDetails.getGenres().size(); i++) {
                mMovieGenre.addChip( movieDetails.getGenres().get(i));
            }
//            ArrayAdapter<String> genreAdapter =
//                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
//            mMovieGenre.setAdapter(genreAdapter);
//            for (int i = 0; i < movieDetails.getGenres().size(); i++) {
//
//                mMovieGenre.setText(mMovieGenre.getText() + movieDetails.getGenres().get(i) + "  ");
//            }
            mMovieTagline.setText(movieDetails.getTagline());

        }
    }

    public void getMovieDetailsFromServer() {

        try {
            //Create Instance of GETAPIRequest and call it's
            //request() method
            String ds = "";
            ds = "http://api.themoviedb.org/3/movie/"+movie.getId()+"?api_key=2154fef784c3948a8c2f2ef885f3d943";
            GETAPIRequest getapiRequest = new GETAPIRequest();
            getapiRequest.request(DetailActivity.this, fetchGetResultListener, ds);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
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
                    movieDetails = JsonUtils.parseMovieDetailsJson(data.toString());
                    populateUI(movie, movieDetails);
                } else {
                    RequestQueueService.showAlert("Error! No data fetched", DetailActivity.this);
                }
            } catch (
                    Exception e) {
                RequestQueueService.showAlert("Something went wrong", DetailActivity.this);
                e.printStackTrace();
            }

        }

        @Override
        public void onFetchFailure(String msg) {
            RequestQueueService.cancelProgressDialog();
            //Show if any error message is there called from GETAPIRequest class
            RequestQueueService.showAlert(msg, DetailActivity.this);
        }

        @Override
        public void onFetchStart() {
            //Start showing progressbar or any loader you have
            RequestQueueService.showProgressDialog(DetailActivity.this);
        }
    };

}
