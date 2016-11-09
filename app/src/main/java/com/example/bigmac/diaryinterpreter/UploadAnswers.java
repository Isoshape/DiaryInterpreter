package com.example.bigmac.diaryinterpreter;

import android.app.ProgressDialog;
import android.content.Intent;
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
import java.util.ArrayList;

public class UploadAnswers extends AppCompatActivity implements View.OnClickListener {

    private Button upload;
    private ArrayList<Integer> answers;
    private ArrayList<JsonHolder> questions;
    private String finalString;
    StringBuilder stringBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_answers);

        upload = (Button) findViewById(R.id.uploadButton);
        upload.setOnClickListener(this);



        LinearLayout ll = (LinearLayout) findViewById(R.id.uploadlinear);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            answers = extras.getIntegerArrayList("answersArray");
             questions = (ArrayList<JsonHolder>) getIntent().getSerializableExtra("questionArray");

               // String[] questionssplit = questions.get(1).getAnswers();
                for (int a=0;a<questions.size();a++){

                    stringBuilder.append(answers.get(a)+",");


                    final TextView rowTextView = new TextView(this);
                    final TextView answerTextview = new TextView(this);
                    // set some properties of rowTextView or something
                    rowTextView.setTypeface(null, Typeface.BOLD);
                    rowTextView.setText(""+questions.get(a).getQuestion());
                    String[] questionssplit = questions.get(a).getAnswers();
                    answerTextview.setText(""+questionssplit[answers.get(a)]);

                    // add the textview to the linearlayout
                    ll.addView(rowTextView);
                    ll.addView(answerTextview);

                }
            finalString = stringBuilder.toString();
            Log.d("mystring",""+finalString);

        }

    }
    @Override
    public void onClick(View v) {
        String id = "5";
        if (v == upload){
            new AysncUpload().execute(finalString,id);
        }

    }

    //Class: Upload answers to database via php - not yet implementet
    private class AysncUpload extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(UploadAnswers.this);
        HttpURLConnection conn;
        URL url = null;

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
            try {

                // Enter URL address where your php file resides

                url = new URL("http://hadsundmotion.dk/upload.php");


            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }

            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(30000);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Log.d("params0", "" + params[0]);
                Log.d("params1",""+params[1]);


                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("answers", params[0])
                        .appendQueryParameter("id", params[1]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }
            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {
                    Log.d("connectiontest1","hvad er status - OK");
                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                        Log.d("testline",line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());

                }else{
                    Log.d("connectiontest2","hvad er status - BAD");
                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }
        }
        //result from doInBackground goes here
        protected void onPostExecute(String done) {
            //this method will be running on UI thread
            pdLoading.dismiss();


            if(done.equalsIgnoreCase("true"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                Toast.makeText(UploadAnswers.this, "Upload gennemført - tak!", Toast.LENGTH_LONG).show();
                Intent back = new Intent(UploadAnswers.this,MainActivity.class);
                startActivity(back);


            }else if (done.equalsIgnoreCase("false")){

                // if SQL stmt is false
                Toast.makeText(UploadAnswers.this, "Kunne ikke uploade!", Toast.LENGTH_LONG).show();

            } else if (done.equalsIgnoreCase("exception") || done.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(UploadAnswers.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }

        }
    }

    @Override
    public void onBackPressed() {
        //disable back button
    }

}
