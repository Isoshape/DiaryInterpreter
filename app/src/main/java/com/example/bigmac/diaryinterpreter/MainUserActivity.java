package com.example.bigmac.diaryinterpreter;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
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
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainUserActivity extends AppCompatActivity implements View.OnClickListener {

    List<Answers> shopList;

    //sharedpreferences
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    //QuestionsString & eventsString
    String jsonQuestionStringData=null;
    String jsonEventsStringData=null;

    //Must be programmatically made
    private Button launchInterpreterBtn;

    //Layout for events placement
    //private LinearLayout eventsholder;
    private LinearLayout timeEventsHolder;
    private LinearLayout normalEventsHolder;


    //test btn
    private Button test;

    //Timeout fields
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    //Questions and Events arrays
    ArrayList<JsonHolder> allquestions = new ArrayList<JsonHolder>();
    ArrayList<EventHolder> allEvents = new ArrayList<EventHolder>();

    //php connector
    HttpURLConnection conn;
    URL url = null;

    //sqlite
    DBHandler db;

    //toolbar
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");

        ImageView testimmage = (ImageView) toolbar.findViewById(R.id.logohospital);
        String variableValue = PersonInfo.getLogourl();
        testimmage.setImageResource(getResources().getIdentifier(variableValue, "drawable", getPackageName()));

        db = new DBHandler(this);

        //pref with private mode = 0 (the created file can only be accessed by the calling application)
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();

        test = (Button) findViewById(R.id.testbtn);
        test.setOnClickListener(this);

       // eventsholder = (LinearLayout) findViewById(R.id.eventsLayout);
        timeEventsHolder = (LinearLayout) findViewById(R.id.timeEvents);
        normalEventsHolder = (LinearLayout) findViewById(R.id.normalEvents);

        launchInterpreterBtn = (Button) findViewById(R.id.InterpreterBtn);
        launchInterpreterBtn.setOnClickListener(this);
       // TextView welcome = (TextView) findViewById(R.id.welcomeTextEdit);
       // welcome.setText("Velkommen " + PersonInfo.getFirstName());

        //Check if data (questions + events) is allrdy stored from previous session
        jsonQuestionStringData = pref.getString("jsondata",null);
        jsonEventsStringData = pref.getString("jsoneventdata",null);
        //

        //if data is availbe parse the data
        if (jsonQuestionStringData != null && jsonEventsStringData != null){

            parseJsonQuestions();
            parseJsonEvents();
            setLayout();

        }

        //if data is not avalible fetch it from database post execute will call pass methodes
        else {
            new AsyncQuestions().execute();

        }

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

        return super.onOptionsItemSelected(item);
    }




    //Methode for creating diary buttons + events buttons/switchs
    public void setLayout(){

        //MIssing diary buttons
        //SAVE DATE IN SHAREDPREF WHEN DIARY IS RUN. WHEN THIS ACTIVITY RUNS CHECK SP, DEFAULT RETURN VALUE SHOULD BE THIS DATE = MEANING NO DIARY HAS EVER BEEN RUN!

        Log.d("HEJ FRA LAYOUT","HEJ HEJ");
        for (int j = 0; j < allEvents.size(); j++) {

            //If the eventype is 1, this equals to time event = create switch
            if (allEvents.get(j).getEventType()==1){
                Switch switchTag = new Switch(this);
                switchTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                switchTag.setText("" + allEvents.get(j).getEventName());
                switchTag.setTag(allEvents.get(j).getEventType() + "," + allEvents.get(j).getEventID());
                switchTag.setOnClickListener(switchEventHandler);
                //when creating the button, get the switch state if it was previously set, if not set to false
                switchTag.setChecked(pref.getBoolean("switchState"+allEvents.get(j).getEventID(),false));
                timeEventsHolder.addView(switchTag);

            }
            //if the eventype is 0, this equals to question event = create button
            if (allEvents.get(j).getEventType()==0){
                Button btnTag = new Button(this);
                btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                btnTag.setText("" + allEvents.get(j).getEventName());
                btnTag.setTag(allEvents.get(j).getEventType() + "," + allEvents.get(j).getEventID());
                btnTag.setOnClickListener(normalEventHandler);
                normalEventsHolder.addView(btnTag);
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
                int questionID = jsonProductObject.getInt("questionID");
                int visible = jsonProductObject.getInt("visible");
                int operation = jsonProductObject.getInt("operation");
                int qcondition = jsonProductObject.getInt("qcondition");
                int questionGrp = jsonProductObject.getInt("questionGrp");
                int type = jsonProductObject.getInt("type");
                String question = jsonProductObject.getString("question");
                String possibleAnswer = jsonProductObject.getString("possibleAnswer");
               allquestions.add(new JsonHolder(questionID,visible,operation,qcondition,questionGrp,type,question,possibleAnswer));
            }

            Log.d("ArrayQuestions",""+jsonArrayQuestions);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        PersonInfo.setAllquestions(allquestions);

    }
    //Methode for parsin Json Array with events
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

    View.OnClickListener normalEventHandler = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Button universalbutton = (Button) v;

            String[] eventInfo = universalbutton.getTag().toString().split(",");
            String eventType = eventInfo[0];
            String eventID = eventInfo[1];
            PersonInfo.setQuestionGrp(Integer.parseInt(eventID));
            PersonInfo.setTrigger(Integer.parseInt(eventType));

            activateIntepreter();
        }
    };

        View.OnClickListener switchEventHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //HUSK SWITCH TILSTAND SKAL SLETTES HVIS NY LOGGER IND
            //ALT I SP SKAL SLETTES VED NY BRUGER

            Switch universalSwitch = (Switch) v;

            String[] eventInfo = universalSwitch.getTag().toString().split(",");
            String eventType = eventInfo[0];
            String eventID = eventInfo[1];

            if (universalSwitch.isChecked()){

                //Sets which questions belongs to this event (questionGrp)
                PersonInfo.setQuestionGrp(Integer.parseInt(eventID));
                //TRIGGER ID IS WHAT KIND OF EVENT THIS IS WHEN IN UPLOADANSWERS ACTIVITY.
                //IF TIME EVENT WE DONT WANT TO UPLOAD ANSWERS BEFORE TIME SWITCH IS OFF, THEREFOR DIFFERENT HANDLING IS REQUIRED
                PersonInfo.setTrigger(Integer.parseInt(eventType));
                //Set the eventID
                long startTime = System.currentTimeMillis();
                //Saving the state of the switch, for when returning to the activity
                editor.putBoolean("switchState" + eventID, true);
                //saving the timestart value
                editor.putLong("starttime"+eventID,+System.currentTimeMillis());
                editor.commit();
                Log.d("Trigger is now ", "" + PersonInfo.getTrigger());
                activateIntepreter();
            }

            if (!universalSwitch.isChecked()){
                //Saving the state of the switch, for when returning to the activity
                editor.putBoolean("switchState" + eventID, false);
                //saving the timeend value
                editor.putLong("endtime" + eventID, +System.currentTimeMillis());
                editor.commit();
                String svar = pref.getString("answers"+PersonInfo.getQuestionGrp(),null);
                Log.d("Svare fra tidligere var",""+svar);

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

        Log.d("Tiden er ",eventID+" SLOG TIDEN FRA TIL "+hms);
        int session = pref.getInt("session"+eventID,-1);
        db.updateDuration(hms,session);


    }

    @Override
    public void onClick(View v) {

        if (v==launchInterpreterBtn){

            //The diary always have questionGrp = 0;
            PersonInfo.setTrigger(0);
            PersonInfo.setQuestionGrp(0);
            activateIntepreter();

        }

        if (v==test){

            Log.d("knap trykket","hejsa");
            new uploadAnswers().execute();


        }

    }

    public void activateIntepreter(){

        Intent launchInterpreter = new Intent(MainUserActivity.this,InterpreterActivity.class);
        startActivity(launchInterpreter);

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

    Log.d("getquestion","jeg er aktiveret");
        try {

            // Enter URL address where your php file resides
            url = new URL("http://10.0.2.2/questions.php");

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
                    .appendQueryParameter("diaryid", ""+PersonInfo.getDiaryID());
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

                Log.d("connection1", "forbindelse etableret1");

                // Read data sent from server
                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line=null;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                    Log.d("db",""+line);


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
            url = new URL("http://10.0.2.2/events.php");

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
                    .appendQueryParameter("diaryid", ""+PersonInfo.getDiaryID());
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

    private class uploadAnswers extends AsyncTask<String,String,String>{


        @Override
        protected String doInBackground(String... params) {

            DBHandler db = new DBHandler(MainUserActivity.this);
            List<Answers> answerList = new ArrayList<Answers>();
            JSONArray array = new JSONArray();

            answerList = db.getAllAnswers();

            for (int b=0;b < answerList.size();b++){

                try {
                    JSONObject jsonAnswers = new JSONObject();
                    jsonAnswers.put("questionID",answerList.get(b).getQuestionID());
                    jsonAnswers.put("diaryID",answerList.get(b).getDiaryID());
                    jsonAnswers.put("uuid",answerList.get(b).getUUID());
                    jsonAnswers.put("answer",answerList.get(b).getAnswer());
                    jsonAnswers.put("questionGrp",answerList.get(b).getQuestionGrp());
                    jsonAnswers.put("dato",answerList.get(b).getDate());
                    jsonAnswers.put("timer", answerList.get(b).getTime());
                    jsonAnswers.put("duration", answerList.get(b).getDuration());
                    array.put(jsonAnswers);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            String test = sendAnswers(array);

//            Log.d("json array er ",""+array);
          Log.d("json array String er ",""+array.toString());




            return test;
        }


        @Override
        protected void onPostExecute(String s) {

            Log.d("Den er færdig",""+s);

        }
    }//end upload class

    private String sendAnswers(JSONArray array){

        try {

            // Enter URL address where your php file resides
            url = new URL("http://10.0.2.2/uploadAnswers.php");

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
                    .appendQueryParameter("json", array.toString());
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

                return("unsuccessful");
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "exception";
        } finally {
            conn.disconnect();
        }

    }

}
