package com.example.bigmac.diaryinterpreter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UploadAnswers extends AppCompatActivity implements View.OnClickListener {

//    //sharedpreferences
//    SharedPreferences pref;
//    SharedPreferences.Editor editor;
//
//    private Button confirmUpload;
//    private Button regret;
//    private Button confirmAnswers;
//
//    private TextView uploadtext;
//
//    LinearLayout ll;
//    LinearLayout buttonholder;
//
//    private ArrayList<String> answers;
//    private ArrayList<String> extraanswers;
//    private ArrayList<JsonHolder> questions;
//    private String finalString;
//    StringBuilder stringBuilder = new StringBuilder();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_upload_answers);
//
//        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
//        editor = pref.edit();
//
//        uploadtext = (TextView) findViewById(R.id.uploadText);
//
//        ll = (LinearLayout) findViewById(R.id.uploadlinear);
//        buttonholder = (LinearLayout) findViewById(R.id.uploadBtnsHolder);
//
//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            answers = extras.getStringArrayList("answersArray");
//            extraanswers = extras.getStringArrayList("extraanswersArray");
//           // questions = PersonInfo.getQuestionsArray();
//            questions = (ArrayList<JsonHolder>) getIntent().getSerializableExtra("questionArray");
//
//            if (questions.size()>0) {
//                showAnswersNormal();
//                createButtons();
//            }
//
//            //If the other activity passed an empty question array this means no question is avalible
//            //most likely a time event without questions
//            if (questions.size()==0){
//                noAnswers();
//            }
//
//        }
//
//    }
//
//    public void createButtons(){
//
//        buttonholder.removeAllViews();
//
//        switch (PersonInfo.getTrigger()){
//
//            case 0:
//                confirmUpload = new Button(this);
//                confirmUpload.setText("Bekræft og upload");
//                confirmUpload.setOnClickListener(this);
//                regret = new Button(this);
//                regret.setText("Start forfra");
//                regret.setOnClickListener(this);
//                buttonholder.addView(confirmUpload);
//                buttonholder.addView(regret);
//                break;
//
//            case 1:
//                regret = new Button(this);
//                regret.setText("Start forfra");
//                regret.setOnClickListener(this);
//                confirmAnswers = new Button(this);
//                confirmAnswers.setText("Bekræft svar");
//                confirmAnswers.setOnClickListener(this);
//                buttonholder.addView(regret);
//                buttonholder.addView(confirmAnswers);
//                break;
//        }
//
//
//
//    }
//
//
//    public void showAnswersNormal(){
//        //used for EQ iterator
//        int aa =0;
//        // String[] questionssplit = questions.get(1).getAnswers();
//        for (int a=0;a<questions.size();a++){
//
//            //Checks if this is the last question and that there is no extra question combined, if true, dont put comma
//            if (a==questions.size()-1 && Integer.parseInt(questions.get(a).getExtraID())==-1){
//                stringBuilder.append(answers.get(a));
//            }
//            //else this is not last questions, just put a comma
//            else{
//                stringBuilder.append(answers.get(a) + ",");
//            }
//
//            final TextView rowTextView = new TextView(this);
//            final TextView answerTextview = new TextView(this);
//            // set some properties of rowTextView or something
//            rowTextView.setTypeface(null, Typeface.BOLD);
//            rowTextView.setText(""+questions.get(a).getQuestion());
//            String[] questionssplit = questions.get(a).getAnswers();
//
//            // -- CHECK FOR TYPE HERE -- //
//
//            //1 = multiplechoice
//            if(questions.get(a).getType()==1) {
//                answerTextview.setText("" + questionssplit[Integer.parseInt(answers.get(a))]);
//            }
//            //2 = userInput
//            if(questions.get(a).getType()==2) {
//                answerTextview.setText("" + answers.get(a));
//            }
//
//            // add the textview to the linearlayout
//            ll.addView(rowTextView);
//            ll.addView(answerTextview);
//
//            //check if any extraAnswers exists (only viable for multiplechoice questions at the moment
//            if(Integer.parseInt(questions.get(a).getExtraID())>-1){
//
//                if (a<questions.size()-1) {
//                    stringBuilder.append(extraanswers.get(aa) + ",");
//                }
//                //if this is the last questions with extra question dont put comma
//                if (a==questions.size()-1){
//                    stringBuilder.append(extraanswers.get(aa));
//                }
//
//                //If extraAnswers excites check if they are answered/activated, if true, show the answer(s)
//                if (Integer.parseInt(extraanswers.get(aa))>-1) {
//
//
//                    final TextView extrarowTextView = new TextView(this);
//                    final TextView extraanswerTextview = new TextView(this);
//                    extrarowTextView.setTypeface(null, Typeface.BOLD_ITALIC);
//                    extrarowTextView.setText("Ekstra spørgsmål: " + questions.get(a).getExtraQuestion());
//                    String[] extraquestionssplit = questions.get(a).getExtraAnswers();
//                    if (Integer.parseInt(extraanswers.get(aa)) > -1) {
//                        extraanswerTextview.setText("" + extraquestionssplit[Integer.parseInt(extraanswers.get(aa))]);
//                    }
//                    ll.addView(extrarowTextView);
//                    ll.addView(extraanswerTextview);
//                }
//                aa++;
//
//            }
//
//
//        }//end loop!
//        finalString = stringBuilder.toString();
//        Log.d("mystring",""+finalString);
//    }
//
//    public void noAnswers(){
//
//        uploadtext.setText("Ingen spørgsmål at vise");
//
//    }
//
//
    @Override
    public void onClick(View v) {
        //hardcoded right now, shall be transfered from prev activity
        int userid = PersonInfo.getUserID();
        int diaryid= PersonInfo.getDiaryID();
        //Create this date - same format as SQL
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());
        //This is not a time event, therefor set time to 0
        String time = "00:00:00";
        //Get eventID meaning who belongs these question to. 0 = always original diary
        int eventID = PersonInfo.getQuestionGrp();
//        if (v == confirmUpload){
//            new AysncUpload().execute(finalString,userid,diaryid);
//        }
//
//        if (v == regret){
//            Intent i = new Intent(UploadAnswers.this,InterpreterActivity.class);
//            startActivity(i);
//        }
//
//        if (v==confirmAnswers){
//            Toast.makeText(UploadAnswers.this, "Husk at slå knappen fra når hændelsen er færdig", Toast.LENGTH_LONG).show();
//            Intent i = new Intent(UploadAnswers.this,MainUserActivity.class);
//            editor.putString("answers"+PersonInfo.getQuestionGrp(),finalString);
//            editor.commit();
//            startActivity(i);

        //}
    }
//
//    //Class: Upload answers to database via php - not yet implementet
//    private class AysncUpload extends AsyncTask<String, String, String> {
//        ProgressDialog pdLoading = new ProgressDialog(UploadAnswers.this);
//        HttpURLConnection conn;
//        URL url = null;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            //this method will be running on UI thread
//            pdLoading.setMessage("\tUploader spørgsmål");
//            pdLoading.setCancelable(false);
//            pdLoading.show();
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            try {
//
//                // Enter URL address where your php file resides
//
//                url = new URL("http://hadsundmotion.dk/finalphpscritpforarray.php");
//
//
//            } catch (MalformedURLException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                return "exception";
//            }
//
//            try {
//                // Setup HttpURLConnection class to send and receive data from php and mysql
//                conn = (HttpURLConnection)url.openConnection();
//                conn.setReadTimeout(15000);
//                conn.setConnectTimeout(30000);
//                conn.setRequestMethod("POST");
//
//                // setDoInput and setDoOutput method depict handling of both send and receive
//                conn.setDoInput(true);
//                conn.setDoOutput(true);
//
//                Log.d("params0", "" + params[0]);
//                Log.d("params1",""+params[1]);
//                Log.d("params1",""+params[2]);
//
//
//                // Append parameters to URL !NEEDED: 1.Diary id_colector, Patient ID, Answers,
//                Uri.Builder builder = new Uri.Builder()
//                        .appendQueryParameter("answers", params[0])
//                        .appendQueryParameter("userid", params[1])
//                        .appendQueryParameter("diaryid", params[2]);
//                String query = builder.build().getEncodedQuery();
//
//                // Open connection for sending data
//                OutputStream os = conn.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(
//                        new OutputStreamWriter(os, "UTF-8"));
//                writer.write(query);
//                writer.flush();
//                writer.close();
//                os.close();
//                conn.connect();
//
//            } catch (IOException e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//                return "exception";
//            }
//            try {
//
//                int response_code = conn.getResponseCode();
//
//                // Check if successful connection made
//                if (response_code == HttpURLConnection.HTTP_OK) {
//                    Log.d("connectiontest1","hvad er status - OK");
//                    // Read data sent from server
//                    InputStream input = conn.getInputStream();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//                    StringBuilder result = new StringBuilder();
//                    String line;
//
//                    while ((line = reader.readLine()) != null) {
//                        result.append(line);
//                        Log.d("testline",line);
//                    }
//
//                    // Pass data to onPostExecute method
//                    return(result.toString());
//
//                }else{
//                    Log.d("connectiontest2","hvad er status - BAD");
//                    return("unsuccessful");
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                return "exception";
//            } finally {
//                conn.disconnect();
//            }
//        }
//        //result from doInBackground goes here
//        protected void onPostExecute(String done) {
//            //this method will be running on UI thread
//            pdLoading.dismiss();
//
//
//            if(done.equalsIgnoreCase("true"))
//            {
//                /* Here launching another activity when login successful. If you persist login state
//                use sharedPreferences of Android. and logout button to clear sharedPreferences.
//                 */
//                Toast.makeText(UploadAnswers.this, "Upload gennemført - tak!", Toast.LENGTH_LONG).show();
//                Intent back = new Intent(UploadAnswers.this,MainUserActivity.class);
//                startActivity(back);
//
//
//            }else if (done.equalsIgnoreCase("false")){
//
//                // if SQL stmt is false
//                Toast.makeText(UploadAnswers.this, "Kunne ikke uploade!", Toast.LENGTH_LONG).show();
//
//            } else if (done.equalsIgnoreCase("exception") || done.equalsIgnoreCase("unsuccessful")) {
//
//                Toast.makeText(UploadAnswers.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
//
//            }
//
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        //disable back button
//    }

}
