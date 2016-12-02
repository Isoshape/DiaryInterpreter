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
    static ArrayList<JsonHolder> allquestions = null;

    public static void setFirstName(String firstname){

        PersonInfo.firstname = firstname;

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


}
