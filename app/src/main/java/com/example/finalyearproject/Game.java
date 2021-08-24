package com.example.finalyearproject;

import java.io.Serializable;

// each game will be stored as a Game class instance
public class Game implements Serializable {
    // fields which will contain metadata about the game
    private int gameID;
    private String name;
    private String box_art;
    private String release_date;
    private String developer;
    private String genre;
    private String description;
    private double averageRating;
    private String numRatings;

    public Game(int gameID, String name, String box_art, String release_date, String developer, String genre, String description, double avgRating, String numRatings) {
        this.gameID = gameID;
        this.name = name;
        this.box_art = box_art;
        this.release_date = release_date;
        this.developer = developer;
        this.genre = genre;
        this.description = description;
        this.averageRating = avgRating;
        this.numRatings = numRatings;
    }

    public int getGameID() {
        return gameID;
    }



    public String getName() {
        return name;
    }



    public String getBox_art() {
        return box_art;
    }



    public String getRelease_date() {
        return release_date;
    }



    public String getDeveloper() {
        return developer;
    }



    public String getGenre() {
        return genre;
    }



    public String getDescription() {
        return description;
    }



    public double getAverageRating() {
        return averageRating;
    }



    public String getNumRatings() {
        return numRatings;
    }

}
