package rkapoors.healthguide;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class schedact extends AppCompatActivity {

    Switch morinstv,aftinstv,niginstv, btv;
    Button sbt;

    int selectedYear;
    int selectedMonth;
    int selectedDayOfMonth;
    private SimpleDateFormat dateFormatter;

    private RadioGroup fastingGroup, recordGroup, morningexcGroup, eveningexcGroup;

    String uidofuser="",fastsugar="NA",recordmade="NA",morningexc="NA",eveningexc="NA";
    String morningins="",afterins="",nightins="",nightchk="",dt="",tm="";

    private DatabaseReference mFirebaseDatabase,schedref;
    private FirebaseDatabase mFirebaseInstance;

    RelativeLayout sl;

    int flg = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedact);

        setTitle("schedule");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {uidofuser=user.getUid();}

        schedref = mFirebaseDatabase.child("users").child(uidofuser).child("schedule");

        Calendar c=Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        selectedYear=c.get(Calendar.YEAR);
        selectedMonth=c.get(Calendar.MONTH);
        selectedDayOfMonth=c.get(Calendar.DAY_OF_MONTH);
        c.set(selectedYear,selectedMonth,selectedDayOfMonth);
        dt=dateFormatter.format(c.getTime());

        Date currentLocalTime = c.getTime();
        DateFormat date = new SimpleDateFormat("hh:mm a");
        date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        tm = date.format(currentLocalTime);

        sl = (RelativeLayout)findViewById(R.id.schedlayout);

        morinstv = (Switch)findViewById(R.id.morinstv);
        aftinstv = (Switch)findViewById(R.id.aftinstv);
        niginstv = (Switch)findViewById(R.id.niginstv);
        btv = (Switch)findViewById(R.id.btv);

        fastingGroup = (RadioGroup)findViewById(R.id.frg);
        recordGroup = (RadioGroup)findViewById(R.id.rrg);
        morningexcGroup = (RadioGroup)findViewById(R.id.mexcrg);
        eveningexcGroup = (RadioGroup)findViewById(R.id.eexcrg);

        fastingGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton rb = (RadioButton) radioGroup.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    fastsugar = rb.getText().toString();
                }

            }
        });

        recordGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton rb = (RadioButton) radioGroup.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    recordmade = rb.getText().toString();
                }

            }
        });

        morningexcGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton rb = (RadioButton) radioGroup.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    morningexc = rb.getText().toString();
                }

            }
        });

        eveningexcGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton rb = (RadioButton) radioGroup.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    eveningexc = rb.getText().toString();
                }

            }
        });

        sbt = (Button)findViewById(R.id.sbt);

        sbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(morinstv.isChecked()){
                    morningins = morinstv.getTextOn().toString();
                }
                else
                {
                    morningins = morinstv.getTextOff().toString();
                }
                if(aftinstv.isChecked()){
                    afterins = aftinstv.getTextOn().toString();
                }
                else
                {
                    afterins = aftinstv.getTextOff().toString();
                }
                if(niginstv.isChecked()){
                    nightins = niginstv.getTextOn().toString();
                }
                else
                {
                    nightins = niginstv.getTextOff().toString();
                }
                if(btv.isChecked()){
                    nightchk = btv.getTextOn().toString();
                }
                else
                {
                    nightchk = btv.getTextOff().toString();
                }

                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if(!isConnected)
                {
                    Snackbar.make(sl, "Check Internet Connection", Snackbar.LENGTH_LONG).show();
                }
                else {
                    flg=0;
                    fetchrecord task = new fetchrecord(schedact.this);
                    task.execute();
                }
            }
        });
    }

    private class fetchrecord extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pd;

        public fetchrecord(schedact activity){
            pd = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute(){
            pd.setMessage("Please wait a moment...");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params){
            try{
                schedref.child("date").setValue(dt + "  " + tm);
                schedref.child("fastingcheck").setValue(fastsugar);
                schedref.child("record").setValue(recordmade);
                schedref.child("morningexc").setValue(morningexc);
                schedref.child("morninginsulin").setValue(morningins);
                schedref.child("nooninsulin").setValue(afterins);
                schedref.child("eveningexc").setValue(eveningexc);
                schedref.child("nightinsulin").setValue(nightins);
                schedref.child("bedtimecheck").setValue(nightchk);

                flg = 1;
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(flg==1){
                        sbt.setEnabled(false);
                        sbt.setBackgroundColor(Color.parseColor("#A9A9A9"));
                        Snackbar.make(sl,"Recorded Successfully",Snackbar.LENGTH_LONG).show();
                    }else {
                        Snackbar.make(sl,"Something went wrong. Try again.",Snackbar.LENGTH_LONG).show();
                    }

                    pd.dismiss();
                }
            },5000);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
