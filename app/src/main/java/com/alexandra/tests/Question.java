package com.alexandra.tests;

import java.util.ArrayList;


public class Question {
    int id;
    String wording;
    ArrayList<QuestionOption> options;
    int answer;


    public Question(int id, String wording, ArrayList<QuestionOption> options, int answer) {
        this.id = id;
        this.wording = wording;
        this.options = options;
        this.answer = answer;
    }
}
