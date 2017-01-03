package com.example.bigmac.diaryinterpreter;


import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class MainUserActivity extends AppCompatActivity implements View.OnClickListener {

    ShowcaseView showcaseView;

    int counter = 0;
    boolean answerstate = false;

    //sharedpreferences
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    //QuestionsString & eventsString
    String jsonQuestionStringData = null;
    String jsonEventsStringData = null;

    //Must be programmatically made
    private Button launchInterpreterBtn;
    private Button openEventButton;
    private TextView timeleftTextview;
    private TextView incidentTextview;
    private TextView myevents;


    //Layout for events placement
    //private LinearLayout eventsholder;
    private LinearLayout timeEventsHolder;
    private LinearLayout normalEventsHolder;
    private RelativeLayout relativScroll;


    //Timeout fields
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    //Questions and Events arrays
    ArrayList<JsonHolder> allquestions = new ArrayList<JsonHolder>();
    ArrayList<EventHolder> allEvents = new ArrayList<EventHolder>();
    //textviewholder
    ArrayList<TextView> txtArray = new ArrayList<>();

    //php connector
    HttpURLConnection conn;
    URL url = null;

    //sqlite
    DBHandler db;

    //toolbar
    private Toolbar toolbar;

    //thread
    static UploadThread uploadThread;

    //drawables
    Drawable drawableTop;

    //date variables
    String date;
    boolean state;

    SimpleDateFormat sdf;
    String currentDate;
    long msInDay = 86400000;


    AlertDialog.Builder alertDialog = null;
    Animation myAnim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        sdf = new SimpleDateFormat("HH:mm:ss");
        myAnim =  AnimationUtils.loadAnimation(this, R.anim.btnflash);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        alertDialog = new AlertDialog.Builder(MainUserActivity.this);
        drawableTop = ResourcesCompat.getDrawable(getResources(), R.drawable.eventperson, null);

        //pref with private mode = 0 (the created file can only be accessed by the calling application)
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();

        //Declare all views
        timeleftTextview = (TextView) findViewById(R.id.timeleftTextview);
        incidentTextview = (TextView) findViewById(R.id.eventsTxtview);
        myevents = (TextView) findViewById(R.id.eventsTxtview);
        launchInterpreterBtn = (Button) findViewById(R.id.InterpreterBtn);
        openEventButton = (Button) findViewById(R.id.openEventsBtn);
        openEventButton.setOnClickListener(this);
        launchInterpreterBtn.setOnClickListener(this);
        // eventsholder = (LinearLayout) findViewById(R.id.eventsLayout);
        timeEventsHolder = (LinearLayout) findViewById(R.id.timeEvents);
        normalEventsHolder = (LinearLayout) findViewById(R.id.normalEvents);
        relativScroll = (RelativeLayout) findViewById(R.id.relativScroll);


        //SET TOOLBAR
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");

        ImageView testimmage = (ImageView) toolbar.findViewById(R.id.logohospital);
        String variableValue = PersonInfo.getLogourl();
        testimmage.setImageResource(getResources().getIdentifier(variableValue, "drawable", getPackageName()));

        db = new DBHandler(this);


        //Check if data (questions + events) is allrdy stored from previous session
        jsonQuestionStringData = pref.getString("jsondata", null);
        jsonEventsStringData = pref.getString("jsoneventdata", null);
        //

        //if data is availbe parse the data
        if (jsonQuestionStringData != null && jsonEventsStringData != null) {

            parseJsonQuestions();
            parseJsonEvents();
            setLayout();

        }

        //if data is not avalible fetch it from database post execute will call pass methodes
        else {
            new AsyncQuestions().execute();

        }

    }


    public void countDown() {

        final SimpleDateFormat timezone = new SimpleDateFormat("HH:mm:ss");
        timezone.setTimeZone(TimeZone.getTimeZone("GMT"));

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getDefault());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        final long howMany = msInDay - (System.currentTimeMillis() - c.getTimeInMillis());

        new CountDownTimer(howMany, 1000) {

            public void onTick(long millisUntilFinished) {

                if (answerstate == true) {


                }
                else if (answerstate == false) {

                    String time = timezone.format(millisUntilFinished);
                    timeleftTextview.setText("Tidsfrist: " + time);

                    if ((msInDay/2)> howMany){

                   


                    }

                    //here you can have your logic to set text to edittext
                }
            }

            public void onFinish() {
                //next patch - when timer finished check state. If state is still false notice that that day was not answered!
                editor.putBoolean("state", false);
                editor.commit();
                answerstate = false;
                countDown();
                launchInterpreterBtn.setBackgroundResource(R.drawable.bluecircle);
                //timeleftTextview.setText("Tak for dit svar. På gensyn i morgen");
            }

        }.start();


    }



    //when activity is active this methodes get called
    @Override
    public void onStart() {
        super.onStart();
        Log.d("onStart", " er kaldet!!");
        //checks if thread never been created, if true, create it and run it
        if (uploadThread == null) {
            startGenerating();
            // Log.d("Aldrig startet, nu", "nu");
        }
        //when returning to activity check if thread is running if true, do nothing
        //if false start it
        else {
            if (uploadThread.isAlive() == true) {
                //Log.d("den lever allerede", "nu");
            } else if (uploadThread.isAlive() == false) {
                startGenerating();
                //Log.d("Starter den op", "nu");
            }

        }


        //CHECK IF PT HAS ANSWERED TO DAY
        SimpleDateFormat dayformat = new SimpleDateFormat("yyyy-MM-dd");
        date = pref.getString("date", "0000-00-00");
        state = pref.getBoolean("state", false);
        currentDate = dayformat.format(new Date());

        Log.d("date og state er: ", "state: " + state + " date: " + date + " currentdate er: " + currentDate);

        if (date.equalsIgnoreCase(currentDate) && state == true) {
            Log.d("Kommer vi herind?","test");
            answerstate = true;
            launchInterpreterBtn.setBackgroundResource(R.drawable.circle);
            timeleftTextview.setText("Tak for dit svar. På gensyn i morgen");

        } else {
            editor.putBoolean("state", false);
            editor.commit();
            answerstate = false;
        }
        ///////
       countDown();


    }

    @Override
    public void onPause() {
        super.onPause();

    }

    protected void onStop() {
        // call the superclass method first
        super.onStop();

    }

    protected void onResume() {
        super.onResume();
        setLayout();
    }

    private void startGenerating() {

        Log.d("start blev kaldt", "åh nej");
        uploadThread = new UploadThread(this);
        uploadThread.start();


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
            counter = 0;
            showcaseView = new ShowcaseView.Builder(this)
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setTarget(new ViewTarget(findViewById(R.id.InterpreterBtn)))
                            // .setShowcaseDrawer(new CustomShowcaseViewAlone(getResources()))
                    .setContentTitle("Dette er din dagbogsknap")
                    .setContentText("Denne skal besvares en gang om dagen. Tryk på ikonet for at starte dagbogen")
                    .setOnClickListener(showCaseHandler)
                    .build();
            showcaseView.setButtonText("Næste");

        }

        return super.onOptionsItemSelected(item);
    }


    //Methode for creating diary buttons + events buttons/switchs
    public void setLayout() {


        //SAVE DATE IN SHAREDPREF WHEN DIARY IS RUN. WHEN THIS ACTIVITY RUNS CHECK SP, DEFAULT RETURN VALUE SHOULD BE THIS DATE = MEANING NO DIARY HAS EVER BEEN RUN!

        timeEventsHolder.removeAllViews();

        int margin = 0;
        incidentTextview.setText("Ingen aktive hændelser...");


        Log.d("HEJ FRA LAYOUT", "HEJ HEJ");
        for (int j = 0; j < allEvents.size(); j++) {

            //NEW IMPLEMENTATION
            boolean localstatebtn = pref.getBoolean("state" + allEvents.get(j).getEventID(), false);
            if (localstatebtn) {
                Button switchBtn = new Button(this);
                LinearLayout.LayoutParams btnparam = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                btnparam.setMargins(margin, 60, 0, 0);
                switchBtn.setLayoutParams(btnparam);
                switchBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
                String time = pref.getString("eventtime" + allEvents.get(j).getEventID(), "00:00:00");
                switchBtn.setText("" + allEvents.get(j).getEventName() + "\n" + time);
                switchBtn.setTag(allEvents.get(j).getEventType() + "," + allEvents.get(j).getEventID() + "," + allEvents.get(j).getEventName());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    switchBtn.setBackground(null);
                }
//                Animation mAnimation = new AlphaAnimation(1, 0);
//                mAnimation.setDuration(100);
//                mAnimation.setInterpolator(new LinearInterpolator());
//                mAnimation.setRepeatCount(Animation.INFINITE);
//                mAnimation.setRepeatMode(Animation.REVERSE);

                switchBtn.startAnimation(myAnim);


                switchBtn.setTextColor(Color.WHITE);
                switchBtn.setOnClickListener(switchEventHandler);
                timeEventsHolder.addView(switchBtn);
                incidentTextview.setText("Aktive hændelser");
                margin = 50;
            }
            //NEW IMPLEMENTATION

        }
    }

    //Methode for parsin Json Array with Questions
    public void parseJsonQuestions() {

        JSONArray jsonArrayQuestions;

        try {
            jsonArrayQuestions = new JSONArray(jsonQuestionStringData);
            for (int i = 0; i < jsonArrayQuestions.length(); i++) {


                JSONObject jsonProductObject = jsonArrayQuestions.getJSONObject(i);
                int questionID = jsonProductObject.getInt("questionID");
                int visible = jsonProductObject.getInt("visible");
                int operation = jsonProductObject.getInt("operation");
                int qcondition = jsonProductObject.getInt("qcondition");
                int questionGrp = jsonProductObject.getInt("questionGrp");
                int type = jsonProductObject.getInt("type");
                String question = jsonProductObject.getString("question");
                String possibleAnswer = jsonProductObject.getString("possibleAnswer");
                allquestions.add(new JsonHolder(questionID, visible, operation, qcondition, questionGrp, type, question, possibleAnswer));
            }

            Log.d("ArrayQuestions", "" + jsonArrayQuestions);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        PersonInfo.setAllquestions(allquestions);

    }

    //Methode for parsin Json Array with events
    public void parseJsonEvents() {

        JSONArray jsonArrayEvents;
        try {
            jsonArrayEvents = new JSONArray(jsonEventsStringData);
            for (int i = 0; i < jsonArrayEvents.length(); i++) {

                JSONObject jsonProductObject = jsonArrayEvents.getJSONObject(i);
                String eventID = jsonProductObject.getString("eventID");
                int eventType = jsonProductObject.getInt("eventType");
                String eventName = jsonProductObject.getString("eventName");
                allEvents.add(new EventHolder(eventID, eventType, eventName));

            }

            Log.d("ArrayEvents", "" + jsonArrayEvents);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        PersonInfo.setAllevents(allEvents);

    }


    View.OnClickListener showCaseHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (counter) {
                case 0:
                    showcaseView.setShowcase(new ViewTarget(openEventButton), true);
                    showcaseView.setContentTitle("Dette er dine hændelser");
                    showcaseView.setContentText("Her kan du aktivere hændelser der opstår i løbet af din dag. Nogle af disse hændelser skal deaktiveres igen når hændelsen er slut");

                    break;

                case 1:
                    showcaseView.setShowcase(new ViewTarget(incidentTextview), true);
                    showcaseView.setContentTitle("Dette er dine aktive hændelser");
                    showcaseView.setContentText("Her kan du se hvilke hændelse der er aktive lige nu");
                    showcaseView.setButtonText("Afslut");

                    break;

                case 2:
                    showcaseView.hide();

                    break;
            }
            counter++;

        }
    };


    View.OnClickListener normalEventHandler = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Button universalbutton = (Button) v;

            String[] eventInfo = universalbutton.getTag().toString().split(",");
            String eventType = eventInfo[0];
            String eventID = eventInfo[1];
            PersonInfo.setQuestionGrp(Integer.parseInt(eventID));
            PersonInfo.setTrigger(Integer.parseInt(eventType));
            Log.d("questionGRP is now ", "" + PersonInfo.getQuestionGrp());

            activateIntepreter();
        }
    };

    View.OnClickListener switchEventHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //HUSK SWITCH TILSTAND SKAL SLETTES HVIS NY LOGGER IND
            //ALT I SP SKAL SLETTES VED NY BRUGER

            Button universalSwitch = (Button) v;
            String[] eventInfo = universalSwitch.getTag().toString().split(",");
            String eventType = eventInfo[0];
            String eventID = eventInfo[1];
            String eventName = eventInfo[2];
            universalSwitch.clearAnimation();

            //true: btn = active .... false: btn = not active. Default false
            //boolean btnState = pref.getBoolean("state" + eventID, false);

                Log.d("Knappen er nu ", "evenID "+eventID+" eventName " +eventName);
                universalSwitch.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);

                editor.putLong("endtime" + eventID, +System.currentTimeMillis());
                editor.commit();

                eventEnded(eventID,eventName);



        }
    };

    public void eventEnded(final String eventID, String ename) {


        final long endTime = pref.getLong("endtime" + eventID, 0);
        Log.d("endtime"," "+endTime);
        final long startTime = pref.getLong("starttime" + eventID, 0);
        Log.d("starttime"," "+startTime);
        long result = (endTime - startTime);
        final String hms = String.format("%02d timer, %02d minutter, og %02d sekunder",
                TimeUnit.MILLISECONDS.toHours(result),
                TimeUnit.MILLISECONDS.toMinutes(result) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(result)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(result) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(result)));


        final int session = pref.getInt("session" + eventID, -1);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Er hændelsen "+ename+" slut?")
                .setMessage("Tid:\n" + hms + "\nBekræft, ændr afslutningstiden eller annuller")
                .setCancelable(false)

                //YES BUTTON
                .setPositiveButton("Bekræft", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        editor.putBoolean("state" + eventID, false);
                        editor.commit();
                        setLayout();
                        uploadTimeevent(eventID, session);


                    }
                })
                // NO BUTTON
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        editor.putBoolean("state" + eventID, true);
                        editor.commit();
                        setLayout();

                    }
                })
                //CHANGE TIME BUTTON
                .setNeutralButton("Ændr tid", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        listener.gather(eventID, session);

                        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                                .setListener(listener)
                                .setInitialDate(new Date())
                                .setIs24HourTime(true)
                                .build()
                                .show();

                    }
                });

        builder.create().show();



    }

        private SlideDateTimeListener listener = new SlideDateTimeListener() {

            String eid;
            int session;

        @Override
        public void gather(String eid,int session){
            this.eid = eid;
            this.session = session;
        }

        @Override
        public void onDateTimeSet(Date date)
        {
            long tiden = date.getTime();
            editor.putLong("endtime" + eid, +tiden);
            editor.putBoolean("state" + eid, false);
            editor.commit();
            setLayout();
            uploadTimeevent(eid,session);

        }

        @Override
        public void onDateTimeCancel()
        {
            editor.putBoolean("state" + eid, true);
            editor.commit();
            setLayout();
        }
    };



    public void uploadTimeevent(String eventID, int session){

        final long endTime = pref.getLong("endtime" + eventID, 0);
        Log.d("endtime"," "+endTime);
        final long startTime = pref.getLong("starttime" + eventID, 0);
        Log.d("starttime"," "+startTime);
        long result = (endTime - startTime);
        final String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(result),
                TimeUnit.MILLISECONDS.toMinutes(result) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(result)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(result) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(result)));

        Toast.makeText(this, "Hændelsen er nu slut og varede i alt \n" + hms + "\nSvaret uploades automatisk - tak", Toast.LENGTH_LONG).show();
        
        db.updateDuration(hms, session);

    }




    @Override
    public void onClick(View v) {

        if (v==launchInterpreterBtn){

            boolean state = pref.getBoolean("state",false);
            if (state == false) {
                //The diary always have questionGrp = 0;
                PersonInfo.setTrigger(0);
                PersonInfo.setQuestionGrp(0);
                activateIntepreter();

            }
            else {
                  Toast.makeText(this, "Du har allerede svaret for i dag", Toast.LENGTH_SHORT).show();
            }

        }

        if (v == openEventButton){

            openEvents();
        }

    }



    public void activateIntepreter(){

        Intent launchInterpreter = new Intent(this,InterpreterActivity.class);
        //launchInterpreter.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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

            //getting all events attachted to this diaryID
            String resultEvents = getEvents();
            //getting all questions attachted to this diaryID
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
                Log.d("ØVVVVVV!!","nej nej nej nej nej");
        }
    }


    public String getQuestions(){

    Log.d("getquestion","jeg er aktiveret");
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

                Log.d("connection events!", "forbindelse etableret "+PersonInfo.getDiaryID());

                // Read data sent from server
                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();

                String line=null;


                while ((line = reader.readLine()) != null) {
                    result.append(line);
                    Log.d("eventsdb", "" + line);

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


    @Override
    public void onBackPressed() {
        exitByBackKey();
    }


    protected void exitByBackKey() {

        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Vil du afslutte?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent homeIntent= new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory(Intent.CATEGORY_HOME);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
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

    public void openEvents(){

        ArrayList<String> eventNames = new ArrayList<>();
        final ArrayList<String> eventID = new ArrayList<>();
        final ArrayList<Integer> eventType = new ArrayList<>();

        for (int b = 0;b<allEvents.size();b++){

            eventNames.add(allEvents.get(b).getEventName());
            eventID.add(allEvents.get(b).getEventID());
            eventType.add(allEvents.get(b).getEventType());
        }


        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.customlist, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Dine mulige hændelser");
        final ListView lv = (ListView) convertView.findViewById(R.id.listView1);
        final AlertDialog dlg = alertDialog.create();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,eventNames);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                //Object o = lv.getItemAtPosition(position);
                boolean local = pref.getBoolean("state"+eventID.get(position),false);
                if (local){
                    Toast.makeText(MainUserActivity.this, "Denne hændelse er allerede aktiv", Toast.LENGTH_SHORT).show();

                }
                else if (!local) {
                    Log.d("Id er", " " + eventID.get(position));
                    eventHandler(eventID.get(position), eventType.get(position));
                    dlg.dismiss();
                }

            }
        });
       dlg.show();


    }

    public void eventHandler(String eventID,int eventtype){



        PersonInfo.setTrigger(eventtype);
        PersonInfo.setQuestionGrp(Integer.parseInt(eventID));

        switch (eventtype) {
            case 0:
                activateIntepreter();
                break;
            case 1:
                String time = sdf.format(new Date().getTime());
                editor.putBoolean("state" + eventID, true);
                editor.putLong("starttime" + eventID, +System.currentTimeMillis());
                editor.putString("eventtime" + eventID, time);
                editor.commit();
                activateIntepreter();
                setLayout();

        }



    }





    }
