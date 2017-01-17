package com.example.bigmac.diaryinterpreter;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
    LinearLayout progrss;

    private String activator = "next";

    private TextView questionfield;
    private Button nextButton;
    private Button prevButton;



    //dB fields;
    DBHandler db;



    ProgressBar simpleProgressBar;

    //data info
    String currentDate;
    String time;
    String duration = "no duration";
    //which session are we in
    int session;


    //Main iteration variable.
    private int i = 0;
    //number shower


    //Sharedpreferences
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    //toolbar
    private Toolbar toolbar;

    private ArrayList<JsonHolder> result = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_success);

            //stop thread from running
            MainUserActivity.uploadThread.interrupt();

            toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setTitle("");
            toolbar.setSubtitle("");

            ImageView testimmage = (ImageView) toolbar.findViewById(R.id.logohospital);
            String variableValue = PersonInfo.getLogourl();
            testimmage.setImageResource(getResources().getIdentifier(variableValue, "drawable", getPackageName()));

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);



            simpleProgressBar=(ProgressBar)findViewById(R.id.progressBar);
            simpleProgressBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);

            //pref with private mode = 0 (the created file can only be accessed by the calling application)
            pref = getApplicationContext().getSharedPreferences("MyPref", 0);
            editor = pref.edit();

            //Create database object and get session
            db = new DBHandler(this);


            //Find last session and add 1
            session = db.getSession()+1;
            Log.d("Hva er session",""+session);

            //checks if event is of type 1. if so put duration to zero, and save this session combined with eventID in SP
            if (PersonInfo.getTrigger() == 1){
                duration = "0";
                editor.putInt("session"+PersonInfo.getQuestionGrp(),+session);
                editor.commit();
            }

            //create date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            currentDate = sdf.format(new Date());
            sdf = new SimpleDateFormat("HH:mm:ss");
            time = sdf.format(new Date().getTime());


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

            simpleProgressBar.setMax(result.size());

            typeHandler();

    }


    //Menu creation
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.d("NU SKAL JEG HJÆLPE DIG!","HER");
        }

        if (item.getItemId() == android.R.id.home) {

            exitByBackKey();

        }


        return super.onOptionsItemSelected(item);
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
                     Toast.makeText(InterpreterActivity.this, "Du er ved første spørgsmål ", Toast.LENGTH_SHORT).show();
               }

            }



    }


    public void typeHandler(){

        //NOTES TO MY SELF:
        //TYPEHANDLER() is responsible for activating the correct layout
        //MAY NOT BE CALLED IF I IS GREATER THAN INDEX
        if (i < result.size()) {

            answersHold.removeAllViews();

            if (activator.equalsIgnoreCase("next")) {
                //check if answer is dependent on previous answer
                if (result.get(i).getVisible() == 1) {
                    //check which question it operates on
                    int master = result.get(i).getOperation();
                    Log.d("master er ", "" + master);
                    //compare this question condition with the master if true show this question
                    if (db.getAnswer(master,session) == result.get(i).getQcondition()) {

                        layoutActivator();
                    }

                    //if condition is not true skip this question and iterate i.
                    else {
                        db.addAnswer(new Answers(result.get(i).getQuestionID(),PersonInfo.getDiaryID(),PersonInfo.getUserID(),"-1",PersonInfo.getQuestionGrp(), currentDate,time,duration,session));
                        db.close();
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

                        layoutActivator();
                    }

                    //if condition is not true skip this question and call methode again
                    else {
                        //DELETE DEFAULT GENERATED ANSWER (GENERATED IF THIS IS NOT ANSWERED)
                        db.deleteLast();
                        typeHandler();
                    }
                    //show the correct layout
                } else {
                    layoutActivator();
                }
            }

            simpleProgressBar.setProgress(i);

        }
        //this makes sure that the last data is saved, and no more layouts are being called - only next activity.
        else if (i == result.size()){
            String svar = "Tak for dine svar. De vil automatisk blive uploadet";
            boolean state = true;

            if(PersonInfo.getQuestionGrp() == 0) {
                editor.putString("date", currentDate);
                editor.putBoolean("state", state);
                editor.commit();
            }

            if (PersonInfo.getTrigger()==1){
                svar = "Husk at deaktivere hændelsen når den er færdig";
            }

            Toast.makeText(InterpreterActivity.this, svar, Toast.LENGTH_SHORT).show();
            Log.d("Arrayslut", "" + i);
            Intent intent = new Intent(InterpreterActivity.this,MainUserActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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


            db.addAnswer(new Answers(result.get(i).getQuestionID(),PersonInfo.getDiaryID(),PersonInfo.getUserID(),rG1_CheckId,PersonInfo.getQuestionGrp(), currentDate,time,duration,session));
            db.close();
            i++;
            typeHandler();

        }
        else {
            typeHandler();
            Toast.makeText(InterpreterActivity.this, "Vælg venligst et svar ", Toast.LENGTH_SHORT).show();

        }
    }
    public void setLayoutMultiple(){

            rg = new RadioGroup(getApplicationContext()); //create the RadioGroup
            rg.setOrientation(RadioGroup.VERTICAL);



            rg.removeAllViews();
            if (i<result.size()) {

            String getquestion = result.get(i).getQuestion();

            //Get question from arraylist
            questionfield.setText(getquestion);

            //get possible answers in insert them in an string array
            String[] questionssplit = result.get(i).getAnswers();
            int a = 0;
            for (; a < questionssplit.length; a++) {
                //create radiobuttons equal to size of possible answers, string array
                RadioButton newRadioButton = new RadioButton(InterpreterActivity.this);
               // newRadioButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                newRadioButton.setLayoutParams(params);



                newRadioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 7F, this.getResources().getDisplayMetrics()));
               // newRadioButton.setPadding(0, 110, 0, 110);
                newRadioButton.setGravity(Gravity.CENTER);
                newRadioButton.setText("" + questionssplit[a]);
                newRadioButton.setId(a);


                rg.addView(newRadioButton, a);



            }//end for
            answersHold.addView(rg);
            if(i==result.size()-1) {

                nextButton.setText("Afslut");
            }
            else {
                nextButton.setText("Næste");
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
            Toast.makeText(InterpreterActivity.this, "Indtast venligst et svar ", Toast.LENGTH_SHORT).show();

        }
        else {
            String answer = etAnswer.getText().toString();

            Log.d("inputuser",""+answer);
            db.addAnswer(new Answers(result.get(i).getQuestionID(), PersonInfo.getDiaryID(), PersonInfo.getUserID(), answer, PersonInfo.getQuestionGrp(), currentDate, time, duration, session));
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
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,200);
        etAnswer = new EditText(this);
        etAnswer.setLayoutParams(lparams);
        etAnswer.setHint("Klik for at indtaste dit svar");
        etAnswer.setInputType(InputType.TYPE_CLASS_TEXT);
        etAnswer.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etAnswer.setBackgroundResource(R.drawable.roundedbutton);

        answersHold.addView(etAnswer);
    }
    // !! USER INPUT MODULE FINISH !! //



    @Override
    public void onBackPressed() {
        exitByBackKey();
    }


    protected void exitByBackKey() {

        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Vil du virkelig afslutte? (alt data mistes)")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {

                        if (PersonInfo.getTrigger()==1){
                            editor.putBoolean("state" + PersonInfo.getQuestionGrp(), false);
                            editor.commit();
                        }
                        db.deleteBackBtn(session);
                        Intent goback = new Intent(InterpreterActivity.this,MainUserActivity.class);
                        goback.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(goback);
                        finish();
                        //close();


                    }
                })
                .setNegativeButton("Nej", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();

    }

}
