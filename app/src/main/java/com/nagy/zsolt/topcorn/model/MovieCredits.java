package com.nagy.zsolt.topcorn.model;

import java.util.List;

/**
 * Created by Zsolti on 2018.03.26..
 */

public class MovieCredits {

    private String profilePath;
    private String name;
    private String character;

    public MovieCredits() {
    }

    public MovieCredits(String profilePath, String name, String character) {
        this.profilePath = profilePath;
        this.name = name;
        this.character = character;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }
}
