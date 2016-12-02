package com.example.bigmac.diaryinterpreter;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;



public class InterpreterActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    //private RadioGroup radioGroup;

    //different dynamic views
    LinearLayout answersHold;
    RadioGroup rg;
    EditText etAnswer;



    private TextView questionfield;
    private Button nextButton;
    private Button prevButton;
    String fname;
    String id;


    int flagExtra = 1;

    //Main iteration variable.
    private int i = 0;


    private ArrayList<JsonHolder> result = null;
    private ArrayList<String> answers = new ArrayList<>();
    private ArrayList<String> exstraAnswersArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);


        answersHold = (LinearLayout) findViewById(R.id.answerHolder);
        questionfield = (TextView) findViewById(R.id.main_title_textView);
        nextButton = (Button) findViewById(R.id.nextquiz);
        nextButton.setOnClickListener(this);

        prevButton = (Button) findViewById(R.id.prevquiz);
        prevButton.setOnClickListener(this);

            fname = PersonInfo.getFirstName();
            id = PersonInfo.getDiaryID();

        result = PersonInfo.getQuestionsArray();
        typeHandler();

    }

    @Override
    public void onClick(View v) {


        if (v == nextButton) {
            //make sure to reset view
            answersHold.removeAllViews();

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

            Log.d("MA", "" + answers);
            Log.d("EA", "" + exstraAnswersArray);
        }

        if (v == prevButton){
            answersHold.removeAllViews();
            if (i>0) {
                // Default, iterate i down and remove from both arrays
                i--;
                answers.remove(i);
                if (Integer.parseInt(result.get(i).getExtraID()) > -1) {
                    exstraAnswersArray.remove(i);

                }

                switch (result.get(i).getType()) {
                    case 1:
                        handleMultipleChoice();
                        break;
                    case 2:
                        handleUserInput();
                        break;
                }
            }

            //if inside extraQuestion and i = 0 more handling is required
            if (flagExtra==2){
                answers.remove(i);
                flagExtra = 1;
                switch (result.get(i).getType()) {
                    case 1:
                        handleMultipleChoice();
                        break;
                    case 2:
                        handleUserInput();
                        break;
                }
            }

            //if i = 0 nothing should happen when back button is pressed.
            else if (i==0) {
                switch (result.get(i).getType()) {
                    case 1:
                        handleMultipleChoice();
                        break;
                    case 2:
                        handleUserInput();
                        break;
                }
            }

            Log.d("MA", "" + answers);
            Log.d("EA", "" + exstraAnswersArray);

        }

    }

    public void typeHandler(){

        //TYPEHANDLER() is responsible for activating the correct layout

        //MÅ IKKE KALDES HVIS I ER STØRRE END INDEX,
        if (i < result.size()) {
            Log.d("1. i= ", "" + i);
            answersHold.removeAllViews();

            //Case 1 - MultipleChoice
            if (result.get(i).getType() == 1) {
                setLayoutMultiple();
            }//end Case 1

            //Case 2 - User input
            if (result.get(i).getType() == 2) {

                setLayoutUserInput();
            }// end Case 2
        }
        //this makes sure that the last data is saved, and no more layouts are being called - only next activity.
        else if (i == result.size()){
            Log.d("Arrayslut",""+i);
            Intent intent = new Intent(InterpreterActivity.this,UploadAnswers.class);
            intent.putExtra("answersArray", answers);
            intent.putExtra("extraanswersArray", exstraAnswersArray);
            intent.putExtra("questionArray", result);
            startActivity(intent);
        }

    }

// !! MULTLTIPLE CHOICE MODULE !! //
    public void handleMultipleChoice(){

        Log.d("handleMulti",""+i);

        //Check if view has been created or not. If this is the first time, view is null and needs to be created
        if (rg==null) {
            typeHandler();

        }
        String rG1_CheckId = ""+rg.getCheckedRadioButtonId();
        Log.d("radiobuttonn", "" + rG1_CheckId);
        rg.clearCheck();
        //check if array holding jsonObjects is out of bounds
        //check if an answer is selected, if true proceed
        if (Integer.parseInt(rG1_CheckId) > -1) {

            //check if extra answer is missed/not activated in case of this save the value as -1 into xtraanswerArray (meaning this questions is not answered/activated)
            if (Integer.parseInt(result.get(i).getExtraID()) > -1 && Integer.parseInt(rG1_CheckId) != Integer.parseInt(result.get(i).getExtraID()) && flagExtra == 1) {
                exstraAnswersArray.add("-1");
            }

            //Check if extra answers is activated (if its possible) flagextra is set to 2 (in the statement) everytime methode is called, to not enter it again until left for main question
            if (Integer.parseInt(rG1_CheckId) == Integer.parseInt(result.get(i).getExtraID()) && flagExtra == 1) {

                answers.add(rG1_CheckId);
                showExtraQuestion();
                //set flag=2 in order to get selected extra answer in extraanswerarray
                flagExtra = 2;

            }
            //Check to see if extra an
            // swers is active, if so save selected value in extraanswersArrat, and go back to flagvalue=1 - default
            else if (flagExtra == 2) {

                exstraAnswersArray.add(rG1_CheckId);
                flagExtra = 1;

                i++;
                typeHandler();

            }
            //Default, no extra answers possible.
            else {
                answers.add(rG1_CheckId);
                flagExtra = 1;
                    i++;
                typeHandler();

            }
        }   if (Integer.parseInt(rG1_CheckId) == -1) {
            Toast.makeText(InterpreterActivity.this, "Vælg venligst et svar ", Toast.LENGTH_SHORT).show();
            typeHandler();
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


    public void showExtraQuestion(){
        rg.removeAllViews();
        String getextraquestion = result.get(i).getExtraQuestion();
        questionfield.setText(""+getextraquestion);
       // get possible answers in insert them in an string array
        String[] extraquestionssplit = result.get(i).getExtraAnswers();
        int a = 0;
        for (; a < extraquestionssplit.length; a++) {
            //create radiobuttons equal to size of possible answers, string array
            RadioButton newRadioButton = new RadioButton(InterpreterActivity.this);
            // newRadioButton.setTextColor(Color.parseColor("#03fe6d"));
            newRadioButton.setText("" + extraquestionssplit[a]);
            newRadioButton.setId(a);

            rg.addView(newRadioButton, a);

        }
        answersHold.addView(rg);

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
            answers.add(answer);

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
