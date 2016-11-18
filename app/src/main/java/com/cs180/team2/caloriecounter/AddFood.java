package com.cs180.team2.caloriecounter;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AddFood extends AppCompatActivity {

    public static class Foodentry
    {
        public long Calories;
        public String Description;
        public String Tag;
        public String User;

        public Foodentry(long Calories, String Description, String Tag, String User)
        {
            this.Calories = Calories;
            this.Description = Description;
            this.Tag = Tag;
            this.User = User;
        }

    }


    private AutoCompleteTextView iname;
    private EditText icalories;
    private AutoCompleteTextView idescription;
    private AutoCompleteTextView itag;
    //private Button verifyInfo;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        iname = (AutoCompleteTextView) findViewById(R.id.iname);
        idescription = (AutoCompleteTextView) findViewById(R.id.idescription);
        icalories = (EditText) findViewById(R.id.icalories);
        itag = (AutoCompleteTextView) findViewById(R.id.itag);

        Button verifyPassword = (Button) findViewById(R.id.verifypass);

        verifyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkFields();
            }
        });
    }

    public String getString(AutoCompleteTextView view) {
        return view.getText().toString();
    }

    private boolean isShort()
    {
        String calories = icalories.getText().toString();
        String name = iname.getText().toString();
        String description = idescription.getText().toString();
        String tag = itag.getText().toString();

        if (calories.length() < 1 || name.length() < 1 || description.length() < 1 || tag.length() < 1)
        {
            Context context = getApplicationContext();
            CharSequence text = "All fields required!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            return true;
        }

        return false;
    }

    private void registerFood()
    {
        final String fname = getString(iname);
        final String cal = icalories.getText().toString(); //TODO: CHECK LIMIT OF CALORIES HERE BRIAN
        final String des = getString(idescription);
        final String tag = getString(itag);
        final String user = LoginActivity.username;

        final DatabaseReference foodbase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kaloriekounterk.firebaseio.com/Food");

        foodbase.child(fname).setValue(new Foodentry(Integer.parseInt(cal), des, tag, user));
        Context context = getApplicationContext();
        CharSequence text = "Food entry successfully added!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        DailyCalories();
    }

    public void DailyCalories() {
        Intent intent = new Intent(this, DailyCalories.class);
        startActivity(intent);
    }


    private void checkFields()
    {
        if (!isShort()) {
            if(Integer.parseInt(icalories.getText().toString()) < 9000)
                registerFood();
            else
            {
                Context context = getApplicationContext();
                CharSequence text = "No.";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }
    }

}