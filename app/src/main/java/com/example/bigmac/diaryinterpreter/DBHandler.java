package com.example.bigmac.diaryinterpreter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BigMac on 08/12/16.
 */
public class DBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 4;
    // Database Name
    private static final String DATABASE_NAME = "answersDB";
    // Contacts table name
    private static final String TABLE_ANSWERS = "answerTable";
    private static final String TABLE_TIME = "timeTable";
    // Shops Table Columns names
    private static final String KEY_ANSWERID = "answerID";
    private static final String KEY_QUESTIONID = "questionID";
    private static final String KEY_DIARYID = "diaryID";
    private static final String KEY_UUID = "uuid";
    private static final String KEY_ANSWER = "answer";
    private static final String KEY_GRP = "questionGrp";
    private static final String KEY_DATE= "datenow";
    private static final String KEY_TIME= "timeevent";
    private static final String KEY_SESSION= "session";



    public DBHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        getWritableDatabase();
        getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_ANSWER_TABLE = "CREATE TABLE " + TABLE_ANSWERS + "("
        + KEY_ANSWERID + " INTEGER PRIMARY KEY," + KEY_QUESTIONID + " INTEGER,"
        + KEY_DIARYID + " INTEGER," + KEY_UUID + " INTEGER," + KEY_ANSWER + " TEXT,"
                + KEY_GRP + " INTEGER,"+ KEY_DATE + " TEXT," + KEY_TIME + " TEXT," + KEY_SESSION + " INTEGER" + ")";

        db.execSQL(CREATE_ANSWER_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        Log.d("UPGRADE","sådan");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANSWERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIME);
        onCreate(db);

    }

    // Adding new answer
    public void addAnswer(Answers answer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_QUESTIONID, answer.getQuestionID()); // questionID
        values.put(KEY_DIARYID, answer.getDiaryID()); // diary ID
        values.put(KEY_UUID, answer.getUUID()); // user ID
        values.put(KEY_ANSWER, answer.getAnswer()); // diary ID
        values.put(KEY_GRP, answer.getQuestionGrp()); // diary ID
        values.put(KEY_SESSION, answer.getSession()); // session only for local db
        values.put(KEY_DATE, answer.getDate()); // date
        values.put(KEY_TIME, answer.getTime()); // time event
        // Inserting Row
        db.insert(TABLE_ANSWERS, null, values);
        db.close(); // Closing database connection


    }

//    public void addDateandTime(int session,String date,String time){
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(KEY_SESSION, session);
//        values.put(KEY_DATE, date);
//        values.put(KEY_TIME, time);
//        db.insert(TABLE_TIME, null, values);
//        db.close(); // Closing database connection
//    }



    public int getAnswer(int id,int session){
        int test = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {KEY_ANSWER};
        Cursor cursor = db.query(TABLE_ANSWERS, columns, KEY_QUESTIONID + " = '" + id + "' AND " + KEY_SESSION + " = '" + session +"'", null, null, null, null);
        if (cursor.moveToLast()) {
          if  (cursor != null)
              test = Integer.parseInt(cursor.getString(0));
        }
        return test;

    }

    public int getSession(){
        int test;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT session FROM " + TABLE_ANSWERS;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToLast()){
            test = cursor.getInt(0);
             //test = Integer.parseInt(cursor.getString(0));
        }
        else{
            test = 0;
        }

        return test;
    }

    //used for when using previous button
    public void deleteLast(){
        int test;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ANSWERS, null, "answerID=(SELECT MAX(answerID) FROM " + TABLE_ANSWERS + ")", null, null, null, null);
        if (cursor.moveToFirst()) {
            test = cursor.getInt(0);
            SQLiteDatabase dba = this.getWritableDatabase();
            dba.delete(TABLE_ANSWERS, "answerID=" + test, null);
        }

    }


    public List<Answers> getAllAnswers() {
        List<Answers> answerList = new ArrayList<Answers>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_ANSWERS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
               Answers answer = new Answers();

                answer.setQuestionID(cursor.getInt(1));
                answer.setDiaryID(cursor.getInt(2));
                answer.setUUID(cursor.getInt(3));
                answer.setAnswer(cursor.getString(4));
                answer.setQuestionGrp(cursor.getInt(5));
                answer.setDate(cursor.getString(6));
                answer.setTime(cursor.getString(7));


                // Adding answers to list
                answerList.add(answer);
            } while (cursor.moveToNext());
        }
        // return answer list
        return answerList;
    }



}
