package rkapoors.healthguide;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signup extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputrePassword, inputName;
    private Button btnSignIn, btnSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private CoordinatorLayout coordinatorLayout;

    FirebaseDatabase database;
    DatabaseReference dbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        setTitle("Sign up");

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(!isConnected) Snackbar.make(coordinatorLayout,"Check Internet Connection",Snackbar.LENGTH_LONG).show();

        database = FirebaseDatabase.getInstance();
        dbref = database.getReference();

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.login_button);
        btnSignUp = (Button) findViewById(R.id.signup_button);
        inputName = (EditText)findViewById(R.id.username);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputrePassword = (EditText) findViewById(R.id.repassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signup.this,login.class));
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = inputName.getText().toString().trim();
                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();
                String repass = inputrePassword.getText().toString().trim();

                //hide keyboard when snackbar appears
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(coordinatorLayout.getWindowToken(), 0);

                if(TextUtils.isEmpty(name)){
                    Snackbar.make(coordinatorLayout,"Enter your name",Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Snackbar.make(coordinatorLayout, "Enter email address", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Snackbar passsnackbar = Snackbar.make(coordinatorLayout, "Enter password", Snackbar.LENGTH_LONG);
                    passsnackbar.show();
                    return;
                }

                if (password.length() < 6) {
                    Snackbar.make(coordinatorLayout, "Password too short. Minimum 6 characters.", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if(!password.equals(repass)){
                    Snackbar.make(coordinatorLayout, "Passwords donot match. Try again.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    if(task.getException() instanceof FirebaseAuthUserCollisionException)
                                    {
                                        Snackbar.make(coordinatorLayout, "User already exists", Snackbar.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        Snackbar.make(coordinatorLayout, "Request FAILED. Try again.", Snackbar.LENGTH_LONG).show();
                                    }
                                } else {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if(user!=null){
                                        DatabaseReference usernode =  dbref.child("users").child(user.getUid());
                                        usernode.child("email").setValue(user.getEmail());
                                        usernode.child("name").setValue(name);
                                        usernode.child("rewards").child("lastrecorded").setValue("0");
                                        usernode.child("rewards").child("counter").setValue("0");
                                    }
                                    Toast.makeText(signup.this, "WELCOME to healthGuide", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(signup.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}