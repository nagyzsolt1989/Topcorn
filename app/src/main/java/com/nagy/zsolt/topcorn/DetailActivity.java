package com.nagy.zsolt.topcorn;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adroitandroid.chipcloud.ChipCloud;
import com.nagy.zsolt.topcorn.api.FetchDataListener;
import com.nagy.zsolt.topcorn.api.GETAPIRequest;
import com.nagy.zsolt.topcorn.api.RequestQueueService;
import com.nagy.zsolt.topcorn.model.Movie;
import com.nagy.zsolt.topcorn.model.MovieCredits;
import com.nagy.zsolt.topcorn.model.MovieDetails;
import com.nagy.zsolt.topcorn.utils.CreditsAdapter;
import com.nagy.zsolt.topcorn.utils.JsonUtils;
import com.nagy.zsolt.topcorn.utils.MovieAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;
    public static final String EXTRA_JSONARRAY = "extra_jsonarray";

    private LinearLayoutManager mLayoutManager;
    String[] profilePath, names, characters;
    JSONArray creditsJsonArray;
    JSONArray array;
    String jsonObject;
    Movie movie;
    MovieDetails movieDetails;
    MovieCredits movieCredits;
    Context mContext;
    boolean addToFavourites = false, addedToWatchlist = false;

    @BindView(R.id.details_poster_iv) ImageView posterIV;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.movie_title) TextView mMovieTitle;
    @BindView(R.id.ratingbar) RatingBar mRatingBar;
    @BindView(R.id.movie_release_year) TextView mReleaseYear;
    @BindView(R.id.movie_duration) TextView mMovieRuntime;
    @BindView(R.id.movie_overview) TextView mMovieOverview;
    @BindView(R.id.movie_genre) ChipCloud mMovieGenre;
    @BindView(R.id.button_favourite) FloatingActionButton mFavouriteFab;
    @BindView(R.id.button_watchlist) FloatingActionButton mWatchlistFab;
    @BindView(R.id.button_imdb) FloatingActionButton mIMDBFab;
    @BindView(R.id.credits_recycler_view) RecyclerView mCreditsRecyclerView;
    @BindView(R.id.movie_tagline) TextView mMovieTagline;


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
            getMovieCreditsFromServer();

//            System.out.print(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        collapsingToolbarLayout.setTitleEnabled(false);

        Picasso.with(this)
                .load("http://image.tmdb.org/t/p/w780/" + movie.getPosterPath())
                .into(posterIV);
    }

    private void populateUI(final Movie movie, final MovieDetails movieDetails) {
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
            for (int i = 0; i < movieDetails.getGenres().size(); i++) {
                mMovieGenre.addChip(movieDetails.getGenres().get(i));
            }
            mMovieOverview.setText(movie.getOverview());
            mMovieTagline.setText(movieDetails.getTagline());
            mFavouriteFab.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View view) {
                    if (!addToFavourites) {
                        addToFavourites = true;
                        Drawable pinkFavourite = getResources().getDrawable(R.drawable.ic_favorite_pink);
                        mFavouriteFab.setImageDrawable(pinkFavourite);
                        Toast.makeText(DetailActivity.this, movie.getTitle() + getString(R.string.addedToFavourites), Toast.LENGTH_SHORT).show();
                    } else if (addToFavourites) {
                        addToFavourites = false;
                        Drawable blackFavourite = getResources().getDrawable(R.drawable.ic_favorite);
                        mFavouriteFab.setImageDrawable(blackFavourite);
                    }
                }
            });

            mWatchlistFab.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View view) {
                    if (!addedToWatchlist) {
                        addedToWatchlist = true;
                        Drawable blueWatchlist = getResources().getDrawable(R.drawable.ic_list_blue);
                        mWatchlistFab.setImageDrawable(blueWatchlist);
                        Toast.makeText(DetailActivity.this, movie.getTitle() + getString(R.string.addedToWatchlist), Toast.LENGTH_SHORT).show();
                    } else if (addedToWatchlist) {
                        addedToWatchlist = false;
                        Drawable blackFavourite = getResources().getDrawable(R.drawable.ic_list);
                        mWatchlistFab.setImageDrawable(blackFavourite);
                    }
                }
            });

            mIMDBFab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    System.out.println(movieDetails.getImdb_id());
                    intent.setData(Uri.parse(getString(R.string.imdb)+movieDetails.getImdb_id()));
                    startActivity(intent);
                }
            });

        }
    }

    private void populateCredits(MovieCredits movieCredits){

    }

    public void getMovieDetailsFromServer() {
        try {
            //Create Instance of GETAPIRequest and call it's
            //request() method
            String url = getString(R.string.movieDbApi) + movie.getId() + getString(R.string.apiKeyParameter) + mContext.getString(R.string.movie_db_api_key);
            System.out.println(url);
            GETAPIRequest getapiRequest = new GETAPIRequest();
            getapiRequest.request(DetailActivity.this, fetchGetResultListener, url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMovieCreditsFromServer() {
        try {
            //Create Instance of GETAPIRequest and call it's
            //request() method
            String url = getString(R.string.movieDbApi) + movie.getId() + getString(R.string.credits) + getString(R.string.apiKeyParameter) + mContext.getString(R.string.movie_db_api_key);
            System.out.println(url);
            GETAPIRequest getapiRequest = new GETAPIRequest();
            getapiRequest.request(DetailActivity.this, fetchCeditResultListener, url);
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
                    RequestQueueService.showAlert(getString(R.string.noDataAlert), DetailActivity.this);
                }
            } catch (
                    Exception e) {
                RequestQueueService.showAlert(getString(R.string.exceptionAlert), DetailActivity.this);
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

    FetchDataListener fetchCeditResultListener = new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject data) {
            //Fetch Complete. Now stop progress bar  or loader
            //you started in onFetchStart
            RequestQueueService.cancelProgressDialog();
            try {
                //Check result sent by our GETAPIRequest class
                if (data != null) {
                    System.out.println("Credits nem null");
                    creditsJsonArray = data.getJSONArray("cast");
                    System.out.println(creditsJsonArray);
                    profilePath = new String[creditsJsonArray.length()];
                    names = new String[creditsJsonArray.length()];
                    characters = new String[creditsJsonArray.length()];
                    for (int i = 0; i < creditsJsonArray.length(); i++) {
                        JSONObject obj = creditsJsonArray.getJSONObject(i);
                        profilePath[i] = obj.optString("profile_path");
                        names[i] = obj.optString("name");
                        characters[i] = obj.optString("character");
//                        System.out.println(profilePath[i]);
                    }
                    mCreditsRecyclerView.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
                    mCreditsRecyclerView.setLayoutManager(mLayoutManager);
                    CreditsAdapter creditsAdapter = new CreditsAdapter(getApplicationContext(), profilePath, names, characters);
                    mCreditsRecyclerView.setAdapter(creditsAdapter);

//                    mCreditsRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                            launchDetailActivity(position);
//                        }
//                    });

                } else {
                    RequestQueueService.showAlert(getString(R.string.noDataAlert), DetailActivity.this);
                }
            } catch (
                    Exception e) {
                RequestQueueService.showAlert(getString(R.string.exceptionAlert), DetailActivity.this);
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
