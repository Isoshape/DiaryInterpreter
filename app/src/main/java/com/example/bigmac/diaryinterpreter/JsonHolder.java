package com.example.bigmac.diaryinterpreter;

import java.io.Serializable;

public class JsonHolder implements Serializable {

    private int id;

    private String question;

    private String answers;

    private String[] splitanswers;


    public JsonHolder(String question, String answers) {


        this.question = question;

        this.answers = answers;

        splitanswers = answers.split(",");

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

    public void setQuestion(String question) {

        this.question = question;

    }

    public String[] getAnswers() {


        return splitanswers;

    }



}
