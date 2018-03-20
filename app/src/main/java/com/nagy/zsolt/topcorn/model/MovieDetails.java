package com.nagy.zsolt.topcorn.model;

import java.util.List;

/**
 * Created by nzs on 3/17/2018.
 */

public class MovieDetails {

    private String runtime;
    private String tagline;
    private List<String> genres;
    private String imdb_id;

    public MovieDetails() {
    }

    public MovieDetails(String runtime, String tagline, List<String> genres, String imdb_id) {
        this.runtime = runtime;
        this.tagline = tagline;
        this.genres = genres;
        this.imdb_id = imdb_id;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getImdb_id() {
        return imdb_id;
    }

    public void setImdb_id(String imdb_id) {
        this.imdb_id = imdb_id;
    }
}
