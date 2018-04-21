package com.nagy.zsolt.topcorn;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adroitandroid.chipcloud.ChipCloud;
import com.nagy.zsolt.topcorn.api.FetchDataListener;
import com.nagy.zsolt.topcorn.api.GETAPIRequest;
import com.nagy.zsolt.topcorn.api.RequestQueueService;
import com.nagy.zsolt.topcorn.data.TopcornDBHelper;
import com.nagy.zsolt.topcorn.data.TopcornContract;
import com.nagy.zsolt.topcorn.model.Movie;
import com.nagy.zsolt.topcorn.model.MovieDetails;
import com.nagy.zsolt.topcorn.model.MovieReview;
import com.nagy.zsolt.topcorn.utils.CreditsAdapter;
import com.nagy.zsolt.topcorn.utils.JsonUtils;
import com.nagy.zsolt.topcorn.utils.ReviewsAdapter;
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

    private LinearLayoutManager mLayoutManager, mReviewsLayoutManager;
    boolean addToFavourites, addedToWatchlist;
    String[] profilePath, names, characters, trailers, ids, authors, contents;
    ArrayList<String> favouritesList, watchList;
    JSONArray creditsJsonArray, trailersJsonArray, reviewsJsonArray;
    JSONArray array;
    View mLoadingScreen;
    Movie movie;
    MovieDetails movieDetails;
    MovieReview movieReview;
    Drawable pinkFavourite, blackFavourite, blueWatchlist, blackWatchlist;
    Context mContext;

    @BindView(R.id.details_poster_iv)
    ImageView posterIV;
    //    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.movie_title)
    TextView mMovieTitle;
    @BindView(R.id.ratingbar)
    RatingBar mRatingBar;
    @BindView(R.id.movie_release_year)
    TextView mReleaseYear;
    @BindView(R.id.movie_duration)
    TextView mMovieRuntime;
    @BindView(R.id.trailer_poster)
    ImageView mTrailerPoster;
    @BindView(R.id.movie_overview)
    TextView mMovieOverview;
    @BindView(R.id.movie_genre)
    ChipCloud mMovieGenre;
    @BindView(R.id.button_play)
    FloatingActionButton mWatchTrailer;
    @BindView(R.id.button_favourite)
    FloatingActionButton mFavouriteFab;
    @BindView(R.id.button_watchlist)
    FloatingActionButton mWatchlistFab;
    @BindView(R.id.button_imdb)
    FloatingActionButton mIMDBFab;
    @BindView(R.id.credits_recycler_view)
    RecyclerView mCreditsRecyclerView;
    @BindView(R.id.reviews_recycler_view)
    RecyclerView mReviewsRecyclerView;
    @BindView(R.id.movie_tagline)
    TextView mMovieTagline;

    private static final String favoritedMovieNamesKey = "favoritedMovieNamesKey";
    final ArrayList<String> favoritedMovieNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        //Initialize variables
        mContext = getApplicationContext();
        pinkFavourite = getResources().getDrawable(R.drawable.ic_favorite_pink);
        blackFavourite = getResources().getDrawable(R.drawable.ic_favorite);
        blueWatchlist = getResources().getDrawable(R.drawable.ic_list_blue);
        blackWatchlist = getResources().getDrawable(R.drawable.ic_list);
        addToFavourites = false;
        addedToWatchlist = false;
        TopcornDBHelper dbHelper = new TopcornDBHelper(mContext);
        favouritesList = getFavourites();
        watchList = getWatchlist();

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

        /**
         * Get movie data from the server
         */
        try {
            array = new JSONArray(jsonArray);
            movie = JsonUtils.parseMovieJson(array.getJSONObject(position).toString());

            getMovieDetailsFromServer();
            getMovieTrailerFromServer();
            getMovieCreditsFromServer();
            getMovieReviewsFromServer();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Preserve Favourite Fab previous state
        if (favouritesList.contains(movie.getTitle())) {
            addToFavourites = true;
            mFavouriteFab.setImageDrawable(pinkFavourite);
        }

        if (watchList.contains(movie.getTitle())) {
            addedToWatchlist = true;
            mWatchlistFab.setImageDrawable(blueWatchlist);
        }

        //Preserve state on rotation
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("FAVOURITE_IS_CLICKED")) {
                if (savedInstanceState.getBoolean("FAVOURITE_IS_CLICKED"))
                    mFavouriteFab.setImageDrawable(pinkFavourite);
                else
                    mFavouriteFab.setImageDrawable(blackFavourite);
            }

        }

        //Build Poster Path URIs
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("image.tmdb.org")
                .appendPath("t")
                .appendPath("p")
                .appendPath("w780");
        String posterPathPrefix = builder.build().toString();

        //Load backrop and poster image
        Picasso.with(this)
                .load(posterPathPrefix + movie.getBackdropPath())
                .placeholder(R.drawable.ic_menu_gallery)
                .error(R.drawable.mrb_star_icon_black_36dp)
                .into(posterIV);

        Picasso.with(this)
                .load(posterPathPrefix + movie.getPosterPath())
                .placeholder(R.drawable.ic_menu_gallery)
                .error(R.drawable.mrb_star_icon_black_36dp)
                .into(mTrailerPoster);

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("FAVOURITE_IS_CLICKED", addToFavourites);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("FAVOURITE_IS_CLICKED")) {
                if (savedInstanceState.getBoolean("FAVOURITE_IS_CLICKED"))
                    mFavouriteFab.setImageDrawable(pinkFavourite);
                else
                    mFavouriteFab.setImageDrawable(blackFavourite);
            }

        }
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
            mMovieTagline.setAlpha(0.5f);

            mWatchTrailer.setAlpha(0.6f);
            mWatchTrailer.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(getString(R.string.youtube) + trailers[0]));
                    startActivity(intent);
                }
            });

            mFavouriteFab.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View view) {
                    if (!addToFavourites) {
                        addToFavourites = true;
                        Drawable pinkFavourite = getResources().getDrawable(R.drawable.ic_favorite_pink);
                        mFavouriteFab.setImageDrawable(pinkFavourite);
                        addToFavourites();
                        Toast.makeText(DetailActivity.this, movie.getTitle() + getString(R.string.addedToFavourites), Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = getSharedPreferences("Favourite state", MODE_PRIVATE).edit();
                        editor.putBoolean("State", addToFavourites);
                        editor.commit();
                    } else if (addToFavourites) {
                        addToFavourites = false;
                        removeFromFavourites();
                        Drawable blackFavourite = getResources().getDrawable(R.drawable.ic_favorite);
                        mFavouriteFab.setImageDrawable(blackFavourite);
                        SharedPreferences.Editor editor = getSharedPreferences("Favourite state", MODE_PRIVATE).edit();
                        editor.putBoolean("State", addToFavourites);
                        editor.commit();
                    }
                }
            });

            mWatchlistFab.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View view) {
                    if (!addedToWatchlist) {
                        addedToWatchlist = true;
                        blueWatchlist = getResources().getDrawable(R.drawable.ic_list_blue);
                        mWatchlistFab.setImageDrawable(blueWatchlist);
                        addToWatchlist();
                        SharedPreferences.Editor editor = getSharedPreferences("Watchlist state", MODE_PRIVATE).edit();
                        editor.putBoolean("State", addedToWatchlist);
                        editor.commit();
                        Toast.makeText(DetailActivity.this, movie.getTitle() + getString(R.string.addedToWatchlist), Toast.LENGTH_SHORT).show();
                    } else if (addedToWatchlist) {
                        addedToWatchlist = false;
                        removeFromWatchlist();
                        blackWatchlist = getResources().getDrawable(R.drawable.ic_list);
                        mWatchlistFab.setImageDrawable(blackWatchlist);
                        SharedPreferences.Editor editor = getSharedPreferences("Watchlist state", MODE_PRIVATE).edit();
                        editor.putBoolean("State", addedToWatchlist);
                        editor.commit();
                    }
                }
            });

            mIMDBFab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    System.out.println(movieDetails.getImdb_id());
                    intent.setData(Uri.parse(getString(R.string.imdb) + movieDetails.getImdb_id()));
                    startActivity(intent);
                }
            });

        }
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

    public void getMovieTrailerFromServer() {
        try {
            //Create Instance of GETAPIRequest and call it's
            //request() method
            String url = getString(R.string.movieDbApi) + movie.getId() + getString(R.string.trailers) + getString(R.string.apiKeyParameter) + mContext.getString(R.string.movie_db_api_key);
            System.out.println(url);
            GETAPIRequest getapiRequest = new GETAPIRequest();
            getapiRequest.request(DetailActivity.this, fetchTrailerResultListener, url);
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

    public void getMovieReviewsFromServer() {
        try {
            //Create Instance of GETAPIRequest and call it's
            //request() method
            String url = getString(R.string.movieDbApi) + movie.getId() + getString(R.string.reviews) + getString(R.string.apiKeyParameter) + mContext.getString(R.string.movie_db_api_key);
            System.out.println(url);
            GETAPIRequest getapiRequest = new GETAPIRequest();
            getapiRequest.request(DetailActivity.this, fetchReviewsResultListener, url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new movie to Favourites table
     */
    public void addToFavourites() {

        new android.os.AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                 //ContentValues instance to pass the values onto the insert query
                ContentValues cv = new ContentValues();

                cv.put(TopcornContract.FavouritesEntry.COLUMN_MOVIE_BACKDROP, movie.getBackdropPath());
                cv.put(TopcornContract.FavouritesEntry.COLUMN_MOVIE_POSTER, movie.getPosterPath());
                cv.put(TopcornContract.FavouritesEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
                cv.put(TopcornContract.FavouritesEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getReleaseDate());
                cv.put(TopcornContract.FavouritesEntry.COLUMN_MOVIE_RATING, movie.getVoteAvg());
                cv.put(TopcornContract.FavouritesEntry.COLUMN_MOVIE_DURATION, movieDetails.getRuntime());
                cv.put(TopcornContract.FavouritesEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
                cv.put(TopcornContract.FavouritesEntry.COLUMN_MOVIE_TAGLINE, movieDetails.getTagline());
                cv.put(TopcornContract.FavouritesEntry.COLUMN_MOVIE_IMDB, movieDetails.getImdb_id());

                mContext.getContentResolver().insert(
                                       TopcornContract.FavouritesEntry.CONTENT_URI,
                                       cv);
                return null;
                }
            }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);

    }

    /**
     * Removes the movie from Favourites
     */
    public void removeFromFavourites() {

        new android.os.AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

        String selection = TopcornContract.FavouritesEntry.COLUMN_MOVIE_TITLE + " LIKE ?";
        String[] selectionArgs = {movie.getTitle()};

        mContext.getContentResolver().delete(
                            TopcornContract.FavouritesEntry.CONTENT_URI,
                            selection,
                            selectionArgs);

                return null;
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);

    }

    /**
     * Adds a new movie to watchlist table
     */
    public void addToWatchlist() {

        new android.os.AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                //ContentValues instance to pass the values onto the insert query
                ContentValues cv = new ContentValues();
                cv.put(TopcornContract.WatchlistEntry.COLUMN_MOVIE_TITLE, movie.getTitle());

                mContext.getContentResolver().insert(
                                            TopcornContract.WatchlistEntry.CONTENT_URI,
                                            cv);
                    return null;
                }
            }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Removes the movie from watchlist
     */
    public void removeFromWatchlist() {

        new android.os.AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                String selection = TopcornContract.WatchlistEntry.COLUMN_MOVIE_TITLE + " LIKE ?";
                String[] selectionArgs = {movie.getTitle()};

                 mContext.getContentResolver().delete(
                                            TopcornContract.WatchlistEntry.CONTENT_URI,
                                            selection,
                                            selectionArgs);
                return null;
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Select all favourited movie titles
     */
    public ArrayList<String> getFavourites() {

        Cursor cursor =  mContext.getContentResolver().query(
                TopcornContract.FavouritesEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        String[] data = null;
        ArrayList<String> itemIds = new ArrayList<String>();

        while (cursor.moveToNext()) {
            String itemId = cursor.getString(cursor.getColumnIndex(TopcornContract.FavouritesEntry.COLUMN_MOVIE_TITLE));
            itemIds.add(itemId);
        }
        cursor.close();

        return itemIds;
    }

    /**
     * Select all movies on Watchlist
     */
    public ArrayList<String> getWatchlist() {

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

        return itemIds;
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

    FetchDataListener fetchTrailerResultListener = new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject data) {
            //Fetch Complete. Now stop progress bar  or loader
            //you started in onFetchStart
            RequestQueueService.cancelProgressDialog();
            try {
                //Check result sent by our GETAPIRequest class
                if (data != null) {
                    trailersJsonArray = data.getJSONArray("results");
//                    System.out.println(trailersJsonArray);
                    trailers = new String[trailersJsonArray.length()];
                    for (int i = 0; i < trailersJsonArray.length(); i++) {
                        JSONObject obj = trailersJsonArray.getJSONObject(i);
                        trailers[i] = obj.optString("key");
                    }
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
                    }
                    mCreditsRecyclerView.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                    mCreditsRecyclerView.setLayoutManager(mLayoutManager);
                    CreditsAdapter creditsAdapter = new CreditsAdapter(getApplicationContext(), profilePath, names, characters);
                    mCreditsRecyclerView.setAdapter(creditsAdapter);
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

    FetchDataListener fetchReviewsResultListener = new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject data) {
            //Fetch Complete. Now stop progress bar  or loader
            //you started in onFetchStart
            RequestQueueService.cancelProgressDialog();
            try {
                //Check result sent by our GETAPIRequest class
                if (data != null) {
                    reviewsJsonArray = data.getJSONArray("results");
                    System.out.println(reviewsJsonArray);
                    ids = new String[reviewsJsonArray.length()];
                    authors = new String[reviewsJsonArray.length()];
                    contents = new String[reviewsJsonArray.length()];
                    for (int i = 0; i < reviewsJsonArray.length(); i++) {
                        JSONObject obj = reviewsJsonArray.getJSONObject(i);
                        ids[i] = obj.optString("id");
                        authors[i] = obj.optString("author");
                        contents[i] = obj.optString("content");
                    }
                    mReviewsRecyclerView.setHasFixedSize(true);
                    mReviewsLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                    mReviewsRecyclerView.setLayoutManager(mReviewsLayoutManager);
                    ReviewsAdapter reviewsAdapter = new ReviewsAdapter(getApplicationContext(), ids, authors, contents);
                    mReviewsRecyclerView.setAdapter(reviewsAdapter);

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
