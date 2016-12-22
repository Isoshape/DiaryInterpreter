package com.example.bigmac.diaryinterpreter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private EditText etPassword,etPassword1,etPassword2,etPassword3;
    private LinearLayout ll;
    private Button universalbutton;
    private String alertUsername;
    private String alterUsernameSave;

    private Button userBtn;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private int focusIndex;
    ArrayList<EditText> btnArray = new ArrayList<>();

    Vibrator viber ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viber = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);


        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();

        userBtn = (Button) findViewById(R.id.changeusernameBtn);
        userBtn.setOnClickListener(this);

        //See if any usename is stored
        alterUsernameSave = pref.getString("brugernavn", null);


        //if first time user, promp dialog box asking for username. The methode saves the username in sharedprefferences
        if (alterUsernameSave==null) {
            int firstcase = 1;
            alerMethod(firstcase);
        }
        // Get Reference to variables

        userBtn.setText(alterUsernameSave);
        etPassword = (EditText) findViewById(R.id.password);
        etPassword1 = (EditText) findViewById(R.id.password1);
        etPassword2 = (EditText) findViewById(R.id.password2);
        etPassword3 = (EditText) findViewById(R.id.password3);

        etPassword.addTextChangedListener(new onTextChangeListner());
        etPassword1.addTextChangedListener(new onTextChangeListner());
        etPassword2.addTextChangedListener(new onTextChangeListner());
        etPassword3.addTextChangedListener(new onTextChangeListner());

        etPassword.setOnFocusChangeListener(focusChangeHandler);
        etPassword1.setOnFocusChangeListener(focusChangeHandler);
        etPassword2.setOnFocusChangeListener(focusChangeHandler);
        etPassword3.setOnFocusChangeListener(focusChangeHandler);

        etPassword.requestFocus();

        etPassword.setShowSoftInputOnFocus(false);
        etPassword1.setShowSoftInputOnFocus(false);
        etPassword2.setShowSoftInputOnFocus(false);
        etPassword3.setShowSoftInputOnFocus(false);


        btnArray.add(etPassword);
        btnArray.add(etPassword1);
        btnArray.add(etPassword2);
        btnArray.add(etPassword3);




        ll = (LinearLayout) findViewById(R.id.numberHolder);

        for (int i = 0; i < 3; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));


            for (int a = 0; a < 3; a++) {

                Button numbersBtn = new Button(this);
                numbersBtn.setBackground(null);
                numbersBtn.setTextSize(30);
                //numbersBtn.setBackground(getResources().getDrawable(R.drawable.btntest));
                numbersBtn.setTextColor(Color.WHITE);
                Log.d("hvad er a nu", "" + a);

                numbersBtn.setText("" + (a + 1 + (i * 3)));
                numbersBtn.setOnClickListener(this);
                numbersBtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));

                row.addView(numbersBtn);


            }
            ll.addView(row);
        }//end rows

    }
    //dialog methode asking for username
    public void alerMethod(int mycase){
        String message = "";
        if (mycase>0){
            message="Da det er første gang du logger på skal du indtaste dit brugernavn";
        }

        if (mycase==0){
            message="Du kan nu ændre dit brugernavn";
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Dit brugernavn");
        alert.setMessage(message);

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setSingleLine(true);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                alertUsername = input.getText().toString();
                userBtn.setText(alertUsername);
                editor.putString("brugernavn", alertUsername);
                editor.commit();
            }
        });
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        alert.show();

    }


    View.OnFocusChangeListener focusChangeHandler = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(v == etPassword && hasFocus){
                etPassword.setText("");
                focusIndex = 0;
            }
            else if(v == etPassword1 && hasFocus){
                etPassword1.setText("");
                focusIndex = 1;
            }
            else if(v == etPassword2 && hasFocus){
                etPassword2.setText("");
                focusIndex = 2;
            }
            else if(v == etPassword3 && hasFocus){
                etPassword3.setText("");
                focusIndex = 3;
            }
        }
    };

    private void changeInputFocus(boolean right) {
        int key = focusIndex;
        if (key < 3 && right) {
            key++;
        } else if (key > 0 && !right) {
            key--;
        } else {
            key = 0;
            checkLogin();
        }
        setFocus(key);
    }

    private void setFocus(int key){

        Log.d("hvad er focus her",""+key);
        switch (key){
            case 0:
                etPassword.requestFocus();
                focusIndex = 0;
                break;
            case 1:
                etPassword1.requestFocus();
                focusIndex = 1;
                break;
            case 2:
                etPassword2.requestFocus();
                focusIndex = 2;
                break;
            case 3:
                etPassword3.requestFocus();
                focusIndex = 3;
                break;
            default:
                break;
        }
    }


    //Buttons listner
    @Override
    public void onClick(View v) {

        if (v==userBtn){
            pref.edit().clear().commit();
            int changecase = 0;
            alerMethod(changecase);

        }

        universalbutton = (Button) v;

        Log.d("Du klikkede",""+universalbutton.getText().toString());


        String letterpressed = universalbutton.getText().toString();
        if (letterpressed.equals(""+1)||
                letterpressed.equals(""+2)||
                letterpressed.equals(""+3)||
                letterpressed.equals(""+4)||
                letterpressed.equals(""+5)||
                letterpressed.equals(""+6)||
                letterpressed.equals(""+7)||
                letterpressed.equals(""+8)||
                letterpressed.equals(""+9)) {
            viber.vibrate(50);
            btnArray.get(focusIndex).setText(""+letterpressed);
        }

    }



    // Triggers when password is entered
    public void checkLogin() {

        // Get text from usernameBtn and passord field
        final String finaluser = userBtn.getText().toString();
        final String password = etPassword.getText().toString()+etPassword1.getText().toString()+etPassword2.getText().toString()+etPassword3.getText().toString();

        for (int clear=0;clear< btnArray.size();clear++){
            btnArray.get(clear).setText("");
        }

        // Initialize  AsyncLogin() class with username and password
        new AsyncLogin().execute(finaluser, password);

    }




    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tKontrollere brugernavn og pinkode");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides

                url = new URL("http://10.0.2.2/login.inc.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("password", params[1]);
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

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread
            String[] values;
            String firstname;
            int uuid;
            int diaryID;
            String logourl;


            pdLoading.dismiss();

            Log.d("finalresult",""+result);
            if(result != null && !result.equalsIgnoreCase("false") && !result.equalsIgnoreCase("unsuccessful") && !result.equalsIgnoreCase("exception") )
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */

                values = result.split(" ");
                firstname = values[0];
                uuid = Integer.parseInt(values[1]);
                diaryID = Integer.parseInt(values[2]);
                logourl = values[3];


                PersonInfo.setFirstName(firstname);
                PersonInfo.setUserID(uuid);
                PersonInfo.setDiaryID(diaryID);
                PersonInfo.setLogourl(logourl);

                Toast.makeText(MainActivity.this, "Velkommen! "+firstname+" Dit id er :"+uuid +" "+diaryID, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,MainUserActivity.class);
                startActivity(intent);
                MainActivity.this.finish();

            }else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(MainActivity.this, "Brugernavn eller adgangskode forkert", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(MainActivity.this, "Ingen forbindelse til serveren - prøv igen senere", Toast.LENGTH_LONG).show();

            }
        }

    }

    private boolean checkNumberInput(String input){
        if(input.matches("\\d")) return true;
        return false;
    }


    private class onTextChangeListner implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {


        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {


        }

        @Override
        public void afterTextChanged(Editable s) {
            if(checkNumberInput(s.toString())){
                changeInputFocus(true);
            }


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
