package com.example.bigmac.diaryinterpreter;

import java.io.Serializable;

public class JsonHolder implements Serializable {

    private int id;

    private String question;

    private String[] splitanswers;

    private String extraID;

    private String extraQuestion;

    private String[] extraAnswers;


    public JsonHolder(String question, String answers, String extraID, String extraQuestions, String extraAnswers) {


        this.question = question;
        splitanswers = answers.split(",");

        this.extraID = extraID;
        this.extraQuestion = extraQuestions;
        this.extraAnswers = extraAnswers.split(",");

    }

    public int getId() {

        return id;

    }

    public void setId(int id) {

        this.id = id;

    }

    public String getQuestion() {

        return question;

    }

    public String[] getAnswers() {


        return splitanswers;

    }

    public String getExtraQuestion() {

        return extraQuestion;

    }

    public String[] getExtraAnswers() {


        return extraAnswers;

    }

    public String getExtraID(){

        return extraID;
    }



}
