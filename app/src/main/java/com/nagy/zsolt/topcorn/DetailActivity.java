package com.nagy.zsolt.topcorn;

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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nagy.zsolt.topcorn.model.Movie;
import com.nagy.zsolt.topcorn.utils.JsonUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;
    public static final String EXTRA_JSONARRAY = "extra_jsonarray";
    JSONArray array;
    Movie movie;

    @BindView(R.id.details_poster_iv) ImageView posterIV;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.my_toolbar) Toolbar toolbar;
    @BindView(R.id.movie_title) TextView mMovieTitle;
    @BindView(R.id.movie_overview) TextView mMovieOverview;
    @BindView(R.id.ratingBar) RatingBar mRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);


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
            System.out.println(array.getJSONObject(position).toString(2));
            movie = JsonUtils.parseMovieJson(array.getJSONObject(position).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        collapsingToolbarLayout.setTitleEnabled(false);
        toolbar.setTitle(movie.getTitle());

        System.out.println("http://image.tmdb.org/t/p/w780/"+movie.getPosterPath());
        Picasso.with(this)
                .load("http://image.tmdb.org/t/p/w780/"+movie.getPosterPath())
                .into(posterIV);

        populateUI(movie);
    }

    private void populateUI(Movie movie) {
        if (movie == null) {
            // Movie data unavailable
            closeOnError();
            return;
        } else {
            String releaseDate = movie.getReleaseDate();
            LayerDrawable stars = (LayerDrawable) mRatingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
            String[] releaseDateParts = releaseDate.split("-");
            mMovieTitle.setText(movie.getTitle() + " (" + releaseDateParts[0] + ")");
            mMovieOverview.setText(movie.getOverview());
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

}
