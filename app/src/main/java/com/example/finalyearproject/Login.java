package com.example.finalyearproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener {

    TextView signUpLink;
    Button loginBtn;
    EditText ET_Login_Username;
    EditText ET_Login_Password;
    ProgressBar progressBar;
    Webhost_Connector webHostCon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");

        ET_Login_Username = findViewById(R.id.ET_Login_Username);
        ET_Login_Password = findViewById(R.id.ET_Login_Password);
        signUpLink = findViewById(R.id.signUpLink);
        signUpLink.setOnClickListener(this);
        loginBtn = findViewById(R.id.BTN_Login);
        loginBtn.setOnClickListener(this);
        progressBar = findViewById(R.id.login_progressBar);

        webHostCon = Webhost_Connector.getInstance(getApplicationContext());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.BTN_Login:
                progressBar.setVisibility(View.VISIBLE);
                checkLogin(ET_Login_Username.getText().toString(), ET_Login_Password.getText().toString());
                break;
            case R.id.signUpLink:
                startActivity(new Intent(this, SignUp.class));
                finish();
                break;
        }
    }

    // method that verifies the credentials and logs user in if credentials are correct
    public void checkLogin(final String username, final String password) {

        if(username.equals("") || password.equals("")){
            Toast.makeText(getApplicationContext(), "Username or Password cannot be empty", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        final String loginURL = "http://192.168.0.40/PHP_Scripts/login.php"; // TODO: CHANGE TO YOUR LOCAL IP ADDRESS
        StringRequest loginRequest = new StringRequest(Request.Method.POST, loginURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");
                    if(result.equals("success")){
                        User user = new User(jsonObject.getInt("userID"), jsonObject.getString("username"));
                        Log.d("SUCCESS", "SUCCESSFULLY CONNECTED TO PHP AND FETCHED DATA");
                        Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(Login.this, HomePage.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Username or password incorrect", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> post_data = new HashMap<String, String>();
                post_data.put("username", username);
                post_data.put("password", password);
                return post_data;
            }
        };
        webHostCon.addRequest(loginRequest);
    }

}