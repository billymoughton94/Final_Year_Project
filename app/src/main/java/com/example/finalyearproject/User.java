package com.example.finalyearproject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {

    private int userID;
    private String username;
    public HashMap<Integer, Double> ratedGames; // gameID, rating values for user (USER-RATING vector implementation)

    public User(int userID, String username) {
        this.userID = userID;
        this.username = username;
        ratedGames = new HashMap<Integer, Double>();
    }

    public int getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    // this method adds a new row to the rating table in the database with the current userID, gameID and rating value
    public void rateGame(final String gameID, final String rating, final Context ctx){
        final String rateGamesURL = "http://192.168.0.40/PHP_Scripts/rategame.php"; // TODO: CHANGE TO YOUR LOCAL IP ADDRESS
        StringRequest rateRequest = new StringRequest(Request.Method.POST, rateGamesURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                    String result = json.getString("result");
                    if(result.equals("failure")){
                        Toast.makeText(ctx, "Error submitting rating to database", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(ctx, "Rating submitted!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> post_data = new HashMap<String, String>();
                post_data.put("gameID", gameID);
                post_data.put("rating", rating);
                post_data.put("userID", Integer.toString(userID));
                return post_data;
            }
        };
        Webhost_Connector.getInstance(ctx).addRequest(rateRequest);
    }

    // this method updates the rating vector of the current user from what is contained in the database
    public void getRatings(final Context ctx){
        ratedGames.clear();
        final String fetchRatingsURL = "http://192.168.0.40/PHP_Scripts/fetchratings.php"; // TODO: CHANGE TO YOUR LOCAL IP ADDRESS
        StringRequest ratingFecthRequest = new StringRequest(Request.Method.POST, fetchRatingsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.getString("result");

                    if(result.equals("success")){
                        JSONArray user_ratings = json.getJSONArray("gameRatings");
                        for(int i = 0; i < user_ratings.length(); i++){
                            int gameID = user_ratings.getJSONObject(i).getInt("gameID");
                            double rating = user_ratings.getJSONObject(i).getDouble("rating");
                            ratedGames.put(gameID, rating);
                        }
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> post_data = new HashMap<String, String>();
                post_data.put("userID", Integer.toString(userID));
                return post_data;
            }
        };
        Webhost_Connector.getInstance(ctx).addRequest(ratingFecthRequest);




    }
}
