package com.example.bigmac.diaryinterpreter;



import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class InterpreterActivity extends AppCompatActivity implements View.OnClickListener {

    //different dynamic views
    LinearLayout answersHold;
    RadioGroup rg;
    EditText etAnswer;

    private String activator = "next";

    private TextView questionfield;
    private Button nextButton;
    private Button prevButton;


    //dB fields;
    DBHandler db;


    //data info
    String currentDateandTime;
    String time;

    //which session are we in
    int session;


    //Main iteration variable.
    private int i = 0;



    private ArrayList<JsonHolder> result = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        //Create database object and get session
        db = new DBHandler(this);



        session = db.getSession()+1;
        Log.d("Hva er session",""+session);

        //create date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDateandTime = sdf.format(new Date());
        time = "00:00:00";


        //Layout
        answersHold = (LinearLayout) findViewById(R.id.answerHolder);
        questionfield = (TextView) findViewById(R.id.main_title_textView);
        nextButton = (Button) findViewById(R.id.nextquiz);
        nextButton.setOnClickListener(this);
        prevButton = (Button) findViewById(R.id.prevquiz);
        prevButton.setOnClickListener(this);



            //HERE WE SHOULD GET ONLY QUESTION WITH THE QUESTIONGRP ID
            for (int c = 0;c<PersonInfo.getQuestionsArray().size();c++){

            if (PersonInfo.getQuestionsArray().get(c).getQuestionGrp()==PersonInfo.getQuestionGrp()){
                Log.d("C's indhold", "" + PersonInfo.getQuestionsArray().get(c));
                result.add(PersonInfo.getQuestionsArray().get(c));
            }
        }

        Log.d("mit array", "" + result);
        typeHandler();

    }
    @Override
    public void onClick(View v) {

        if (v == nextButton) {

            //make sure to reset view
            answersHold.removeAllViews();
            activator="next";

            //implement the different modules here
            // case 1 = multiple choice
            // case 2 = user input
            // case 3 = upcomming next patch!
            switch (result.get(i).getType()) {
                case 1:
                    handleMultipleChoice();
                    break;
                case 2:
                    handleUserInput();
                    break;


            }
        }

            if (v == prevButton) {


               if (i>0) {
                   activator="prev";
                   typeHandler();
                   db.deleteLast();

               }
                else {
                     Toast.makeText(InterpreterActivity.this, "Du er ved første spørgsmål ", Toast.LENGTH_LONG).show();
               }

            }

    }


    public void typeHandler(){

        //TYPEHANDLER() is responsible for activating the correct layout
        //MÅ IKKE KALDES HVIS I ER STØRRE END INDEX,
        if (i < result.size()) {
            Log.d("1. i= ", "" + i);
            answersHold.removeAllViews();

            if (activator.equalsIgnoreCase("next")) {
                //check if answer is dependent on previous answer
                if (result.get(i).getVisible() == 1) {
                    //check which question it operates on
                    int master = result.get(i).getOperation();
                    Log.d("master er ", "" + master);
                    //compare this question condition with the master if true show this question
                    if (db.getAnswer(master,session) == result.get(i).getQcondition()) {
                        Log.d("forrige svar var ", "" + db.getAnswer(master,session));
                        Log.d("Spørgsmålet skal vises", "hurra");
                        layoutActivator();
                    }

                    //if condition is not true skip this question and iterate i.
                    else {
                        i++;
                        typeHandler();
                    }
                } else {

                    layoutActivator();
                }
            }

            if (activator.equalsIgnoreCase("prev")) {
                //iterate i down and check for invisble question
                i--;
                if (result.get(i).getVisible() == 1) {
                    //check which question it operates on
                    int master = result.get(i).getOperation();
                    Log.d("master er ", "" + master);
                    //compare this question condition with the master if true show this question
                    if (db.getAnswer(master,session) == result.get(i).getQcondition()) {
                        Log.d("forrige svar var ", "" + db.getAnswer(master,session));
                        Log.d("Spørgsmålet skal vises", "hurra");
                        layoutActivator();
                    }

                    //if condition is not true skip this question and call methode again
                    else {
                        typeHandler();
                    }
                    //show the correct layout
                } else {
                    layoutActivator();
                }
            }

        }
        //this makes sure that the last data is saved, and no more layouts are being called - only next activity.
        else if (i == result.size()){
            Log.d("Arrayslut",""+i);
            Intent intent = new Intent(InterpreterActivity.this,MainUserActivity.class);
            intent.putExtra("questionArray", result);
            startActivity(intent);
        }
        }

    public void layoutActivator(){

        //Case 1 - MultipleChoice
        if (result.get(i).getType() == 1) {
            setLayoutMultiple();
        }//end Case 1

        //Case 2 - User input
        if (result.get(i).getType() == 2) {
           setLayoutUserInput();
        }// end Case 2
    }





// !! MULTLTIPLE CHOICE MODULE !! //
    public void handleMultipleChoice(){

        //Check if view has been created or not. If this is the first time, view is null and needs to be created
        if (rg==null) {
            typeHandler();
        }
        String rG1_CheckId = ""+ rg.getCheckedRadioButtonId();
        Log.d("radiobuttonn", "" + rG1_CheckId);
        rg.clearCheck();
        //check if array holding jsonObjects is out of bounds
        //check if an answer is selected, if true proceed
        if (Integer.parseInt(rG1_CheckId) > -1) {


            db.addAnswer(new Answers(result.get(i).getQuestionID(),PersonInfo.getDiaryID(),PersonInfo.getUserID(),rG1_CheckId,PersonInfo.getQuestionGrp(),currentDateandTime,time,session));
            db.close();
            i++;
            typeHandler();

        }
        else {
            typeHandler();
            Toast.makeText(InterpreterActivity.this, "Vælg venligst et svar ", Toast.LENGTH_LONG).show();

        }
    }
    public void setLayoutMultiple(){

            rg = new RadioGroup(getApplicationContext()); //create the RadioGroup
            rg.setOrientation(RadioGroup.VERTICAL);
            rg.removeAllViews();
            if (i<result.size()) {

            String getquestion = result.get(i).getQuestion();

            //Get question from arraylist
            questionfield.setText(""+(i+1)+" / "+result.size()+ " " + getquestion);

            //get possible answers in insert them in an string array
            String[] questionssplit = result.get(i).getAnswers();
            int a = 0;
            for (; a < questionssplit.length; a++) {
                //create radiobuttons equal to size of possible answers, string array
                RadioButton newRadioButton = new RadioButton(InterpreterActivity.this);
               // newRadioButton.setTextColor(Color.parseColor("#03fe6d"));
                newRadioButton.setText("" + questionssplit[a]);
                newRadioButton.setId(a);
                rg.addView(newRadioButton, a);

            }//end for
            answersHold.addView(rg);
            if(i==result.size()-1) {

                nextButton.setText("Afslut");
            }
            else {
                nextButton.setText("Næste spørgsmål");
            }

        }
    }

    // !! MULTLTIPLE CHOICE MODULE FINISH !! //


    // !! USER INPUT MODULE !! //
    public void handleUserInput(){

        Log.d("handleuser",""+i);
        if (etAnswer == null){
            Log.d("etanswer er nullobject", "" + i);
            typeHandler();
        }
        //Check if editText is empty
        if (etAnswer.getText().toString().matches("")){
            typeHandler();
            Toast.makeText(InterpreterActivity.this, "Indtast venligst et svar ", Toast.LENGTH_LONG).show();

        }
        else {
            String answer = etAnswer.getText().toString();

            Log.d("inputuser",""+answer);
            db.addAnswer(new Answers(result.get(i).getQuestionID(), PersonInfo.getDiaryID(), PersonInfo.getUserID(), answer, PersonInfo.getQuestionGrp(), currentDateandTime, time, session));
            db.close();
            i++;
            //clear editText (not sure if needed)
            etAnswer.setText("");
            //after stuff is handled call typehandler in order for next layout
            typeHandler();
        }

    }
    public void setLayoutUserInput(){

        String getquestion = result.get(i).getQuestion();
        questionfield.setText(getquestion);

        etAnswer = new EditText(this);
        etAnswer.setBackgroundResource(R.drawable.roundedbutton);
        etAnswer.setWidth(30);

        answersHold.addView(etAnswer);
    }
    // !! USER INPUT MODULE FINISH !! //



    @Override
    public void onBackPressed() {
        //disable back button
    }


}
