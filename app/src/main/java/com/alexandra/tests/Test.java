package com.alexandra.tests;

import java.util.ArrayList;


public class Test {
    int id;
    String title, description;
    int testingTime;
    ArrayList<Question> questions;

    public Test(int id, String title, String description, int testingTime, ArrayList<Question> questions) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.testingTime = testingTime;
        this.questions = questions;
    }
}
