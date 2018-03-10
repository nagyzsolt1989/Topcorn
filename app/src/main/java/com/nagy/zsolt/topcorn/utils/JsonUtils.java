package com.nagy.zsolt.topcorn.utils;

import com.nagy.zsolt.topcorn.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zsolti on 2018.03.06..
 */

public class JsonUtils {

    public static Movie parseMovieJson(String json) {

        String title;
        String vote_count;
        String id;
        String voteAvg;
        String popularity;
        String posterPath;
        String originalLang;
        List<String> genreIds;
        String overview;
        String releaseDate;

        final String KEY_TITLE = "title";
        final String KEY_VOTE_COUNT = "vote_count";
        final String KEY_ID = "id";
        final String KEY_VOTE_AVERAGE = "vote_average";
        final String KEY_POPULARITY = "popularity";
        final String KEY_POSTER_PATH = "poster_path";
        final String KEY_ORIGINAL_LANGUAGE = "original_language";
        final String KEY_GENRE_IDS = "genre_ids";
        final String KEY_OVERVIEW = "overview";
        final String KEY_RELEASE_DATE = "release_date";

        try {

            JSONObject movieJson = new JSONObject(json);

            title = movieJson.optString(KEY_TITLE);
            vote_count = movieJson.optString(KEY_VOTE_COUNT);
            id = movieJson.optString(KEY_ID);
            voteAvg = movieJson.optString(KEY_VOTE_AVERAGE);
            popularity = movieJson.optString(KEY_POPULARITY);
            posterPath = movieJson.optString(KEY_POSTER_PATH);
            originalLang = movieJson.optString(KEY_ORIGINAL_LANGUAGE);
            JSONArray genreJsonArray = movieJson.getJSONArray(KEY_GENRE_IDS);
            genreIds = convertJsonArrayToList(genreJsonArray);
            overview = movieJson.optString(KEY_OVERVIEW);
            releaseDate = movieJson.optString(KEY_RELEASE_DATE);

            return new Movie(title, vote_count, id, voteAvg, popularity, posterPath, originalLang, genreIds, overview, releaseDate);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ArrayList<String> convertJsonArrayToList(JSONArray jsonArray) {
        ArrayList<String> list = new ArrayList<>();
        if (jsonArray == null) {
            return null;
        }
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                list.add(jsonArray.get(i).toString());
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
