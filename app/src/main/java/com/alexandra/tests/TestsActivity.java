package com.alexandra.tests;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class TestsActivity extends AppCompatActivity {

    int student;

    ListView testsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests);

        student = getIntent().getIntExtra("student", 0);

        testsListView = findViewById(R.id.testsList);
        loadTests();
    }


    protected void loadTests() {
        LoadTestsTask loadTestsTask = new LoadTestsTask();
        loadTestsTask.execute(student);

        ArrayList<Test> tests = new ArrayList<>();

        try {
            String response = loadTestsTask.get();
            Log.d("1234", "response: " + response);
            JSONArray testsJson = new JSONArray(response);

            for (int i = 0; i < testsJson.length(); i++) {
                JSONObject testJson = (JSONObject) testsJson.get(i);
                int id = testJson.getInt("id");
                String title = testJson.getString("title");
                String description = testJson.getString("description");
                int testingTime = testJson.getInt("testing_time");

                Test test = new Test(id, title, description, testingTime, new ArrayList<>());
                tests.add(test);
            }

            TestsAdapter adapter = new TestsAdapter(this, tests);
            testsListView.setAdapter(adapter);
        }
        catch (Exception e) {
            Log.d("12345", e.toString());
        }
    }


    public void loadTest(int test) {
        Intent intent = new Intent(this, TestsActivity.class);
        intent.putExtra("student", student);
        intent.putExtra("test", test);
        startActivity(intent);
    }


    public void onLoadTestsListButtonClick(View view) {
        loadTests();
    }


    class LoadTestsTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... data) {
            String serverUrl = "http://192.168.1.2:80/api/tests";
            String postParams = "student=" + data[0];

            try {
                URL url = new URL(serverUrl);
                HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
                httpConnection.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(httpConnection.getOutputStream());
                out.writeBytes(postParams);

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
