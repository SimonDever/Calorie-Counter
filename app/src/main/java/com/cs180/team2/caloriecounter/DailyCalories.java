package com.cs180.team2.caloriecounter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Stack;

import static com.cs180.team2.caloriecounter.LoginActivity.username;
import static com.cs180.team2.caloriecounter.R.id.textView7;



public class DailyCalories extends AppCompatActivity {
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public static String choice = "";
    public static String cal_limit = ""; //CALORIE LIMIT GLOBAL STRING

    public static ArrayList<File> getAllFilesInDir(File dir) { //GET ALL FILES IN DIRECTORY FUNCTION
        if (dir == null)
            return null;

        ArrayList<File> files = new ArrayList<File>();

        Stack<File> dirlist = new Stack<File>();
        dirlist.clear();
        dirlist.push(dir);

        while (!dirlist.isEmpty()) {
            File dirCurrent = dirlist.pop();

            File[] fileList = dirCurrent.listFiles();
            File aFile = new File("");
            for (int i = 0; i < fileList.length; i++) {
                aFile = fileList[i];
                if (aFile.isDirectory())
                    dirlist.push(aFile);
                else
                    files.add(aFile);
            }
        }

        return files;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_calories);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        Intent intent = getIntent();
        String message = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);


        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_daily_calories);
        layout.addView(textView);

        String root = Environment.getExternalStorageDirectory().toString(); //ROOT DIRECTORY

        File goaldir = new File(root + "/calorie_goal");  //START CALORIE GOAL FILE CREATION/READ FOR USER'S CALORIE GOAL
        if(!goaldir.exists()) {
            goaldir.mkdirs();
        }
        String goalfilename = username + ".txt";
        File goalfile = new File(goaldir, goalfilename);
        if(!goalfile.exists()) { //MAKE USER GOAL FILE IF IT DOESN'T EXIST
            try {
                goalfile.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(goalfile);
                outputStream.write("2000".getBytes());
                cal_limit = "2000";
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try { //ELSE READ EXISTING GOAL AND MAKE IT THE GLOBAL GOAL VARIABLE
                InputStream in = new FileInputStream(goalfile);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                cal_limit = reader.readLine();
            } catch (FileNotFoundException e) {
                Context context = getApplicationContext();
                CharSequence text = "Calorie limit file not found!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration); //Chanho: Toast is a popup notification that disappears automatically after a period of time
                toast.show();
            } catch (IOException e) {
                Context context = getApplicationContext();
                CharSequence text = "Calorie limit file not found!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration); //Chanho: Toast is a popup notification that disappears automatically after a period of time
                toast.show();
            }
        }


        File dir = new File(root + "/daily_logs"); //DAILY LOG FILE DIRECTORY
        if(!dir.exists()) {
            dir.mkdirs();
        }
        if(dir.listFiles() != null) {
            Calendar time = Calendar.getInstance(); // Clean up log files older than a week
            time.add(Calendar.DAY_OF_YEAR, -7);
            ArrayList<File> filelist = getAllFilesInDir(dir);
            for (File fileentry : filelist) {
                Date lastModified = new Date(fileentry.lastModified());
                if (lastModified.before(time.getTime())) {
                    fileentry.delete();
                }
            }
        }


        Calendar c = Calendar.getInstance(); //create log file for current day if it doesn't exist already
        String Filename = Integer.toString(c.get(Calendar.MONTH)) + "." + Integer.toString(c.get(Calendar.DAY_OF_MONTH)) + "." + Integer.toString(c.get(Calendar.YEAR));
        Filename = Filename + "_" + username + ".txt";
        File file = new File(dir, Filename);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }




        String currentDateString = DateFormat.getDateInstance().format(new Date());  //OUTPUT CURRENT DATE
        TextView textView6 = (TextView) findViewById(R.id.textView6);
        textView6.setText(currentDateString);

        String usrnme = "Welcome back, " + username.toString() + "!"; //GREET USER
        TextView textViewUserName = (TextView) findViewById(textView7);

        Button mChangePassword = (Button) findViewById(R.id.change_password_button);
        Button statButton = (Button) findViewById(R.id.statGraphButton);

        Button logButton = (Button) findViewById(R.id.button8);

        boolean isGuest = false;

        if (!username.isEmpty())
        {
            textViewUserName.setText(usrnme);
            mChangePassword.setVisibility(View.VISIBLE);
            statButton.setVisibility(View.VISIBLE);
        }
        else
        {
            textViewUserName.setText("Guest");      //sign in as guest user
            isGuest = true;
        }

        if (isGuest)     //if sign in as guest, don't show change password
        {
            mChangePassword.setVisibility(View.INVISIBLE);
            logButton.setVisibility(View.INVISIBLE);
            statButton.setVisibility(View.INVISIBLE);

        }
        //View mChangePasswordView;

        mChangePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changePassword();
                }
            });



            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Daily Calories Page")
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("https://kaloriekounterk.firebaseio.com/"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public void addEntryBreakfast(View view) {
        choice = "Breakfast";
        Intent intent = new Intent(this, AddEntry.class);
        startActivity(intent);

    }

    public void addEntryLunch(View view) {
        choice = "Lunch";
        Intent intent = new Intent(this, AddEntry.class);
        startActivity(intent);

    }

    public void addEntryDinner(View view) {
        choice = "Dinner";
        Intent intent = new Intent(this, AddEntry.class);
        startActivity(intent);

    }

    public void addEntrySnacks(View view) {
        choice = "Snacks";
        Intent intent = new Intent(this, AddEntry.class);
        startActivity(intent);

    }

    public void logOut(View view)
    {
        Context context = getApplicationContext();
        CharSequence text = "Logging out...";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration); //Chanho: Toast is a popup notification that disappears automatically after a period of time
        toast.show();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void changePassword(){
      Intent intent = new Intent(this, ChangePasswordActivity.class);
      startActivity(intent);
   }

    public void viewStatsGraph(View view)
    {
        Intent intent = new Intent(this, Graph.class );
        startActivity(intent);
    }

    public void startLog(View view) {
        Intent intent = new Intent(this, Log.class);
        startActivity(intent);
    }

}
