package com.example.bigmac.diaryinterpreter;

import android.app.Activity;
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


public class SuccessActivity extends AppCompatActivity implements View.OnClickListener {


    private RadioGroup radioGroup;
    private TextView questionfield;
    private Button nextButton;
    private Button prevButton;

    private int i = 0;


    ArrayList<JsonHolder> result = null;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        questionfield = (TextView) findViewById(R.id.main_title_textView);
        nextButton = (Button) findViewById(R.id.nextquiz);
        nextButton.setOnClickListener(this);

        prevButton = (Button) findViewById(R.id.previousquiz);
        prevButton.setOnClickListener(this);


        new AsyncQuestions().execute();

    }

    @Override
    public void onClick(View v) {

        if(v==nextButton){
            if (i<result.size()) {
                i++;
                setLayout(result, i);
            }
        }

        if (v==prevButton){
        if (i>0) {
            i--;
            setLayout(result, i);
        }
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
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(30000);
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

                    // Pass data to onPostExecute method
                    Log.d("parsing","QUestions is: "+question+" answers is: "+answers);


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
                setLayout(result,i);

            } else if (result == null) {

                // If username and password does not match display a error message


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
                newRadioButton.setText("" + questionssplit[a]);
                LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.WRAP_CONTENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT);
                radioGroup.addView(newRadioButton, a, layoutParams);

                if(i==result.size()-1) {

                    nextButton.setText("Afslut spørgsmål");
                }
                else {
                    nextButton.setText("Næste spørgsmål");
                }
            }
        }
        else{

            Log.d("Arrayslut",""+i);
           // Intent intent = new Intent(SuccessActivity.this,MainActivity.class);
           // startActivity(intent);
        }

    }//end setLayout

}
