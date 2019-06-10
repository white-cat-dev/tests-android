package com.alexandra.tests;


public class QuestionOption {
    int id;
    String wording;
    boolean isRight;

    public QuestionOption(int id, String wording, boolean isRight) {
        this.id = id;
        this.wording = wording;
        this.isRight = isRight;
    }
}
