package com.alexandra.tests;

import java.util.ArrayList;


public class Test {
    int id;
    String title, description;
    int testingTime, questionsCount;
    String testingTimeStr, questionsCountStr;
    ArrayList<Question> questions;
    TestResult result;

    public Test(int id, String title, String description, int testingTime, String testingTimeStr,
                int questionsCount, String questionsCountStr, ArrayList<Question> questions, TestResult result) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.testingTime = testingTime;
        this.testingTimeStr = testingTimeStr;
        this.questionsCount = questionsCount;
        this.questionsCountStr = questionsCountStr;
        this.questions = questions;
        this.result = result;
    }
}
