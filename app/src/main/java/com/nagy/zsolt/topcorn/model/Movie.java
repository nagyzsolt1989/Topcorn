package com.nagy.zsolt.topcorn.model;

import java.util.List;

/**
 * Created by Zsolti on 2018.03.06..
 */

public class Movie {

    private String title;
    private String vote_count;
    private String id;
    private String voteAvg;
    private String popularity;
    private String posterPath;
    private String backdropPath;
    private String originalLang;
    private List<String> genreIds;
    private String overview;
    private String releaseDate;

    /**
     * No args constructor for use in serialization
     */
    public Movie() {
    }

    public Movie(String title, String vote_count, String id, String voteAvg, String popularity, String posterPath,String backdropPath, String originalLang,
                 List<String> genreIds, String overview, String releaseDate) {
        this.title = title;
        this.vote_count = vote_count;
        this.id = id;
        this.voteAvg = voteAvg;
        this.popularity = popularity;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.originalLang = originalLang;
        this.genreIds = genreIds;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVote_count() {
        return vote_count;
    }

    public void setVote_count(String vote_count) {
        this.vote_count = vote_count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVoteAvg() {
        return voteAvg;
    }

    public void setVoteAvg(String voteAvg) {
        this.voteAvg = voteAvg;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getOriginalLang() {
        return originalLang;
    }

    public void setOriginalLang(String originalLang) {
        this.originalLang = originalLang;
    }

    public List<String> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<String> genreIds) {
        this.genreIds = genreIds;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
