package com.example.bigmac.diaryinterpreter;

import java.io.Serializable;

public class JsonHolder implements Serializable {

    private int questionID;
    private int visible;
    private int operation;
    private int qcondition;

    private int questionGrp;

    private int type;

    private String question;

    private String[] splitanswers;


    public JsonHolder(int questionID,int visible,int operation,int qcondition,int questionGrp,int type,String question, String answers) {

        this.questionID=questionID;
        this.visible=visible;
        this.operation=operation;
        this.qcondition = qcondition;
        this.questionID=questionID;
        this.questionGrp = questionGrp;
        this.question = question;
        splitanswers = answers.split(",");
        this.type = type;

    }

    public int getQuestionGrp(){

        return questionGrp;
    }


    public int getType(){

        return type;
    }


    public String getQuestion() {

        return question;

    }

    public String[] getAnswers() {


        return splitanswers;

    }

    public int getQuestionID(){

        return questionID;
    }

    public int getVisible(){

        return visible;
    }

    public int getOperation(){

        return operation;
    }

    public int getQcondition(){

        return qcondition;
    }


}
