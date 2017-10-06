package rkapoors.healthguide;

import android.content.Context;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class newrecord extends AppCompatActivity {

    private String comments[]={"before Breakfast","after Breakfast","before Lunch","after Lunch","before Dinner","after Dinner"};
    private String comm="";
    private String tm="";
    private String dt="";
    private String glucoval="";
    private String dosageval="";

    int selectedYear;
    int selectedMonth;
    int selectedDayOfMonth;
    private SimpleDateFormat dateFormatter;

    CoordinatorLayout coordinatorLayout;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String readid;

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

                dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                selectedYear=c.get(Calendar.YEAR);
                selectedMonth=c.get(Calendar.MONTH);
                selectedDayOfMonth=c.get(Calendar.DAY_OF_MONTH);
                c.set(selectedYear,selectedMonth,selectedDayOfMonth);
                dt=dateFormatter.format(c.getTime());;

                Date currentLocalTime = c.getTime();
                DateFormat date = new SimpleDateFormat("hh:mm a");
                date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                tm = date.format(currentLocalTime);

                ans.setText(dt+" "+tm+" "+comm+" "+glucoval);

                final Userdata uservals = new Userdata(tm,comm,glucoval,dosageval);

                final DatabaseReference temp = mFirebaseDatabase.child(mailofuser).child(dt);

                readid=temp.push().getKey();
                temp.child(readid).setValue(uservals);
            }
        });

    }
}
