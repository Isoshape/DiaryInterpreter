package com.example.bigmac.diaryinterpreter;

import java.util.Date;

/**
 * Created by BigMac on 08/12/16.
 */
public class Answers {

    private int questionID;
    private int diaryID;
    private int UUID;
    private String answer;
    private int questionGrp;
    private String date;
    private String time;
    private int session;


    public Answers(){

    }

    public Answers(int questionID,int diaryID, int UUID,String answer,int questionGrp,String date,String time,int session){

        this.questionID = questionID;
        this.diaryID = diaryID;
        this.UUID = UUID;
        this.answer = answer;
        this.questionGrp = questionGrp;
        this.date = date;
        this.time = time;
        this.session = session;

    }


    public void setQuestionID(int questionID){

        this.questionID = questionID;
    }

    public void setDiaryID(int diaryID){

        this.diaryID = diaryID;
    }

    public void setUUID(int UUID){

        this.UUID = UUID;
    }

    public void setAnswer(String answer){

        this.answer=answer;
    }

    public void setDate(String date){

        this.date=date;
    }

    public void setQuestionGrp(int questionGrp){

        this.questionGrp=questionGrp;
    }


    public void setTime(String time){

        this.time=time;
    }


    public int getQuestionID(){

        return questionID;
    }

    public int getDiaryID(){

        return diaryID;
    }

    public int getUUID(){

        return UUID;
    }

    public String getAnswer(){

        return answer;
    }

    public int getQuestionGrp(){

        return questionGrp;
    }

    public String getDate(){

        return date;
    }

    public String getTime(){

        return time;
    }

    public int getSession(){

        return session;
    }

}
