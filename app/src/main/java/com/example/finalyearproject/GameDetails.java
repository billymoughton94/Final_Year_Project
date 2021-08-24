package com.example.finalyearproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import java.text.DecimalFormat;

// this class displays all information about the currently selected game. Also allows a user to rate the game.
public class GameDetails extends AppCompatActivity implements View.OnClickListener {
    Game game;
    User user;
    ImageView boxart;
    TextView gameTitle;
    Button avgRatingBtn;
    TextView numRatingsTV;
    TextView previouslyRated;
    ExpandableTextView expandableTV;
    TextView gameGenre;
    TextView gameDeveloper;
    TextView gameReleaseDate;
    RatingBar gameRating;
    Button submitRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);
        setTitle("Game Details");

        game = (Game) getIntent().getSerializableExtra("game");
        user = (User) getIntent().getSerializableExtra("user");

        boxart = findViewById(R.id.gameBoxArt);
        gameTitle = findViewById(R.id.gameTitle);
        avgRatingBtn = findViewById(R.id.avgRatingBtn);
        numRatingsTV = findViewById(R.id.numRatings);
        previouslyRated = findViewById(R.id.previouslyRated);
        expandableTV = findViewById(R.id.expand_text_view);
        gameGenre = findViewById(R.id.gameGenre);
        gameDeveloper = findViewById(R.id.gameDeveloper);
        gameReleaseDate = findViewById(R.id.gameReleaseDate);
        gameRating = findViewById(R.id.gameRating);
        submitRating = findViewById(R.id.submitRating);
        submitRating.setOnClickListener(this);

        DecimalFormat ratingF = new DecimalFormat("0.#");

        double avgRatingDouble = game.getAverageRating();
        avgRatingBtn.setText(ratingF.format(avgRatingDouble));
        numRatingsTV.setText("Average rating from " + game.getNumRatings() + " users");

        if(user.ratedGames.containsKey(game.getGameID())){
            double rating = user.ratedGames.get(game.getGameID());
            String text = "You previously rated this title " + ratingF.format(rating) + " out of 5 stars";
            previouslyRated.setText(text);
            previouslyRated.setVisibility(View.VISIBLE);
            gameRating.setRating((float)rating);
        }
        else{
            previouslyRated.setVisibility(View.GONE);
        }
        // SET VALUES FROM THE GAME OBJECT
        Glide.with(getApplicationContext())
                .load(game.getBox_art())
                .into(boxart);
        gameTitle.setText(game.getName());
        expandableTV.setText("Description: " + game.getDescription().replaceAll("\uFFFD", "")); // replaces unicode Replacement Character (black diamond with '?') with empty string
        gameGenre.append(game.getGenre());
        gameDeveloper.append(game.getDeveloper());
        gameReleaseDate.append(game.getRelease_date());
    }

    @Override
    public void onClick(View view) {
        // if submit rating button clicked, upload the rating to the database
        if(view.getId() == R.id.submitRating){
            String rating = Float.toString(gameRating.getRating());
            user.rateGame(Integer.toString(game.getGameID()), rating, this);

            onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gamerbase_menu, menu);
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