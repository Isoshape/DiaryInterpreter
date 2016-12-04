package com.example.bigmac.diaryinterpreter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

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
import java.util.concurrent.TimeUnit;


public class MainUserActivity extends AppCompatActivity implements View.OnClickListener {

    //sharedpreferences
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    //QuestionsString & eventsString
    String jsonQuestionStringData=null;
    String jsonEventsStringData=null;

    //Must be programmatically made
    private Button launchInterpreterBtn;


    private LinearLayout eventsholder;

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    ArrayList<JsonHolder> allquestions = new ArrayList<JsonHolder>();
    ArrayList<EventHolder> allEvents = new ArrayList<EventHolder>();

    HttpURLConnection conn;
    URL url = null;

    //MULIGHED FOR AT TILGÅ HOVEDEDAGBOG + VIS FORSKELLIGE HÆNDELSES MULIGHEDER
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        //pref with private mode = 0 (the created file can only be accessed by the calling application)
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();

        eventsholder = (LinearLayout) findViewById(R.id.eventsLayout);

        launchInterpreterBtn = (Button) findViewById(R.id.InterpreterBtn);
        launchInterpreterBtn.setOnClickListener(this);
        TextView welcome = (TextView) findViewById(R.id.welcomeTextEdit);
        welcome.setText("Velkommen " + PersonInfo.getFirstName());

        //Check if data is allrdy stored from previous session
        jsonQuestionStringData = pref.getString("jsondata",null);
        jsonEventsStringData = pref.getString("jsoneventdata",null);
        //


        if (jsonQuestionStringData != null && jsonEventsStringData != null){

            parseJsonQuestions();
            parseJsonEvents();
            setLayout();

        }

        //if data is not avalible fetch it from database
        else {
            new AsyncQuestions().execute();

        }

    }

    //Methode for creating diary buttons + events buttons/switchs
    public void setLayout(){

        Log.d("HEJ FRA LAYOUT","HEJ HEJ");
    //SAVE DATE IN SHAREDPREF WHEN DIARY IS RUN. WHEN THIS ACTIVITY RUNS CHECK SP, DEFAULT RETURN VALUE SHOULD BE THIS DATE = MEANING NO DIARY HAS EVER BEEN RUN!
        for (int j = 0; j < allEvents.size(); j++) {

            if (allEvents.get(j).getEventType()==1){
                Switch switchTag = new Switch(this);
                switchTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                switchTag.setText("" + allEvents.get(j).getEventName());
                switchTag.setTag(allEvents.get(j).getEventType() + "," + allEvents.get(j).getEventID());
                switchTag.setOnClickListener(switchHandler);
                switchTag.setChecked(pref.getBoolean("switchState"+allEvents.get(j).getEventID(),false));
                //switchTag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener());
                eventsholder.addView(switchTag);

            }

            if (allEvents.get(j).getEventType()==2){
                Button btnTag = new Button(this);
                btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                btnTag.setText("" + allEvents.get(j).getEventName());
                btnTag.setTag(allEvents.get(j).getEventType() + "," + allEvents.get(j).getEventID());

                btnTag.setOnClickListener(myhandler2);

                eventsholder.addView(btnTag);
            }

        }

    }

    //Methode for parsin Json Array with Questions
    public void parseJsonQuestions(){

        JSONArray jsonArrayQuestions;

        try {
            jsonArrayQuestions = new JSONArray(jsonQuestionStringData);
            for (int i=0; i<jsonArrayQuestions.length(); i++){


                JSONObject jsonProductObject = jsonArrayQuestions.getJSONObject(i);
                String question = jsonProductObject.getString("question");
                String answers = jsonProductObject.getString("answers");
                String extraID = jsonProductObject.getString("extraID");
                String extraQuestion = jsonProductObject.getString("extraQuestion");
                String  extraAnswers = jsonProductObject.getString("extraAnswers");
                int type = jsonProductObject.getInt("type");
                allquestions.add(new JsonHolder(question, answers, extraID, extraQuestion, extraAnswers, type));
            }

            Log.d("ArrayQuestions",""+jsonArrayQuestions);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        PersonInfo.setAllquestions(allquestions);

    }
    //Methode for parsin Json Array with evens
    public void parseJsonEvents(){

        JSONArray jsonArrayEvents;
        try {
            jsonArrayEvents = new JSONArray(jsonEventsStringData);
        for (int i=0; i<jsonArrayEvents.length(); i++){

            JSONObject jsonProductObject = jsonArrayEvents.getJSONObject(i);
            String eventID = jsonProductObject.getString("eventID");
            int eventType = jsonProductObject.getInt("eventType");
            String eventName = jsonProductObject.getString("eventName");
            allEvents.add(new EventHolder(eventID,eventType,eventName));

        }

            Log.d("ArrayEvents",""+jsonArrayEvents);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        PersonInfo.setAllevents(allEvents);

    }

    View.OnClickListener myhandler2 = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Button universalbutton = (Button) v;

            String[] eventInfo = universalbutton.getTag().toString().split(",");
            String eventType = eventInfo[0];
            String eventID = eventInfo[1];

            if (eventType.equalsIgnoreCase("2")){

                Log.d("Event  ",""+eventType);
                Log.d("ID",""+eventID);

            }

        }
    };

    View.OnClickListener switchHandler = new View.OnClickListener() {



        @Override
        public void onClick(View v) {

            //HUSK SWITCH TILSTAND SKAL SLETTES HVIS NY LOGGER IND

            Switch universalSwitch = (Switch) v;

            String[] eventInfo = universalSwitch.getTag().toString().split(",");
            String eventType = eventInfo[0];
            String eventID = eventInfo[1];

            if (universalSwitch.isChecked()){

                long startTime = System.currentTimeMillis();
                //Saving the state of the switch, for when returning to the activity
                editor.putBoolean("switchState" + eventID, true);
                //saving the timestart value
                editor.putLong("starttime"+eventID,+System.currentTimeMillis());
                editor.commit();

            }

            if (!universalSwitch.isChecked()){
                //Saving the state of the switch, for when returning to the activity
                editor.putBoolean("switchState" + eventID, false);
                //saving the timeend value
                editor.putLong("endtime" + eventID, +System.currentTimeMillis());
                editor.commit();
                eventEnded(eventID);

            }



        }
    };


    public void eventEnded(String eventID){

       long endTime =  pref.getLong("endtime"+eventID,0);
       long startTime = pref.getLong("starttime"+eventID,0);
        long result = endTime - startTime;
        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(result),
                TimeUnit.MILLISECONDS.toMinutes(result) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(result)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(result) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(result)));

        String[] timespent = hms.split(":");
        Log.d("Tiden er ",eventID+" SLOG TIDEN FRA TIL "+hms);

    }

    @Override
    public void onClick(View v) {

        if (v==launchInterpreterBtn){

            Intent launchInterpreter = new Intent(MainUserActivity.this,InterpreterActivity.class);
            startActivity(launchInterpreter);

        }
    }

    //AsyncTask: getting questions from database
    private class AsyncQuestions extends AsyncTask<String, String, String>{

        ProgressDialog pdLoading = new ProgressDialog(MainUserActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            pdLoading.setMessage("\tHenter dagbog");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String resultEvents = getEvents();
           String resultQuestion = getQuestions();


            return resultQuestion + " " + resultEvents;
        }


        //What happens when doInBackground is done
        @Override
        protected void onPostExecute(String s) {

            String[] results = s.split(" ");

            pdLoading.dismiss();

            if (results[0].equalsIgnoreCase("Success") && results[1].equalsIgnoreCase("Success")) {

                parseJsonQuestions();
                parseJsonEvents();
                setLayout();


            }
            else
                Log.d("ØVVVVVV!!","Jaaaaa");
        }
    }


    public String getQuestions(){


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

            // Append parameter ID to URL
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("id", PersonInfo.getDiaryID());
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
            //return "exception";
        }

        try {

            int response_code = conn.getResponseCode();

            // Check if successful connection made
            if (response_code == HttpURLConnection.HTTP_OK) {

                Log.d("connection", "forbindelse etableret");

                // Read data sent from server
                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line=null;

                while ((line = reader.readLine()) != null) {
                    result.append(line);


                    try {
                        JSONArray arr = new JSONArray(line);
                        jsonQuestionStringData = arr.toString();
                        editor.putString("jsondata", arr.toString());
                        editor.commit();


                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                return ("unsuccessful");
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "exception";
        }  finally {
            conn.disconnect();
        }

        Log.d("success", "ja?");
        return ("Success");

    }

    public String getEvents(){

        try {

            // Enter URL address where your php file resides
            url = new URL("http://hadsundmotion.dk/events.php");

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

            // Append parameter ID to URL
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("id", PersonInfo.getDiaryID());
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
            //return "exception";
        }

        try {

            int response_code = conn.getResponseCode();

            // Check if successful connection made
            if (response_code == HttpURLConnection.HTTP_OK) {

                Log.d("connection", "forbindelse etableret");

                // Read data sent from server
                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();

                String line=null;
                String eventID=null;
                int eventType;
                String eventName=null;


                while ((line = reader.readLine()) != null) {
                    result.append(line);

                    try {
                        JSONArray arr = new JSONArray(line);
                        jsonEventsStringData = arr.toString();
                        editor.putString("jsoneventdata", arr.toString());
                        editor.commit();

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                return ("unsuccessful");
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "exception";
        }  finally {
            conn.disconnect();
        }

        return ("Success");

    }

}
