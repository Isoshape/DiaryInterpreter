package com.example.bigmac.diaryinterpreter;

import java.util.ArrayList;

/**
 * Created by BigMac on 28/11/16.
 */
 public class PersonInfo {

    static String firstname = null;
    static String lastname = null;
    static int diaryID;
    static int userID;
    static int questionGrp;
    static int trigger;
    static String logourl;
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

    public static void setUserID(int userID){

        PersonInfo.userID = userID;
    }

    public static void setDiaryID(int diaryID){

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

    public static void setLogourl(String logourl){

        PersonInfo.logourl = logourl;
    }


    public static String getFirstName(){

        return firstname;

    }

    public static String getLastname(){

        return lastname;

    }

    public static int getDiaryID(){

        return diaryID;

    }

    public static int getUserID(){
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

    public static String getLogourl(){

        return logourl;
    }



}
