package com.alexandra.tests;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity {

    EditText emailView, passwordView;
    TextView errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailView = findViewById(R.id.email);
        passwordView = findViewById(R.id.password);
        errorView = findViewById(R.id.error);
    }


    public void onLoginButtonClick(View view) {
        String login = emailView.getText().toString();
        String password = passwordView.getText().toString();

        LoginTask loginTask = new LoginTask();
        loginTask.execute(login, password);

        try {
            String response = loginTask.get();
            Log.d("1234", "response: " + response);
            JSONObject responseJson = new JSONObject(response);
            if (responseJson.has("error")) {
                errorView.setText(responseJson.getString("error"));
            }
            else {
                Intent intent = new Intent(this, TestsActivity.class);
                intent.putExtra("user_id", responseJson.getString("id"));
                startActivity(intent);
            }
        }
        catch (Exception e) {
            errorView.setText("Произошла ошибка");
        }
    }


    class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... data) {
            String serverUrl = "http://192.168.1.2:80/api/login";
            String postParams = "email=" + data[0] + "&password=" + data[1];

            try {
                URL url = new URL(serverUrl);
                HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
                httpConnection.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(httpConnection.getOutputStream());
                out.writeBytes(postParams);
                out.flush();
                out.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();
            }
            catch (Exception e) {
                return "";
            }
        }
    }
}
