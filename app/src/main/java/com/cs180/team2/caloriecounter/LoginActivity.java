package com.cs180.team2.caloriecounter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.text.InputType;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    public final static String EXTRA_MESSAGE = "com.cs180.team2.caloriecounter.MESSAGE";


    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private View mForgotPasswordView;

    private String m_Text;
    public String secretQuestion;
    public String secretAnswer;
    public static String username = "";
    private String pw = "";
    public String fullName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mForgotPasswordView = findViewById(R.id.forgot_password_button);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);



        Button mforgotPasswordButton = (Button) findViewById(R.id.forgot_password_button);
        mforgotPasswordButton.setOnClickListener( new OnClickListener() {

            @Override
            public void onClick(View v){
                forgotPassword();
            }
        });

        final LinearLayout background = (LinearLayout) findViewById(R.id.linearlayout); //START Select random background image
        Resources res = getResources();
        final TypedArray myImages = res.obtainTypedArray(R.array.myImages); //myImages array is in res/values/strings.xml
        final Random random = new Random();

        int randomInt = random.nextInt(myImages.length());
        int drawableID = myImages.getResourceId(randomInt, -1);
        background.setBackgroundResource(drawableID); //END Select random background image
    }




    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {


            final DatabaseReference registeredusers = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kaloriekounterk.firebaseio.com/registeredusers");
            //final DatabaseReference User = registeredusers.child(email);  // Chanho: access firebase and search by email

            ValueEventListener valueEventListener = registeredusers.addValueEventListener(new ValueEventListener() {  //Chanho: Create listener that will obtain values of user
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String email = mEmailView.getText().toString();
                    final String password = mPasswordView.getText().toString();

                    if (dataSnapshot.hasChild(email)) {
                        username = dataSnapshot.child(email).getKey();
                        fullName = dataSnapshot.child(email).child("Name").getValue(String.class);
                        pw = dataSnapshot.child(email).child("Password").getValue(String.class);

                        //TextView textView = (TextView) findViewById(R.id.databaseOutput);
                        //textView.setText("Username: " + username + "\nFull Name: " + fullName + "\nPassword: " + pw);

                        if (password.equals(pw)) {
                            dailyCalories(); //TODO: REMEMBER TO KEEP USERNAME/FULL NAME VALUES GLOBALLY FROM HERE ON
                        }
                        else {
                            Context context = getApplicationContext();
                            CharSequence text = "Wrong password!";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration); //Chanho: Toast is a popup notification that disappears automatically after a period of time
                            toast.show();                                          //This toast tells the user that the password for the username is wrong


                        }
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this); //Chanho: Dialogs are popup notifications that require users to interact with to get rid of.
                        builder.setMessage("Username not found. Register new user?"); //This dialog asks the user if they want to register a new user


                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                registerUser();
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

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getMessage());
                }
            });
        }
    }

    private void forgotPassword(){
        m_Text = "";
        final int usrChoice = 0;
        final int pwChoice = 1;
        final DatabaseReference registeredusers = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kaloriekounterk.firebaseio.com/registeredusers");
        final DatabaseReference usersByName = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kaloriekounterk.firebaseio.com/usersByName");
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this); //Chanho: Dialogs are popup notifications that require users to interact with to get rid of.
        builder.setMessage("Enter your Full Name and then click \"username\" button " +
                "to get your username or enter your username and click the \"password\" button to get your password");

        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Password", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                m_Text = input.getText().toString(); //will be password from input

                ValueEventListener valueEventListener = registeredusers.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(m_Text)){ //m_text is username from input right here
                            username = dataSnapshot.child(m_Text).getKey();
                            secretQuestion = dataSnapshot.child(username).child("secretq").child("question").getValue(String.class);
                            secretAnswer = dataSnapshot.child(username).child("secretq").child("answer").getValue(String.class);
                            pw = dataSnapshot.child(username).child("Password").getValue(String.class);

                            askQuestions(pwChoice, secretQuestion, secretAnswer, username, pw);
                        }
                        else {
                            final AlertDialog.Builder builder2 = new AlertDialog.Builder(LoginActivity.this);
                            builder2.setMessage("Could not find");
                            builder2.show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        builder.setNegativeButton("Username", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                    m_Text = input.getText().toString(); //will be Full Name from input

                    ValueEventListener valueEventListener = usersByName.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(m_Text)){ //m_text is Full Name from input right here
                                fullName = dataSnapshot.child(m_Text).getKey();
                                secretQuestion = dataSnapshot.child(fullName).child("secretq").child("question").getValue(String.class);
                                secretAnswer = dataSnapshot.child(fullName).child("secretq").child("answer").getValue(String.class);
                                pw = "";//useless but required to use the function
                                username = dataSnapshot.child(fullName).child("username").getValue(String.class);


                                askQuestions(usrChoice, secretQuestion, secretAnswer, username, pw);
                            }
                            else {
                                final AlertDialog.Builder builder2 = new AlertDialog.Builder(LoginActivity.this);
                                builder2.setMessage("Could not find");
                                builder2.show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void askQuestions(final int choicer, String secretQuestion, final String secretAnswer, String usrNme, String psswrd){

        final String dialogBoxPW = "Your password is: " + psswrd;
        final String dialogBoxUN = "Your username is: " + usrNme;
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Answer your secret question: " + secretQuestion);

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString(); //answer to secret question

                if(m_Text.equals(secretAnswer) && choicer == 1) {
                    final AlertDialog.Builder builder2 = new AlertDialog.Builder(LoginActivity.this);
                    builder2.setMessage(dialogBoxPW);
                    builder2.show();
                } else if(m_Text.equals(secretAnswer) && choicer == 0){
                    final AlertDialog.Builder builder2 = new AlertDialog.Builder(LoginActivity.this);
                    builder2.setMessage(dialogBoxUN);
                    builder2.show();
                } else {
                    final AlertDialog.Builder builder2 = new AlertDialog.Builder(LoginActivity.this);
                    builder2.setMessage("That's wrong!");
                    builder2.show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private boolean isEmailValid(String email) {
        return email != "";
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 0;
    }




    public void dailyCaloriesGUI(View view) {
        username = "";
        Intent intent = new Intent(this, DailyCalories.class);
        startActivity(intent);
    }

    public void dailyCalories() {
        Intent intent = new Intent(this, DailyCalories.class);
        startActivity(intent);
    }

    public void registerUser() {
        Intent intent = new Intent(this, RegisterUser.class);
        startActivity(intent);
    }

    public void registration(View view) {
        Intent intent = new Intent(this, RegisterUser.class);
        startActivity(intent);
    }



    /*public void forgotPassword(){
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }*/



}

