package com.nagy.zsolt.topcorn.utils;

import com.nagy.zsolt.topcorn.model.Movie;
import com.nagy.zsolt.topcorn.model.MovieCredits;
import com.nagy.zsolt.topcorn.model.MovieDetails;

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
        String backdropPath;
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
        final String KEY_BACKDROP_PATH = "backdrop_path";
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
            backdropPath = movieJson.optString(KEY_BACKDROP_PATH);
            posterPath = movieJson.optString(KEY_POSTER_PATH);
            originalLang = movieJson.optString(KEY_ORIGINAL_LANGUAGE);
            JSONArray genreJsonArray = movieJson.getJSONArray(KEY_GENRE_IDS);
            genreIds = convertJsonArrayToList(genreJsonArray);
            overview = movieJson.optString(KEY_OVERVIEW);
            releaseDate = movieJson.optString(KEY_RELEASE_DATE);

            return new Movie(title, vote_count, id, voteAvg, popularity, posterPath, backdropPath, originalLang, genreIds, overview, releaseDate);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static MovieDetails parseMovieDetailsJson(String data) {

        String runtime;
        String tagline;
        List<String> genres;
        String imdb_id;

        final String KEY_RUNTIME = "runtime";
        final String KEY_TAGLINE = "tagline";
        final String KEY_GENRES = "genres";
        final String KEY_IMDB_ID = "imdb_id";

        try {

            JSONObject movieDetailsJson = new JSONObject(data);
            genres = new ArrayList<>();

            runtime = movieDetailsJson.optString(KEY_RUNTIME);
            tagline = movieDetailsJson.optString(KEY_TAGLINE);
            JSONArray genreJsonArray = movieDetailsJson.getJSONArray(KEY_GENRES);
            for (int i = 0; i < genreJsonArray.length(); i++) {
                genres.add(genreJsonArray.getJSONObject(i).getString("name"));
            }
            imdb_id = movieDetailsJson.optString(KEY_IMDB_ID);

            return new MovieDetails(runtime, tagline, genres, imdb_id);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static MovieCredits parseMovieCreditsJson(String data) {

        String profilePath;
        String name;
        String character;

        final String KEY_PROFILE_PATH = "profile_path";
        final String KEY_ACTOR_NAME = "name";
        final String KEY_CHARACTER_NAME = "character";

        try {

            JSONObject movieJson = new JSONObject(data);

            profilePath = movieJson.optString(KEY_PROFILE_PATH);
            name = movieJson.optString(KEY_ACTOR_NAME);
            character = movieJson.optString(KEY_CHARACTER_NAME);

            return new MovieCredits(profilePath, name, character);

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
