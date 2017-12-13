package rkapoors.healthguide;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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

    int selectedYear;
    int selectedMonth;
    int selectedDayOfMonth;
    private SimpleDateFormat dateFormatter;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String readid;
    String mailofuser="";
    String uidofuser="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newrecord);

        mFirebaseInstance = FirebaseDatabase.getInstance();

        //mFirebaseInstance.setPersistenceEnabled(true);
        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference();

        setTitle("New Record");
       android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {mailofuser=user.getEmail();uidofuser=user.getUid();}

        final Spinner spinner= (Spinner)findViewById(R.id.comspinner);
        final EditText val=(EditText)findViewById(R.id.glucolevel);
        final EditText dosageet = (EditText)findViewById(R.id.others);
        final Button rbt=(Button)findViewById(R.id.rbt);
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        final TextView mailuser = (TextView)findViewById(R.id.usermail);

        mFirebaseDatabase.child("users").child(uidofuser).child("doctor").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("email").getValue(String.class);
                if(username!=null)
                mailuser.setText("Sharing with : "+username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.comments, android.R.layout.simple_spinner_item);

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

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(coordinatorLayout.getWindowToken(), 0);

                glucoval=val.getText().toString().trim();
                dosageval=dosageet.getText().toString().trim();

                if (TextUtils.isEmpty(glucoval)) {
                    Snackbar.make(coordinatorLayout, "Enter glucose reading.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(dosageval)) {
                    Snackbar.make(coordinatorLayout, "Enter last insulin dosage intake.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                Calendar c=Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));

                dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                selectedYear=c.get(Calendar.YEAR);
                selectedMonth=c.get(Calendar.MONTH);
                selectedDayOfMonth=c.get(Calendar.DAY_OF_MONTH);
                c.set(selectedYear,selectedMonth,selectedDayOfMonth);
                dt=dateFormatter.format(c.getTime());;

                Date currentLocalTime = c.getTime();
                DateFormat date = new SimpleDateFormat("hh:mm a");
                date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                tm = date.format(currentLocalTime);

                final checkrecorddata uservals = new checkrecorddata(tm,comm,glucoval,dosageval);

                //Donot use email id for child   as characters . * ,   etc. are not allowed for database reference
                DatabaseReference temp = mFirebaseDatabase.child("users").child(uidofuser).child("records").child(dt);

                readid=temp.push().getKey();
                temp.child(readid).setValue(uservals);

                rbt.setEnabled(false);
                rbt.setTextColor(Color.parseColor("#A9A9A9"));
                Snackbar.make(coordinatorLayout,"Recorded Successfully",Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
