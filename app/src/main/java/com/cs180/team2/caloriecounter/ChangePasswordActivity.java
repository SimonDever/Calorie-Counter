package com.cs180.team2.caloriecounter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {

    //variables
    String enterPass = "";
    public static String newPass = "";
    String confirmNew = "";
    String pw = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        final DatabaseReference registeredusers = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kaloriekounterk.firebaseio.com/registeredusers");
        final EditText enterPassView = (EditText) findViewById(R.id.enterPass_edit);
        final EditText newPassView = (EditText) findViewById(R.id.newPass_edit);
        final EditText confirmNewView = (EditText) findViewById(R.id.confirmNew_edit);
        final Button submitView = (Button) findViewById(R.id.submit_button);



        registeredusers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pw = dataSnapshot.child(LoginActivity.username).child("Password").getValue(String.class);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });  //Chanho: Create listener that will obtain values of user

        submitView.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        enterPass = enterPassView.getText().toString();
                        newPass = newPassView.getText().toString();
                        confirmNew = confirmNewView.getText().toString();

                        if(!enterPass.equals(pw) || enterPass.isEmpty() || newPass.isEmpty() || confirmNew.isEmpty()){
                            Context context = getApplicationContext();
                            CharSequence text = "The password you entered is wrong or you left a field blank.";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration); //Chanho: Toast is a popup notification that disappears automatically after a period of time
                            toast.show();
                        } else if(!newPass.equals(confirmNew)) {
                            Context context = getApplicationContext();
                            CharSequence text = "The new passwords do not match";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration); //Chanho: Toast is a popup notification that disappears automatically after a period of time
                            toast.show();
                        } else if(newPass.length() < 6) {
                            Context context = getApplicationContext();
                            CharSequence text = "The new password must be at least 6 characters";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration); //Chanho: Toast is a popup notification that disappears automatically after a period of time
                            toast.show();
                        } else {
                            Map<String, Object> userUpdates = new HashMap<String, Object>();
                            userUpdates.put(LoginActivity.username + "/Password", newPass);

                            registeredusers.updateChildren(userUpdates);

                            Context context = getApplicationContext();
                            CharSequence text = "Your password has been changed.";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration); //Chanho: Toast is a popup notification that disappears automatically after a period of time
                            toast.show();
                            dailyCalories();

                        }
                    }
                });

    }



    public void dailyCalories() {
        Intent intent = new Intent(this, DailyCalories.class);
        startActivity(intent);
    }
}
