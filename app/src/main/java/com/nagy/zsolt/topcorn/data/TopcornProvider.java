package com.nagy.zsolt.topcorn.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.nagy.zsolt.topcorn.data.TopcornDBHelper.*;

public class TopcornProvider extends ContentProvider {

    public static final int CODE_FAVOURITES = 100;
    public static final int CODE_WATCHLIST = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private TopcornDBHelper mOpenHelper;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TopcornContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, TopcornContract.PATH_FAVOURITES, CODE_FAVOURITES);
        matcher.addURI(authority, TopcornContract.PATH_WATCHLIST, CODE_WATCHLIST);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new TopcornDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        Cursor cursor = null;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CODE_FAVOURITES:
                queryBuilder.setTables(TopcornContract.FavouritesEntry.TABLE_NAME);
                break;
            case CODE_WATCHLIST:
                queryBuilder.setTables(TopcornContract.WatchlistEntry.TABLE_NAME);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        cursor = queryBuilder.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case CODE_FAVOURITES: {
                long id = db.insert(TopcornContract.FavouritesEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = TopcornContract.FavouritesEntry.buildMovieUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            }
            case CODE_WATCHLIST: {
                long id = db.insert(TopcornContract.WatchlistEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = TopcornContract.WatchlistEntry.buildMovieUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnUri;


    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] strings) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (null == selection) {
            selection = "1";
        }
        switch (match) {
            case CODE_FAVOURITES:
                rowsDeleted = db.delete(
                        TopcornContract.FavouritesEntry.TABLE_NAME, selection, strings);
                break;
            case CODE_WATCHLIST:
                rowsDeleted = db.delete(
                        TopcornContract.WatchlistEntry.TABLE_NAME, selection, strings);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
