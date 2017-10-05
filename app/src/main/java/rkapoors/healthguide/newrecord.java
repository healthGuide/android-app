package rkapoors.healthguide;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class newrecord extends AppCompatActivity {

    private String comments[]={"before Breakfast","after Breakfast","before Lunch","after Lunch","before Dinner","after Dinner"};
    private String comm="";
    private String tm="";
    private String dt="";
    private String glucoval="";
    private String dosageval="";

    CoordinatorLayout coordinatorLayout;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newrecord);

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(!isConnected)
        {
            Snackbar snackbar=Snackbar.make(coordinatorLayout, "Check Internet Connection", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }

        auth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(newrecord.this, signup.class));
                    finish();
                }
            }
        };

        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        setTitle("New Record");

        final Spinner spinner= (Spinner)findViewById(R.id.comspinner);
        final EditText val=(EditText)findViewById(R.id.glucolevel);
        final EditText dosageet = (EditText)findViewById(R.id.others);
        final TextView ans=(TextView)findViewById(R.id.dummy);
        final Button rbt=(Button)findViewById(R.id.rbt);

        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.comments,
                        android.R.layout.simple_spinner_item);

        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(staticAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                comm=comments[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        rbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ans.setText("");

               // String mailofuser=FirebaseAuth.getInstance().getCurrentUser().getEmail();     IMPLEMENT this NULL point exception
                String mailofuser="kapoorkimail";

                glucoval=val.getText().toString();
                dosageval=dosageet.getText().toString();

                Calendar c=Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));

                dt=Integer.toString(c.get(Calendar.DAY_OF_MONTH))+"-"+Integer.toString(c.get(Calendar.MONTH)+1)+"-"+Integer.toString(c.get(Calendar.YEAR));

                Date currentLocalTime = c.getTime();
                DateFormat date = new SimpleDateFormat("hh:mm a");
                date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                tm = date.format(currentLocalTime);

                ans.setText(dt+" "+tm+" "+comm+" "+glucoval);

                Userdata uservals = new Userdata(tm,comm,glucoval,dosageval);

                DatabaseReference temp = mFirebaseDatabase.child(mailofuser).child(dt);

                temp.child("1 reading").setValue(uservals);                            // IMPLEMENT this size of date node referred
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
