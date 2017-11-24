package rkapoors.healthguide;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class newrecord extends AppCompatActivity {

    private String comments[]={"before Breakfast","after Breakfast","before Lunch","after Lunch","before Dinner","after Dinner"};
    private String comm="";
    private String tm="";
    private String dt="";
    private String glucoval="";
    private String dosageval="",doctorkimail="";

    int selectedYear;
    int selectedMonth;
    int selectedDayOfMonth;
    private SimpleDateFormat dateFormatter;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String readid;
    String mailofuser="";
    String uidofuser="";
    AutoCompleteTextView doctoremail;

    public static final String USERNAME = "MyApp_Settings";
    public static final String SEARCHHISTORY="searchhistory";
    SharedPreferences docmail;
    Set<String> history;

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
        doctoremail = (AutoCompleteTextView)findViewById(R.id.demail);

        mailuser.setText(mailofuser);

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

        docmail=getSharedPreferences(USERNAME,0);   //USERNAME specifies module of sharedPreference to be used , in private mode 0
        history = new HashSet<String>(docmail.getStringSet(SEARCHHISTORY, new HashSet<String>()));     //key, default val
        setautocompletesource();

        doctoremail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction()==KeyEvent.ACTION_DOWN)&&(keyCode==KeyEvent.KEYCODE_ENTER))
                {
                    addsearchinput(doctoremail.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });

        rbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(coordinatorLayout.getWindowToken(), 0);

                glucoval=val.getText().toString().trim();
                dosageval=dosageet.getText().toString().trim();
                doctorkimail=doctoremail.getText().toString().trim();

                if (TextUtils.isEmpty(doctorkimail)) {
                    Snackbar.make(coordinatorLayout, "Enter doctor's email.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(glucoval)) {
                    Snackbar.make(coordinatorLayout, "Enter glucose reading.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(dosageval)) {
                    Snackbar.make(coordinatorLayout, "Enter last insulin dosage intake.", Snackbar.LENGTH_LONG).show();
                    return;
                }

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

                final checkrecorddata uservals = new checkrecorddata(tm,comm,glucoval,dosageval);

                //Donot use email id for child   as characters . * ,   etc. are not allowed for database reference
                DatabaseReference temp = mFirebaseDatabase.child("users").child(uidofuser).child("records").child(dt);

                readid=temp.push().getKey();
                temp.child(readid).setValue(uservals);

                history.add(doctorkimail);

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
    private void setautocompletesource()
    {
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,history.toArray(new String[history.size()]));
        doctoremail.setAdapter(adapter);
    }
    private void addsearchinput(String input)
    {
        if(!history.contains(input))
        {
            history.add(input);
            setautocompletesource();
        }
    }
    private void saveprefs()
    {
        docmail=getSharedPreferences(USERNAME,0);                 //name of sharedPreference module, mode 0 : accessible by app
        SharedPreferences.Editor editor=docmail.edit();
        editor.putStringSet(SEARCHHISTORY,history);              //SEARCHHISTORY is a key , history is value
        editor.apply();
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        saveprefs();
    }
}
