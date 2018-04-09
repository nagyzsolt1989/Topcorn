package com.nagy.zsolt.topcorn.data;

import android.provider.BaseColumns;

/**
 * Created by Zsolti on 2018.04.09..
 */

public class WatchlistContract {

    public static final class WatchlistEntry implements BaseColumns {

        public static final String TABLE_NAME = "Watchlist";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
