package com.alexandra.tests;

import java.util.ArrayList;


public class Question {
    int id;
    String wording;
    ArrayList<QuestionOption> options;

    public Question(int id, String wording, ArrayList<QuestionOption> options) {
        this.id = id;
        this.wording = wording;
        this.options = options;
    }
}
