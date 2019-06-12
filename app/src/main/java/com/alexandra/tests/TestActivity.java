package com.alexandra.tests;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import java.util.Timer;
import java.util.TimerTask;


public class TestActivity extends AppCompatActivity {

    int student, testId;
    Test test;
    int questionNumber = 0;
    int selectedOption = 0;
    int rightAnswers = 0;
    int testingTime = 0;

    Timer testTimer;
    TestTimerTask testTimerTask;

    TextView titleView, descriptionView, testingTimeView, questionsCountView, questionView, resultView, progressView, timerView;
    ListView optionsView;
    View testInfoView, testQuestionView, testResultsView, testProgressView;
    Button nextButton, startTestButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        student = getIntent().getIntExtra("student", 0);
        testId = getIntent().getIntExtra("test", 0);

        titleView = findViewById(R.id.title);
        descriptionView = findViewById(R.id.description);
        testingTimeView = findViewById(R.id.testingTime);
        questionsCountView = findViewById(R.id.questionsCount);
        questionView = findViewById(R.id.question);
        optionsView = findViewById(R.id.options);
        resultView = findViewById(R.id.result);
        progressView = findViewById(R.id.progress);
        timerView = findViewById(R.id.timer);

        testInfoView = findViewById(R.id.testInfo);
        testQuestionView = findViewById(R.id.testQuestion);
        testResultsView = findViewById(R.id.testResults);
        testProgressView = findViewById(R.id.testProgress);

        nextButton = findViewById(R.id.nextButton);
        startTestButton = findViewById(R.id.startTestButton);

        testTimer = new Timer();
        testTimerTask = new TestTimerTask();

        loadTest();
    }


    protected void loadTest() {
        if (testId == 0) {
            return;
        }

        LoadTestTask loadTestTask = new LoadTestTask();
        loadTestTask.execute(student, testId);

        try {
            String response = loadTestTask.get();
            Log.d("12345", "response: " + response);
            JSONObject testJson = new JSONObject(response);

            int testId = testJson.getInt("id");
            String title = testJson.getString("title");
            String description = testJson.getString("description");
            int testingTime = testJson.getInt("testing_time");
            String testingTimeStr = testJson.getString("testing_time_str");
            int questionsCount = testJson.getInt("questions_count");
            String questionsCountStr = testJson.getString("questions_count_str");

            JSONArray questionsJson = testJson.getJSONArray("questions");
            ArrayList<Question> questions = new ArrayList<>();

            for (int i = 0; i < questionsJson.length(); i++) {
                JSONObject questionJson = (JSONObject) questionsJson.get(i);
                int questionId = questionJson.getInt("id");
                String questionWording = questionJson.getString("wording");
                int answer = questionJson.getInt("answer");

                JSONArray optionsJson = questionJson.getJSONArray("options");
                ArrayList<QuestionOption> options = new ArrayList<>();

                for (int j = 0; j < optionsJson.length(); j++) {
                    JSONObject optionJson = (JSONObject) optionsJson.get(j);
                    int optionId = optionJson.getInt("id");
                    String optionWording = optionJson.getString("wording");
                    boolean isRight = (optionJson.getInt("is_right") > 0);

                    QuestionOption option = new QuestionOption(optionId, optionWording, isRight);
                    options.add(option);
                }
                Question question = new Question(questionId, questionWording, options, answer);
                questions.add(question);
            }

            test = new Test(testId, title, description, testingTime, testingTimeStr,
                    questionsCount, questionsCountStr, questions, null);

            if (testJson.has("result")) {
                JSONObject resultJson = testJson.getJSONObject("result");
                int rightAnswers = resultJson.getInt("right_answers");
                int resultTestingTime = resultJson.getInt("testing_time");

                test.result = new TestResult(rightAnswers, resultTestingTime);
            }

            updateTestInfoView();
            updateTestResultsView();
        }
        catch (Exception e) {
            Log.d("12345", e.toString());
        }
    }


    protected void updateTestInfoView() {
        if (test == null) {
            return;
        }

        titleView.setText(test.title);
        descriptionView.setText(test.description);
        testingTimeView.setText(test.testingTimeStr);
        questionsCountView.setText(test.questionsCountStr);
    }


    public void onStartTestButtonClick(View view) {
        testInfoView.setVisibility(View.GONE);
        testResultsView.setVisibility(View.GONE);
        testQuestionView.setVisibility(View.VISIBLE);

        if (test.result == null) {
            rightAnswers = 0;
            testingTime = 0;
            testProgressView.setVisibility(View.VISIBLE);
            testTimer.schedule(testTimerTask, 0, 1000);
        }

        questionNumber = 0;
        progressView.setText(questionNumber + "/" + test.questionsCount);

        updateTestQuestionView();
    }


    protected void updateTestQuestionView() {
        Question question = test.questions.get(questionNumber);

        questionView.setText(question.wording);

        OptionsAdapter adapter = new OptionsAdapter(this, question.options, test.result != null, question.answer);
        optionsView.setAdapter(adapter);

        if (test.result != null) {
            nextButton.setEnabled(true);
        }
        else {
            nextButton.setEnabled(false);
        }
    }


    public void selectQuestionOption(int option) {
        selectedOption = option;
        if (selectedOption > 0) {
            nextButton.setEnabled(true);
        }
    }


    public void onNextButtonClick(View view) {
        if (test.result == null) {
            Question question = test.questions.get(questionNumber);

            SendAnswerTask sendAnswerTask = new SendAnswerTask();
            sendAnswerTask.execute(student, test.id, question.id, selectedOption);

            question.answer = selectedOption;
            if (checkAnswer(question, selectedOption)) {
                rightAnswers++;
            }
        }

        questionNumber++;
        progressView.setText(questionNumber + "/" + test.questionsCount);
        if (questionNumber >= test.questionsCount) {
            finishTest();
            return;
        }
        else {
            selectedOption = 0;
            updateTestQuestionView();
        }
    }


    protected boolean checkAnswer(Question question, int selectedOption) {
        for (int i = 0; i < question.options.size(); i++) {
            QuestionOption option = question.options.get(i);
            if ((option.isRight) && (option.id == selectedOption)) {
                return true;
            }
        }
        return false;
    }


    protected void finishTest() {
        if (test.result == null) {
            SendTestResultTask sendTestResultTask = new SendTestResultTask();
            sendTestResultTask.execute(student, test.id, rightAnswers, 0);

            test.result = new TestResult(rightAnswers, 0);

            testTimer.cancel();
            testProgressView.setVisibility(View.GONE);
        }

        testQuestionView.setVisibility(View.GONE);
        testInfoView.setVisibility(View.VISIBLE);

        updateTestResultsView();
    }


    protected void updateTestResultsView() {
        if (test.result == null) {
            return;
        }

        String result = test.result.rightAnswers + "/" + test.questionsCount;
        resultView.setText(result);

        testResultsView.setVisibility(View.VISIBLE);
        startTestButton.setVisibility(View.GONE);
    }


    public void onTestsListButtonClick(View view) {
        super.onBackPressed();
    }



    class LoadTestTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... data) {
            String serverUrl = "http://192.168.1.2:80/api/test";
            String postParams = "student=" + data[0] + "&test=" + data[1];

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


    class SendAnswerTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... data) {
            String serverUrl = "http://192.168.1.2:80/api/test-answer";
            String postParams = "student=" + data[0] + "&test=" + data[1] + "&question=" + data[2] + "&option=" + data[3];

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


    class SendTestResultTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... data) {
            String serverUrl = "http://192.168.1.2:80/api/test-finish";
            String postParams = "student=" + data[0] + "&test=" + data[1] + "&right_answers=" + data[2] + "&testing_time=" + data[3];

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


    class TestTimerTask extends TimerTask {

        @Override
        public void run() {
            testingTime++;
            int timer = test.testingTime * 60 - testingTime;
            String timerStr = timer / 60 + ":" + String.format("%02d", timer % 60);

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    timerView.setText(timerStr);
                    if (timer <= 0) {
                        finishTest();
                    }
                }
            });
        }
    }
}
