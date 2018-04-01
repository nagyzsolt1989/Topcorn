package com.nagy.zsolt.topcorn.data;

import android.provider.BaseColumns;

/**
 * Created by Zsolti on 2018.03.30..
 */

public class FavourtiesContract {

    public static final class FavouritesEntry implements BaseColumns {

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
}
