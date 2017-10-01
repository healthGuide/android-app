package rkapoors.healthguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class signup extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputrePassword;
    private Button btnSignIn, btnSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.login_button);
        btnSignUp = (Button) findViewById(R.id.signup_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputrePassword = (EditText) findViewById(R.id.repassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

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

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String repass = inputrePassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Snackbar.make(coordinatorLayout, "Enter email address", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Snackbar.make(coordinatorLayout, "Enter Password", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (password.length() < 6) {
                    Snackbar.make(coordinatorLayout, "Password too short. Minimum 6 characters reqd.", Snackbar.LENGTH_LONG).show();
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