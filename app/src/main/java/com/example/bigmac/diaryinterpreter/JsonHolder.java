package com.example.bigmac.diaryinterpreter;

import java.io.Serializable;

public class JsonHolder implements Serializable {

    private int questionGrp;

    private int id;

    private int type;

    private String question;

    private String[] splitanswers;

    private String extraID;

    private String extraQuestion;

    private String[] extraAnswers;


    public JsonHolder(int questionGrp,String question, String answers, String extraID, String extraQuestions, String extraAnswers, int type) {

        this.questionGrp = questionGrp;
        this.question = question;
        splitanswers = answers.split(",");
        this.extraID = extraID;
        this.extraQuestion = extraQuestions;
        this.extraAnswers = extraAnswers.split(",");
        this.type = type;

    }

    public int getQuestionGrp(){

        return questionGrp;
    }

    public int getId() {

        return id;

    }

    public int getType(){

        return type;
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
