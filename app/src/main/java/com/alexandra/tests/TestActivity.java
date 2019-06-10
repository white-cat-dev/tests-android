package com.alexandra.tests;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class TestActivity extends AppCompatActivity {

    int student, test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        student = getIntent().getIntExtra("student", 0);
        test = getIntent().getIntExtra("test", 0);

        loadTest();
    }


    protected void loadTest() {
        if (test == 0) {
            return;
        }
    }


    class LoadTestTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... data) {
            String serverUrl = "http://192.168.1.2:80/api/test/" + test;

            try {
                URL url = new URL(serverUrl);
                HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
                httpConnection.setDoOutput(false);

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
