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
        TextView breakfast_log = (TextView) findViewById(R.id.breakfast_log);
        TextView lunch_log = (TextView) findViewById(R.id.lunch_log);
        TextView dinner_log = (TextView) findViewById(R.id.dinner_log);
        TextView snacks_log = (TextView) findViewById(R.id.snacks_log);
        TextView breakfast_title = (TextView) findViewById(R.id.breakfast_title);
        TextView lunch_title = (TextView) findViewById(R.id.lunch_title);
        TextView dinner_title = (TextView) findViewById(R.id.dinner_title);
        TextView snacks_title = (TextView) findViewById(R.id.snacks_title);

        if(username.isEmpty()) {
            breakfast_log.setVisibility(View.INVISIBLE);
            lunch_log.setVisibility(View.INVISIBLE);
            dinner_log.setVisibility(View.INVISIBLE);
            snacks_log.setVisibility(View.INVISIBLE);
            lunch_title.setVisibility(View.INVISIBLE);
            dinner_title.setVisibility(View.INVISIBLE);
            snacks_title.setVisibility(View.INVISIBLE);
            String create_account = "Please create an account to use the daily log!";
            breakfast_title.setText(create_account);
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

                    breakfast_title.setText("Breakfast\n");
                    for(int i = 0; i < breakfast.size(); i++) {
                        breakfast_log.append("Name: " + breakfast.get(i).Name + "\n");
                        breakfast_log.append("Calories: " + breakfast.get(i).Calories.toString() + "\n");
                        breakfast_log.append("Description: " + breakfast.get(i).Description + "\n\n");
                        breakfasttotal = breakfasttotal + breakfast.get(i).Calories;
                    }
                    breakfast_log.append("Total Calories for Breakfast: " + breakfasttotal.toString() + "\n\n");
                    if(breakfast.size() == 0) {
                        breakfast_log.append("Nothing in breakfast!\n");
                    }

                    lunch_title.setText("Lunch\n");
                    for(int i = 0; i < lunch.size(); i++) {
                        lunch_log.append("Name: " + lunch.get(i).Name + "\n");
                        lunch_log.append("Calories: " + lunch.get(i).Calories.toString() + "\n");
                        lunch_log.append("Description: " + lunch.get(i).Description + "\n\n");
                        lunchtotal = lunchtotal + lunch.get(i).Calories;
                    }
                    lunch_log.append("Total Calories for Lunch: " + lunchtotal.toString() + "\n\n");
                    if(lunch.size() == 0) {
                        lunch_log.append("Nothing in lunch!\n");
                    }

                    dinner_title.setText("Dinner\n");
                    for(int i = 0; i < dinner.size(); i++) {
                        dinner_log.append("Name: " + dinner.get(i).Name + "\n");
                        dinner_log.append("Calories: " + dinner.get(i).Calories.toString() + "\n");
                        dinner_log.append("Description: " + dinner.get(i).Description + "\n\n");
                        dinnertotal = dinnertotal + dinner.get(i).Calories;
                    }
                    dinner_log.append("Total Calories for Dinner: " + dinnertotal.toString() + "\n\n");
                    if(dinner.size() == 0) {
                        dinner_log.append("Nothing in dinner!\n");
                    }

                    snacks_title.setText("Snacks\n\n");
                    for(int i = 0; i < snacks.size(); i++) {
                        snacks_log.append("Name: " + snacks.get(i).Name + "\n");
                        snacks_log.append("Calories: " + snacks.get(i).Calories.toString() + "\n");
                        snacks_log.append("Description: " + snacks.get(i).Description + "\n\n");
                        snackstotal = snackstotal + snacks.get(i).Calories;
                    }
                    snacks_log.append("Total Calories for Snacks: " + snackstotal.toString() + "\n\n");
                    if(snacks.size() == 0) {
                        snacks_log.append("Nothing in snacks!\n");
                    }
                    grandtotal = breakfasttotal + lunchtotal + dinnertotal + snackstotal;
                    snacks_log.append("\nTotal Calories consumed today: " + grandtotal.toString());

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
                breakfast_log.setVisibility(View.INVISIBLE);
                lunch_log.setVisibility(View.INVISIBLE);
                dinner_log.setVisibility(View.INVISIBLE);
                snacks_log.setVisibility(View.INVISIBLE);
                lunch_title.setVisibility(View.INVISIBLE);
                dinner_title.setVisibility(View.INVISIBLE);
                snacks_title.setVisibility(View.INVISIBLE);
                String log_error = "Daily log file not found!";
                breakfast_title.setText(log_error);
            }
        }
    }
}
