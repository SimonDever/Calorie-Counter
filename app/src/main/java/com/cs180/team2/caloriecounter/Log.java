package com.cs180.team2.caloriecounter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import static com.cs180.team2.caloriecounter.DailyCalories.cal_limit;
import static com.cs180.team2.caloriecounter.LoginActivity.username;

public class Log extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        String root = Environment.getExternalStorageDirectory().toString();
        File dir = new File(root + "/daily_logs"); //FILE DIRECTORY
        if(!dir.exists()) {
            dir.mkdirs();
        }

        Calendar c = Calendar.getInstance();
        String Filename = Integer.toString(c.get(Calendar.MONTH)) + "." + Integer.toString(c.get(Calendar.DAY_OF_MONTH)) + "." + Integer.toString(c.get(Calendar.YEAR));
        Filename = Filename + "_" + username + ".txt";
        File file = new File(dir, Filename);
        if(!file.getParentFile().exists()) {
            if(file.getParentFile().mkdirs()) {
                System.out.println(file.getParentFile() + " created!");
            }

            try {
                System.out.println(file.getCanonicalPath() + " about to be created!");
            } catch(IOException e) {
                e.printStackTrace();
            }
        }


        ArrayList<FoodEntry> breakfast = new ArrayList<FoodEntry>();
        ArrayList<FoodEntry> lunch = new ArrayList<FoodEntry>();
        ArrayList<FoodEntry> dinner = new ArrayList<FoodEntry>();
        ArrayList<FoodEntry> snacks = new ArrayList<FoodEntry>();
        TextView log = (TextView) findViewById(R.id.log);

        if(username.isEmpty()) {
            log.setVisibility(View.INVISIBLE);
        } else {
            if (file.exists()) {
                try {
                    InputStream in = new FileInputStream(file);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));


                    String line = reader.readLine();
                    while(line != null) {
                        FoodEntry tempFood = new FoodEntry("", Long.valueOf(0), "", "", "");
                        tempFood.Name = line;
                        tempFood.Calories = Long.valueOf(reader.readLine());
                        tempFood.Description = reader.readLine();
                        tempFood.Tag = reader.readLine();
                        tempFood.User = reader.readLine();
                        tempFood.Tag = reader.readLine();
                        line = reader.readLine();
                        if(tempFood.Tag.equals("Breakfast")) {
                            breakfast.add(tempFood);
                        }
                        else if(tempFood.Tag.equals("Lunch")) {
                            lunch.add(tempFood);
                        }
                        else if(tempFood.Tag.equals("Dinner")) {
                            dinner.add(tempFood);
                        }
                        else if(tempFood.Tag.equals("Snacks")) {
                            snacks.add(tempFood);
                        }
                    }
                    Long breakfasttotal = Long.valueOf(0);
                    Long lunchtotal = Long.valueOf(0);
                    Long dinnertotal = Long.valueOf(0);
                    Long snackstotal = Long.valueOf(0);
                    Long grandtotal = Long.valueOf(0);
                    log.setText("Breakfast\n\n");
                    for(int i = 0; i < breakfast.size(); i++) {
                        log.append("Name: " + breakfast.get(i).Name + "\n");
                        log.append("Calories: " + breakfast.get(i).Calories.toString() + "\n");
                        log.append("Description: " + breakfast.get(i).Description + "\n\n");
                        breakfasttotal = breakfasttotal + breakfast.get(i).Calories;
                    }
                    log.append("Total Calories for Breakfast: " + breakfasttotal.toString() + "\n\n");
                    if(breakfast.size() == 0) {
                        log.append("Nothing in breakfast!\n");
                    }

                    log.append("Lunch\n\n");
                    for(int i = 0; i < lunch.size(); i++) {
                        log.append("Name: " + lunch.get(i).Name + "\n");
                        log.append("Calories: " + lunch.get(i).Calories.toString() + "\n");
                        log.append("Description: " + lunch.get(i).Description + "\n\n");
                        lunchtotal = lunchtotal + lunch.get(i).Calories;
                    }
                    log.append("Total Calories for Lunch: " + lunchtotal.toString() + "\n\n");
                    if(breakfast.size() == 0) {
                        log.append("Nothing in lunch!\n");
                    }

                    log.append("Dinner\n\n");
                    for(int i = 0; i < dinner.size(); i++) {
                        log.append("Name: " + dinner.get(i).Name + "\n");
                        log.append("Calories: " + dinner.get(i).Calories.toString() + "\n");
                        log.append("Description: " + dinner.get(i).Description + "\n\n");
                        dinnertotal = dinnertotal + dinner.get(i).Calories;
                    }
                    log.append("Total Calories for Dinner: " + dinnertotal.toString() + "\n\n");
                    if(breakfast.size() == 0) {
                        log.append("Nothing in dinner!\n");
                    }

                    log.append("Snacks\n\n");
                    for(int i = 0; i < snacks.size(); i++) {
                        log.append("Name: " + snacks.get(i).Name + "\n");
                        log.append("Calories: " + snacks.get(i).Calories.toString() + "\n");
                        log.append("Description: " + snacks.get(i).Description + "\n\n");
                        snackstotal = snackstotal + snacks.get(i).Calories;
                    }
                    log.append("Total Calories for Snacks: " + snackstotal.toString() + "\n\n");
                    if(breakfast.size() == 0) {
                        log.append("Nothing in snacks!\n");
                    }
                    grandtotal = breakfasttotal + lunchtotal + dinnertotal + snackstotal;
                    log.append("Total Calories consumed today: " + grandtotal.toString());

                    if(grandtotal >= Long.parseLong(cal_limit)) {
                        Long diff = grandtotal - Long.parseLong(cal_limit);

                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.priceisrighthorn);
                        mp.start();

                        AlertDialog.Builder builder = new AlertDialog.Builder(Log.this); //Chanho: Dialogs are popup notifications that require users to interact with to get rid of.
                        builder.setMessage("Warning: You are " + diff.toString() + " calories over your limit!" ); //This dialog asks the user if they want to
                        builder.setPositiveButton("Whoops", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                    else if((Long.parseLong(cal_limit) - grandtotal) <= 200) {
                        Long diff = Long.parseLong(cal_limit) - grandtotal;

                        AlertDialog.Builder builder = new AlertDialog.Builder(Log.this); //Chanho: Dialogs are popup notifications that require users to interact with to get rid of.
                        builder.setMessage("Warning: You are only " + diff.toString() + " calories from going over your limit!" ); //This dialog asks the user if they want to
                        builder.setPositiveButton("Whatever, man", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do nothing i guess
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                } catch (FileNotFoundException e) {
                    Context context = getApplicationContext();
                    CharSequence text = "Log file not found!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration); //Chanho: Toast is a popup notification that disappears automatically after a period of time
                    toast.show();
                } catch (IOException e) {
                    Context context = getApplicationContext();
                    CharSequence text = "Log file not found!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration); //Chanho: Toast is a popup notification that disappears automatically after a period of time
                    toast.show();
                }
            }
            else {
                log.setText("Log file not found!");
            }
        }
    }
}
