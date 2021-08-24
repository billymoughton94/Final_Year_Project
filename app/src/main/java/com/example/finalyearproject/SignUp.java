package com.example.finalyearproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    TextView loginLink;
    Button signupBtn;
    EditText ET_Signup_Username;
    EditText ET_Signup_Password;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");
        loginLink = findViewById(R.id.logInLink);
        loginLink.setOnClickListener(this);
        signupBtn = findViewById(R.id.BTN_Signup);
        signupBtn.setOnClickListener(this);
        ET_Signup_Username = findViewById(R.id.ET_Signup_Username);
        ET_Signup_Password = findViewById(R.id.ET_Signup_Password);
        progressBar = findViewById(R.id.signup_progressBar);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.BTN_Signup:
                progressBar.setVisibility(View.VISIBLE);
                createUser(ET_Signup_Username.getText().toString(), ET_Signup_Password.getText().toString());
                break;
            case R.id.logInLink:
                startActivity(new Intent(this, Login.class));
                finish();
                break;
        }
    }

    // this method creates a new user entry in the database if it passes validation test
    public void createUser(final String username, final String password){
        if(username.equals("") || password.equals("")){
            Toast.makeText(this, "Username and password cannot be empty", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if(password.length() < 6){
            Toast.makeText(this, "Password cannot be less than 6 characters long", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        final String signupURL = "http://192.168.0.40/PHP_Scripts/signup.php"; // TODO: CHANGE TO YOUR LOCAL IP ADDRESS
        StringRequest signupRequest = new StringRequest(Request.Method.POST, signupURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");

                if(result.equals("success")){
                    Toast.makeText(getApplicationContext(), "User successfully added", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);

                    Intent intent = new Intent(SignUp.this, Login.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Error creating new user. Please try again", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error creating new user. Please try again", Toast.LENGTH_LONG).show();
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
        Webhost_Connector.getInstance(this.getApplicationContext()).addRequest(signupRequest);
    }



}