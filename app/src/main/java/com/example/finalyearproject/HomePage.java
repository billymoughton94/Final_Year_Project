package com.example.finalyearproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/*
Class derived from the following sources:

[34]"How to Add a Toolbar - Android Studio Tutorial",
Youtube.com, 2017. [Online].
Available: https://www.youtube.com/watch?v=DMkzIOLppf4. [Accessed: 03- May- 2021].
 */


// this class acts as the landing page, where users can choose from a range of genres to search or even begin getting recommendations
public class HomePage extends AppCompatActivity implements View.OnClickListener{
    User user;

    TextView welcomeMsg;

    Button browseBtn;
    Button recommendBtn;
    Button actionBtn;
    Button adventureBtn;
    Button indieBtn;
    Button strategyBtn;
    Button simulationBtn;
    Button rpgBtn;
    Button casualBtn;
    Button racingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        setTitle("Home");
        user = (User) getIntent().getSerializableExtra("user");
        welcomeMsg = findViewById(R.id.welcomeMsg);
        String msg = "Welcome " +  user.getUsername() + ".\nPlease choose an option below";
        welcomeMsg.setText(msg);

        browseBtn = findViewById(R.id.browseButton);
        recommendBtn = findViewById(R.id.myRecommendationsButton);
        actionBtn = findViewById(R.id.actionButton);
        adventureBtn = findViewById(R.id.adventureButton);
        indieBtn = findViewById(R.id.indieButton);
        strategyBtn = findViewById(R.id.strategyButton);
        simulationBtn = findViewById(R.id.simulationButton);
        rpgBtn = findViewById(R.id.rpgButton);
        casualBtn = findViewById(R.id.casualButton);
        racingBtn = findViewById(R.id.racingButton);

        browseBtn.setOnClickListener(this);
        recommendBtn.setOnClickListener(this);
        actionBtn.setOnClickListener(this);
        adventureBtn.setOnClickListener(this);
        indieBtn.setOnClickListener(this);
        strategyBtn.setOnClickListener(this);
        simulationBtn.setOnClickListener(this);
        rpgBtn.setOnClickListener(this);
        casualBtn.setOnClickListener(this);
        racingBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, GamesBrowser.class);
        intent.putExtra("user", user);
        String request = null;
        switch(view.getId()){
            case R.id.browseButton:
                request = "%";
                break;
            case R.id.myRecommendationsButton:
                request = "recommend";
                break;
            case R.id.actionButton:
                request = "Action";
                break;
            case R.id.adventureButton:
                request = "Adventure";
                break;
            case R.id.indieButton:
                request = "Indie";
                break;
            case R.id.strategyButton:
                request = "Strategy";
                break;
            case R.id.simulationButton:
                request = "Simulation";
                break;
            case R.id.rpgButton:
                request = "RPG";
                break;
            case R.id.casualButton:
                request = "Casual";
                break;
            case R.id.racingButton:
                request = "Racing";
                break;
        }
        intent.putExtra("fetchType", request);
        startActivity(intent);
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
            Log.d("TEST", "LOGOUT");
            user = null;
            Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }
        return true;
    }
}