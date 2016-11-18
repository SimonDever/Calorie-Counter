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

public class RegisterUser extends AppCompatActivity {


    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("RegisterUser Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStop() {
        super.onStop();


    }

    public static class User {
        public String Name;
        public String Password;
        public Secret secretq;

        public User(String fullName, String password, String question, String answer) {
            // ...
            this.Name = fullName;
            this.Password = password;
            this.secretq = new Secret(question, answer);
        }

    }

    public static class UserByName {
        public String username;
        public String password;
        public Secret secretq;

        public UserByName(String username, String password, String question, String answer) {
            this.username = username;
            this.password = password;
            this.secretq = new Secret(question, answer);
        }
    }

    public static class Secret {
        public String question;
        public String answer;

        public Secret(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }
    }

    private AutoCompleteTextView iFullName;
    private AutoCompleteTextView iUserName;
    private AutoCompleteTextView iPassword;
    private AutoCompleteTextView iRePassword;
    private AutoCompleteTextView iSecretQ;
    private AutoCompleteTextView iSecretA;
    //private Button verifyInfo;
    private TextView testText;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        iFullName = (AutoCompleteTextView) findViewById(R.id.regFullName);
        iUserName = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        iPassword = (AutoCompleteTextView) findViewById(R.id.ipassword);
        iRePassword = (AutoCompleteTextView) findViewById(R.id.irepassword);
        iSecretQ = (AutoCompleteTextView) findViewById(R.id.secretq);
        iSecretA = (AutoCompleteTextView) findViewById(R.id.secreta);

        testText = (TextView) findViewById(R.id.textView7);

        Button verifyPassword = (Button) findViewById(R.id.verifypass);

        verifyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkFields();
                return;
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }


    public String getString(AutoCompleteTextView view) {
        return view.getText().toString();
    }

    private boolean isShort()       //checks if password is less than six characters long
    {
        String password = iPassword.getText().toString();

        if (password.length() < 6)   //change later
        {
            Context context = getApplicationContext();
            CharSequence text = "Password too short! Minimum length is six";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            return true;
        }

        return false;
    }

    private void registerUser() {
        final String fName = getString(iFullName);
        final String uName = getString(iUserName);
        final String pw = getString(iPassword);

        final String sq = getString(iSecretQ);
        final String sa = getString(iSecretA);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference registeredusers = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kaloriekounterk.firebaseio.com/registeredusers");
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kaloriekounterk.firebaseio.com/usersByName");

        registeredusers.child(uName).setValue(new User(fName, pw, sq, sa));    //registeruser branch
        userRef.child(fName).setValue(new UserByName(uName, pw, sq, sa));        //userByName branch

        Context context = getApplicationContext();
        CharSequence text = "Registration Successful";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        loginActivity();
    }

    public void loginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    static boolean passok = false;
    private void checkFields() {
        String pw = iPassword.getText().toString();
        String verifyPass = iRePassword.getText().toString();


        if (isShort()) {
            return;
        }
        if (!pw.equals(verifyPass)) {
            Context context = getApplicationContext();
            CharSequence text = "Passwords don't match";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }
        else {
            passok = true;
        }

        if(iSecretQ.getText().toString().isEmpty() || iSecretA.getText().toString().isEmpty())
        {
            Context context = getApplicationContext();
            CharSequence text = "Error with Secret Question/Secret Answer";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }



        final DatabaseReference registeredusers = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kaloriekounterk.firebaseio.com/registeredusers");

        ValueEventListener valueEventListener = registeredusers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String check = iUserName.getText().toString();
                //check = registeredusers.child(check);

                if(dataSnapshot.hasChild(iUserName.getText().toString()))
                {
                    Context context = getApplicationContext();
                    CharSequence text = "Error with username";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        registeredusers.removeEventListener(valueEventListener);

        if(passok)
        {
            registerUser();
            return;
        }
    }

}