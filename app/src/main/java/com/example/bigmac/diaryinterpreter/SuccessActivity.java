package com.example.bigmac.diaryinterpreter;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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


public class SuccessActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private RadioGroup radioGroup;
    private TextView questionfield;
    private Button nextButton;
    private Button prevButton;

    private int i = 0;


    private ArrayList<JsonHolder> result = null;
    private ArrayList<Integer> answers = new ArrayList<>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        questionfield = (TextView) findViewById(R.id.main_title_textView);
        nextButton = (Button) findViewById(R.id.nextquiz);
        nextButton.setOnClickListener(this);

//        prevButton = (Button) findViewById(R.id.previousquiz);
//        prevButton.setOnClickListener(this);


        new AsyncQuestions().execute();

    }

    @Override
    public void onClick(View v) {

        if(v==nextButton){

            int rG1_CheckId = radioGroup.getCheckedRadioButtonId();
            Log.d("radiobuttonn", "" + rG1_CheckId);
            radioGroup.clearCheck();

            //check if array is out of bounds and if radiobutton is selected
            if (i<=result.size()-1) {
                if (rG1_CheckId>-1) {
                    i++;
                    answers.add(rG1_CheckId);
                    setLayout(result, i);
                }else{
                    Toast.makeText(SuccessActivity.this,"Vælg venligst et svar ",Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(SuccessActivity.this,"Ikke flere spørgsmål ",Toast.LENGTH_LONG).show();
            }

            if (answers.size()>0) {

                    Log.d("arrayanswers", "" + answers);

            }



//            SharedPreferences rG1Prefs = getSharedPreferences("rG1Prefs", MODE_PRIVATE);
//            SharedPreferences.Editor prefsEditor = rG1Prefs.edit();
//            prefsEditor.putInt("rG1_CheckId", rG1_CheckId);
//            prefsEditor.commit();
//            Log.d("sharedfirst",""+ rG1Prefs.getInt("rG1_CheckId", 0));

        }

//        if (v==prevButton){
//        if (i>0) {
//            i--;
//            setLayout(result, i);
//        }
//           SharedPreferences rG1Prefs = this.getSharedPreferences("rG1Prefs", MODE_PRIVATE);
//
//            rG1Prefs.getInt("rG1_CheckId", 0);
//            Log.d("shared", "" +  rG1Prefs.getInt("rG1_CheckId", 0));
//        }

    }

    //Class: Upload answers to database via php - not yet implementet
    private class AysncUpload extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(SuccessActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            pdLoading.setMessage("\tUploader spørgsmål");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String done = "Alt er klaret nu";
            return done;
        }
        //result from doInBackground goes here
        protected void onPostExecute(String done) {
            //this method will be running on UI thread
            pdLoading.dismiss();
            Toast.makeText(SuccessActivity.this,""+done,Toast.LENGTH_LONG).show();

        }
    }


    private class AsyncQuestions extends AsyncTask<String, String, ArrayList<JsonHolder>> {
        ProgressDialog pdLoading = new ProgressDialog(SuccessActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            pdLoading.setMessage("\tHenter spørgsmål");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected ArrayList<JsonHolder> doInBackground(String... params) {

            ArrayList<JsonHolder> allquestions = new ArrayList<JsonHolder>();
            try {

                // Enter URL address where your php file resides
                url = new URL("http://hadsundmotion.dk/questions.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                //return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                //return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    Log.d("connection","forbindelse etableret");

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line=null;
                    String question=null;
                    String answers=null;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                        Log.d("testline", line);

                        try {
                            JSONArray arr = new JSONArray(line);
                            Log.d("jsonarray",""+arr);

                            //loop through each object
                            for (int i=0; i<arr.length(); i++){

                                JSONObject jsonProductObject = arr.getJSONObject(i);
                                question = jsonProductObject.getString("question");
                                answers = jsonProductObject.getString("answers");

                                allquestions.add(new JsonHolder(question,answers));
                            }

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    //return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
               // return "exception";
            }  finally {
                conn.disconnect();
            }

            Log.d("arraylist", "" + allquestions);
            return (allquestions);
        }

        @Override
        protected void onPostExecute(ArrayList<JsonHolder> reresult) {

            //this method will be running on UI thread

            pdLoading.dismiss();
            result = reresult;

            if (result != null) {
                Toast.makeText(SuccessActivity.this,"Antal spørgmål blev hentet: "+result.size(),Toast.LENGTH_LONG).show();
                setLayout(result, i);

            } else if (result == null || result.size()==0) {

                Toast.makeText(SuccessActivity.this,"Ingen spørgsmål at vise "+result.size(),Toast.LENGTH_LONG).show();

            }
        }

    }//end asynClass

    public void setLayout(ArrayList<JsonHolder> result, int i){

        if (i<result.size()) {
            Log.d("Hvad er i", "I er nu " + i);
            String getquestion = result.get(i).getQuestion();
            Log.d("inde i array", "" + getquestion);
            questionfield.setText("" + getquestion);

            String[] questionssplit = result.get(i).getAnswers();
            Log.d("StringArray", "" + questionssplit[0]);
            radioGroup.removeAllViews();
            int a = 0;
            for (; a < questionssplit.length; a++) {
                RadioButton newRadioButton = new RadioButton(SuccessActivity.this);
               // newRadioButton.setTextColor(Color.parseColor("#03fe6d"));
                newRadioButton.setText("" + questionssplit[a]);
                newRadioButton.setId(a);

//                int test = 1;
//                if(a == test){
//                    newRadioButton.setChecked(true);
//                }

                LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.WRAP_CONTENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT);
                radioGroup.addView(newRadioButton, a, layoutParams);

                if(i==result.size()-1) {

                    nextButton.setText("Afslut");
                }
                else {
                    nextButton.setText("Næste spørgsmål");
                }
            }//end for

        }
        else{
            Log.d("Arrayslut",""+i);
//           Intent intent = new Intent(SuccessActivity.this,MainActivity.class);
//           startActivity(intent);
            new AysncUpload().execute();
        }

    }//end setLayout

}
