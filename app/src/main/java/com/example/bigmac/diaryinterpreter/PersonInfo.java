package com.example.bigmac.diaryinterpreter;

import java.util.ArrayList;

/**
 * Created by BigMac on 28/11/16.
 */
 public class PersonInfo {

    static String firstname = null;
    static String lastname = null;
    static String diaryID = null;
    static String userID = null;
    static int questionGrp;
    static int trigger;
    static ArrayList<JsonHolder> allquestions = null;
    static ArrayList<EventHolder> allevents = null;

    public static void setFirstName(String firstname){

        PersonInfo.firstname = firstname;

    }

    public static void setTrigger(int trigger){

        PersonInfo.trigger = trigger;
    }

    public static void setLastName(String lastname){

        PersonInfo.lastname = lastname;

    }

    public static void setUserID(String userID){

        PersonInfo.userID = userID;
    }

    public static void setDiaryID(String diaryID){

        PersonInfo.diaryID = diaryID;

    }

    public static void setAllquestions(ArrayList<JsonHolder> allquestions){

        PersonInfo.allquestions = allquestions;
    }

    public static void setAllevents(ArrayList<EventHolder> allevents){

        PersonInfo.allevents = allevents;
    }

    public static void setQuestionGrp(int questionGrp){

        PersonInfo.questionGrp = questionGrp;

    }


    public static String getFirstName(){

        return firstname;

    }

    public static String getLastname(){

        return lastname;

    }

    public static String getDiaryID(){

        return diaryID;

    }

    public static String getUserID(){
        return userID;
    }

    public static ArrayList<JsonHolder> getQuestionsArray(){

        return allquestions;
    }

    public static ArrayList<EventHolder> getEventsArray(){

        return allevents;
    }

    public static int getQuestionGrp(){

        return questionGrp;
    }

    public static int getTrigger(){

        return trigger;
    }



}
