package com.cs180.team2.caloriecounter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.cs180.team2.caloriecounter.DailyCalories.choice;
import static com.cs180.team2.caloriecounter.R.id.addcustomfoodbutton;
import static com.cs180.team2.caloriecounter.R.id.textView2;
import static java.security.AccessController.getContext;


import static com.cs180.team2.caloriecounter.LoginActivity.username;

public class AddEntry extends AppCompatActivity {

    private DatabaseReference myRef;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        Button customFood = (Button) findViewById(R.id.addcustomfoodbutton);
        if(username.isEmpty())   //if guest user, don't allow them to add custom food
        {
            customFood.setVisibility(View.INVISIBLE);
        }
        else
            customFood.setVisibility(View.VISIBLE);
    }




    public void searchDatabase(View view) {

        EditText inputTxt = (EditText) findViewById(R.id.text);
        final String str = inputTxt.getText().toString().trim().toLowerCase();

        final DatabaseReference mFoodRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kaloriekounterk.firebaseio.com/Food");
        mFoodRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String tag = "";
                String Foodname = "";
                String User = "";
                String FoodDescription = "";
                Long FoodCalories;

                ArrayList<FoodEntry> results = new ArrayList<FoodEntry>();
                int matches = 0;

                for (DataSnapshot foodSnapshot: dataSnapshot.getChildren()) {
                    tag = (String)foodSnapshot.child("Tag").getValue();
                    Foodname = foodSnapshot.getKey();
                    if (tag.equals(str) || Foodname.toLowerCase().equals(str)) {
                        User = foodSnapshot.child("User").getValue(String.class);
                        FoodCalories = foodSnapshot.child("Calories").getValue(Long.class);
                        FoodDescription = foodSnapshot.child("Description").getValue(String.class);
                        FoodEntry foodresult = new FoodEntry(Foodname, FoodCalories, FoodDescription, tag, User);
                        //results += tag + "\n";
                        results.add(foodresult);
                        matches++;
                    }
                }
                if (matches == 0) {
                    Context context = getApplicationContext();
                    CharSequence text = "No matches found!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {

                    final FoodEntryAdapter adapter = new FoodEntryAdapter(AddEntry.this, results);


                    ListView textView2 = (ListView) findViewById(R.id.textView2);
                    textView2.setAdapter(adapter);

                    textView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {  // LOG FILE WRITING STARTS HERE
                            if (username.isEmpty()) {
                                Context context = getApplicationContext();
                                CharSequence text = "Please login/register to track food and calories!";
                                int duration = Toast.LENGTH_SHORT;

                                Toast toast = Toast.makeText(context, text, duration); //Chanho: Toast is a popup notification that disappears automatically after a period of time
                                toast.show();
                            } else {
                                final FoodEntry item = adapter.getItem(i);
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddEntry.this); //Chanho: Dialogs are popup notifications that require users to interact with to get rid of.
                                builder.setMessage("Add " + item.Name + " to daily log?"); //This dialog asks the user if they want to register a new user


                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String root = Environment.getExternalStorageDirectory().toString();
                                        File dir = new File(root + "/daily_logs"); //FILE DIRECTORY


                                        Calendar c = Calendar.getInstance();
                                        String Filename = Integer.toString(c.get(Calendar.MONTH)) + "." + Integer.toString(c.get(Calendar.DAY_OF_MONTH)) + "." + Integer.toString(c.get(Calendar.YEAR));
                                        Filename = Filename + "_" + username + ".txt"; //FILENAME: MM.DD.YYYY_USERNAME.txt
                                        File file = new File(dir, Filename);
                                        if (!file.getParentFile().exists()) {
                                            if (file.getParentFile().mkdirs()) {
                                                System.out.println(file.getParentFile() + " created!");
                                            }

                                            try {
                                                System.out.println(file.getCanonicalPath() + " about to be created!");
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        if (!file.exists()) { // ALL ENTRIES NEED ALL CHILDREN OR ELSE APP CRASHES
                                            try {
                                                file.createNewFile();
                                                FileOutputStream outputStream = new FileOutputStream(file, true);
                                                outputStream.write(item.Name.getBytes());
                                                outputStream.write("\n".getBytes());
                                                outputStream.write(item.Calories.toString().getBytes());
                                                outputStream.write("\n".getBytes());
                                                outputStream.write(item.Description.getBytes());
                                                outputStream.write("\n".getBytes());
                                                outputStream.write(item.Tag.getBytes());
                                                outputStream.write("\n".getBytes());
                                                outputStream.write(item.User.getBytes());
                                                outputStream.write("\n".getBytes());
                                                outputStream.write(choice.getBytes());
                                                outputStream.write("\n".getBytes());
                                                outputStream.close();
                                                Context context = getApplicationContext();
                                                CharSequence text = item.Name + " added to " + choice + " log!";
                                                int duration = Toast.LENGTH_SHORT;

                                                Toast toast = Toast.makeText(context, text, duration); //Chanho: Toast is a popup notification that disappears automatically after a period of time
                                                toast.show();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            try {
                                                FileOutputStream outputStream = new FileOutputStream(file, true);
                                                outputStream.write(item.Name.getBytes());
                                                outputStream.write("\n".getBytes());
                                                outputStream.write(item.Calories.toString().getBytes());
                                                outputStream.write("\n".getBytes());
                                                outputStream.write(item.Description.getBytes());
                                                outputStream.write("\n".getBytes());
                                                outputStream.write(item.Tag.getBytes());
                                                outputStream.write("\n".getBytes());
                                                outputStream.write(item.User.getBytes());
                                                outputStream.write("\n".getBytes());
                                                outputStream.write(choice.getBytes());
                                                outputStream.write("\n".getBytes());
                                                outputStream.close();
                                                Context context = getApplicationContext();
                                                CharSequence text = item.Name + " added to " + choice + " log!";
                                                int duration = Toast.LENGTH_SHORT;

                                                Toast toast = Toast.makeText(context, text, duration); //Chanho: Toast is a popup notification that disappears automatically after a period of time
                                                toast.show();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                    }
                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();

                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });

        /*final DatabaseReference mSearchedFoodRef = mFoodRef.child(str);


        mSearchedFoodRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String Foodname = mSearchedFoodRef.getKey();
                Long FoodCalories = dataSnapshot.child("Calories").getValue(Long.class);
                String FoodDescription = dataSnapshot.child("Description").getValue(String.class);
                TextView textView2 = (TextView) findViewById(R.id.textView2);
                //textView2.setText("Name: " + Foodname + "\nCalories: " + FoodCalories + "\nDescription: " + FoodDescription);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });*/

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Add Entry") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
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

    public void addFood(View view) {
        Intent intent = new Intent(this, AddFood.class);
        startActivity(intent);

    }
}
