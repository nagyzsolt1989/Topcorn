package com.nagy.zsolt.topcorn.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Zsolti on 2018.03.30..
 */

public class TopcornDBHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "topcorn.db";

    private static final int DATABASE_VERSION = 2;

    public TopcornDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAVOURITES_TABLE = "CREATE TABLE " + TopcornContract.FavouritesEntry.TABLE_NAME + " (" +
                TopcornContract.FavouritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TopcornContract.FavouritesEntry.COLUMN_MOVIE_BACKDROP + " TEXT NOT NULL, " +
                TopcornContract.FavouritesEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, " +
                TopcornContract.FavouritesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                TopcornContract.FavouritesEntry.COLUMN_MOVIE_RELEASE_DATE + " DATE NOT NULL, " +
                TopcornContract.FavouritesEntry.COLUMN_MOVIE_RATING + " TEXT NOT NULL, " +
                TopcornContract.FavouritesEntry.COLUMN_MOVIE_DURATION + " INTEGER NOT NULL, " +

                TopcornContract.FavouritesEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                TopcornContract.FavouritesEntry.COLUMN_MOVIE_TAGLINE + " TEXT NOT NULL, " +
                TopcornContract.FavouritesEntry.COLUMN_MOVIE_IMDB + " TEXT NOT NULL, " +
                TopcornContract.FavouritesEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        final String SQL_CREATE_WATCHLIST_TABLE = "CREATE TABLE " + TopcornContract.WatchlistEntry.TABLE_NAME + " (" +
                TopcornContract.WatchlistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TopcornContract.WatchlistEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                TopcornContract.WatchlistEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_WATCHLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TopcornContract.FavouritesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TopcornContract.WatchlistEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
