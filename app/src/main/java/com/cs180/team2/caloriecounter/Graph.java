package com.cs180.team2.caloriecounter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import static android.R.attr.resource;
import static com.cs180.team2.caloriecounter.DailyCalories.cal_limit;
import static com.cs180.team2.caloriecounter.LoginActivity.username;
import static com.cs180.team2.caloriecounter.R.color.primary_material_dark;

/**
 * Created by briannguyen on 11/11/2016.
 */

public class Graph extends AppCompatActivity {
    static LineGraphSeries<DataPoint> line = new LineGraphSeries<>(new DataPoint[]{   //change y according to calorie goal
            new DataPoint(0, Integer.parseInt(cal_limit)),
            new DataPoint(150, Integer.parseInt(cal_limit))

    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat_graph);

        GraphView graph = (GraphView) findViewById(R.id.graph);

        Long breakfasttotal = Long.valueOf(0);
        Long lunchtotal = Long.valueOf(0);
        Long dinnertotal = Long.valueOf(0);
        Long snackstotal = Long.valueOf(0);
        Long grandtotal = Long.valueOf(0);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[]{"", "Breakfast", "Lunch", "Dinner", "Snacks", "Total", ""});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graph.getGridLabelRenderer().setTextSize(50f);

        final String root = Environment.getExternalStorageDirectory().toString();
        File dir = new File(root + "/daily_logs"); //FILE DIRECTORY

        if (!dir.exists()) {
            dir.mkdirs();
        }

        Calendar c = Calendar.getInstance();
        String Filename = Integer.toString(c.get(Calendar.MONTH)) + "." + Integer.toString(c.get(Calendar.DAY_OF_MONTH)) + "." + Integer.toString(c.get(Calendar.YEAR));
        Filename = Filename + "_" + username + ".txt";
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


        ArrayList<FoodEntry> breakfast = new ArrayList<FoodEntry>();
        ArrayList<FoodEntry> lunch = new ArrayList<FoodEntry>();
        ArrayList<FoodEntry> dinner = new ArrayList<FoodEntry>();
        ArrayList<FoodEntry> snacks = new ArrayList<FoodEntry>();


        if (file.exists()) {
            try {
                InputStream in = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));


                String line = reader.readLine();
                while (line != null) {
                    FoodEntry tempFood = new FoodEntry("", Long.valueOf(0), "", "", "");
                    tempFood.Name = line;
                    tempFood.Calories = Long.valueOf(reader.readLine());
                    tempFood.Description = reader.readLine();
                    tempFood.Tag = reader.readLine();
                    tempFood.User = reader.readLine();
                    tempFood.Tag = reader.readLine();
                    line = reader.readLine();
                    if (tempFood.Tag.equals("Breakfast")) {
                        breakfast.add(tempFood);
                    } else if (tempFood.Tag.equals("Lunch")) {
                        lunch.add(tempFood);
                    } else if (tempFood.Tag.equals("Dinner")) {
                        dinner.add(tempFood);
                    } else if (tempFood.Tag.equals("Snacks")) {
                        snacks.add(tempFood);
                    }
                }

                for (int i = 0; i < breakfast.size(); i++) {
                    breakfasttotal = breakfasttotal + breakfast.get(i).Calories;
                }

                for (int i = 0; i < lunch.size(); i++) {
                    lunchtotal = lunchtotal + lunch.get(i).Calories;
                }


                for (int i = 0; i < dinner.size(); i++) {
                    dinnertotal = dinnertotal + dinner.get(i).Calories;
                }


                for (int i = 0; i < snacks.size(); i++) {
                    snackstotal = snackstotal + snacks.get(i).Calories;
                }

                grandtotal = breakfasttotal + lunchtotal + dinnertotal + snackstotal;

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

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[]{   //change only y values please, y values are total calories consumed

                new DataPoint(20, breakfasttotal),   //Breakfast
                new DataPoint(40, lunchtotal),    //Lunch
                new DataPoint(60, dinnertotal),    //Dinner
                new DataPoint(80, snackstotal),    //Snacks
                new DataPoint(100, grandtotal),   //Total Calorie Consumption

        });

        series.setDrawValuesOnTop(true);
        series.setTitle("Calorie Intake");
        series.setColor(Color.WHITE);
        series.setValuesOnTopColor(Color.WHITE);

        graph.addSeries(series);
        graph.addSeries(line);

        line.setTitle("Calorie Goal");
        line.setColor(Color.rgb(102,19,31));

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);


// styling
        series.setSpacing(20);

        Button changeGoalPrompt = (Button) findViewById(R.id.changeGoal_button);
        EditText inputNewGoal = (EditText) findViewById(R.id.changeGoalInput);
        final Editable x = inputNewGoal.getText();

        changeGoalPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(x.toString().length() == 0)
                {
                    Context context = getApplicationContext();
                    CharSequence text = "Please enter in a number that is less than or equal to 50000 or greater than or equal to 0";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    return;
                }
                if( x.toString().length() <= 10  && Integer.parseInt(x.toString()) <=50000) {

                    cal_limit = x.toString();

                    line.resetData(new DataPoint[]{   //change y according to calorie goal
                            new DataPoint(0, Integer.parseInt(cal_limit)),
                            new DataPoint(150, Integer.parseInt(cal_limit))});

                    String root = Environment.getExternalStorageDirectory().toString(); //ROOT DIRECTORY
                    File goaldir = new File(root + "/calorie_goal");  //START CALORIE GOAL FILE CREATION/READ FOR USER'S CALORIE GOAL
                    String goalfilename = username + ".txt";
                    File goalfile = new File(goaldir, goalfilename);


                    if (!goalfile.exists()) { //MAKE USER GOAL FILE IF IT DOESN'T EXIST
                        try {
                            goalfile.createNewFile();
                            FileOutputStream outputStream = new FileOutputStream(goalfile);
                            outputStream.write(cal_limit.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        try {
                            FileOutputStream outputStream = new FileOutputStream(goalfile);
                            outputStream.write(cal_limit.getBytes());
                        } catch(IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
                else if(x.toString().length() > 10 || Integer.parseInt(x.toString()) > 50000 )
                {
                    Context context = getApplicationContext();
                    CharSequence text = "Get a better hobby, fatty.";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

                if(Integer.parseInt(x.toString()) == 9001)
                {
                    Context context = getApplicationContext();
                    CharSequence text = "Vegeta, what's the reading on his Calorie levels!? Its over 9000!";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });

        final LinearLayout background = (LinearLayout) findViewById(R.id.stat_graph); //START Select random background image
        Resources res = getResources();
        final TypedArray myImages = res.obtainTypedArray(R.array.myImages); //myImages array is in res/values/strings.xml
        final Random random = new Random();

        int randomInt = random.nextInt(myImages.length());
        int drawableID = myImages.getResourceId(randomInt, -1);
        background.setBackgroundResource(drawableID); //END Select random background image
    }
}
