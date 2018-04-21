package com.nagy.zsolt.topcorn.data;

import android.net.Uri;
import android.content.ContentUris;
import android.provider.BaseColumns;

/**
 * Created by Zsolti on 2018.03.30..
 */

public class TopcornContract {

    public static final String CONTENT_AUTHORITY = "com.nagy.zsolt.topcorn";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVOURITES = "favourites";
    public static final String PATH_WATCHLIST = "watchlist";

    public static final class FavouritesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVOURITES)
                .build();

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String TABLE_NAME = "Favourites";
        public static final String COLUMN_MOVIE_BACKDROP = "backdrop";
        public static final String COLUMN_MOVIE_POSTER = "poster";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_MOVIE_TAGLINE = "tagline";
        public static final String COLUMN_MOVIE_DURATION = "duration";
        public static final String COLUMN_MOVIE_RATING = "rating";
        public static final String COLUMN_MOVIE_IMDB = "imdb";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

    public static final class WatchlistEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_WATCHLIST)
                .build();

        public static Uri buildMovieUri(long id) {
            return android.content.ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static final String TABLE_NAME = "Watchlist";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
