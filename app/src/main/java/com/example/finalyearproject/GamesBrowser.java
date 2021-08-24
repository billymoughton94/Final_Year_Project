package com.example.finalyearproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
Class derived from the following sources:

[36]"How to Parse a Json Using Volley - SIMPLE GET REQUEST - Android Studio Tutorial",
Youtube.com, 2017. [Online].
Available: https://www.youtube.com/watch?v=y2xtLqP8dSQ. [Accessed: 03- May- 2021].
 */

// this class displays each game in succession
public class GamesBrowser extends AppCompatActivity {
    User user;

    RecyclerView recyclerView;
    GamesAdaptor gamesAdaptor;
    List<Game> gamesList;
    String request = null;
    ProgressBar recProgress;
    TextView recProgressTxt;
    TextView recTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games_browser);
        recProgress = findViewById(R.id.recommendProgressBar);
        recProgressTxt = findViewById(R.id.recommendProgressTxt);
        recTxt = findViewById(R.id.recommendedGamesTxt);

        user = (User) getIntent().getSerializableExtra("user");
        if(user != null){
            user.getRatings(this); // populate the client side rating matrix of the user in the current session
        }

        gamesList = new ArrayList<>();
        recyclerView = findViewById(R.id.gamesRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        request = getIntent().getStringExtra("fetchType");

        // fetch certain data, depending on button clicked
        if(request.equals("recommend")){
            setTitle("Recommended Games");
            recommendGames();
        }

        else if(request.equals("%")){
            setTitle("Browse All");
            loadGames();
        }

        else{
            setTitle(request + " Games");
            loadGames();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(user != null){
            user.getRatings(this); // populate the client side rating matrix of the user in the current session
        }
    }

    // begin generating recommendations for current user
    private void recommendGames() {
        recTxt.setVisibility(View.GONE);
        recProgress.setVisibility(View.VISIBLE);
        recProgressTxt.setVisibility(View.VISIBLE);
        final String fetchGamesURL = "http://192.168.0.40/PHP_Scripts/Recommender/fetchRecommendations.php"; // TODO: CHANGE TO YOUR LOCAL IP ADDRESS
        StringRequest fetchRequest = new StringRequest(Request.Method.POST, fetchGamesURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    recProgress.setVisibility(View.GONE);
                    JSONObject json = new JSONObject(response);
                    String result = json.getString("result");

                    if(result.equals("success")){
                        recTxt.setVisibility(View.VISIBLE);
                        recProgressTxt.setVisibility(View.GONE);
                        JSONArray jsonGames = json.getJSONArray("games");
                        for(int i = 0; i < jsonGames.length(); i++){
                            JSONObject jsonGame = jsonGames.getJSONObject(i);
                            int gameID = jsonGame.getInt("gameID");
                            String name = jsonGame.getString("name");
                            String box_art = jsonGame.getString("box_art");
                            String release_date = jsonGame.getString("release_date");
                            String developer = jsonGame.getString("developer");
                            String genre = jsonGame.getString("genre");
                            String description = jsonGame.getString("description");
                            double avgRating = jsonGame.getDouble("avgRating");
                            String numRatings = jsonGame.getString("numRatings");
                            Game game = new Game(gameID, name, box_art, release_date, developer, genre, description, avgRating, numRatings);
                            gamesList.add(game);
                        }
                        gamesAdaptor = new GamesAdaptor(GamesBrowser.this, gamesList, user);
                        recyclerView.setAdapter(gamesAdaptor);
                    }

                    if(result.equals("null ratings")){
                        String msg = "No recommendations available. Please rate more games!";
                        recProgressTxt.setText(msg);
                    }
                }
                catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Could not display games", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not display games", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> post_data = new HashMap<String, String>();
                post_data.put("userID", Integer.toString(user.getUserID()));
                return post_data;
            }
        };
        Webhost_Connector.getInstance(this).addRequest(fetchRequest);
    }

    // fetches games from the database
    private void loadGames() 
    {
        recTxt.setVisibility(View.GONE);
        final String fetchGamesURL = "http://192.168.0.40/PHP_Scripts/fetchgames.php?genre=" + request; // TODO: CHANGE TO YOUR LOCAL IP ADDRESS
        StringRequest fetchRequest = new StringRequest(Request.Method.GET, fetchGamesURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.getString("result");

                    if(result.equals("success")){
                        JSONArray jsonGames = json.getJSONArray("games");
                        for(int i = 0; i < jsonGames.length(); i++){
                            JSONObject jsonGame = jsonGames.getJSONObject(i);
                            int gameID = jsonGame.getInt("gameID");
                            String name = jsonGame.getString("name");
                            String box_art = jsonGame.getString("box_art");
                            String release_date = jsonGame.getString("release_date");
                            String developer = jsonGame.getString("developer");
                            String genre = jsonGame.getString("genre");
                            String description = jsonGame.getString("description");
                            double avgRating = jsonGame.getDouble("avgRating");
                            String numRatings = jsonGame.getString("numRatings");
                            Game game = new Game(gameID, name, box_art, release_date, developer, genre, description, avgRating, numRatings);
                            gamesList.add(game);
                        }
                        gamesAdaptor = new GamesAdaptor(GamesBrowser.this, gamesList, user);
                        recyclerView.setAdapter(gamesAdaptor);
                    }
                }
                catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Could not display games", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Could not display games", Toast.LENGTH_LONG).show();
            }
        });
        Webhost_Connector.getInstance(this).addRequest(fetchRequest);
    }

    // method used for instantiating object that controls search filter
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gamerbase_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(true);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                gamesAdaptor.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_home){
            Intent intent = new Intent(this, HomePage.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }

        else if(item.getItemId() == R.id.action_logout){
            user = null;
            Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }
        return true;
    }
}