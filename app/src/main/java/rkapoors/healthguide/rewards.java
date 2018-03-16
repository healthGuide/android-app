package rkapoors.healthguide;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class rewards extends AppCompatActivity {
    ImageView bronze, silver, gold;
    TextView count,btv,stv,gtv;

    final Context context = this;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    String mailofuser="",uidofuser="",firebaselastrecorded="",valoocounter="";
    int flg=0;

    private String dt="", prevdt="";
    int selectedYear;
    int selectedMonth;
    int selectedDayOfMonth;
    private SimpleDateFormat dateFormatter;

    Date curdate,lastdate,prevcurdate;
    DatabaseReference rewardref;

    ScrollView vw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        mFirebaseInstance = FirebaseDatabase.getInstance();

        //mFirebaseInstance.setPersistenceEnabled(true);
        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {mailofuser=user.getEmail();uidofuser=user.getUid();}

        vw=(ScrollView)findViewById(R.id.rwview);

        count = (TextView)findViewById(R.id.counter);
        btv = (TextView)findViewById(R.id.bronzetv);
        stv = (TextView)findViewById(R.id.silvertv);
        gtv = (TextView)findViewById(R.id.goldtv);

        bronze = (ImageView)findViewById(R.id.bronze);
        silver = (ImageView)findViewById(R.id.silver);
        gold = (ImageView)findViewById(R.id.gold);

        setTitle("My Rewards");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        rewardref = mFirebaseDatabase.child("users").child(uidofuser).child("rewards");

        Calendar c=Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        selectedYear=c.get(Calendar.YEAR);
        selectedMonth=c.get(Calendar.MONTH);
        selectedDayOfMonth=c.get(Calendar.DAY_OF_MONTH);
        c.set(selectedYear,selectedMonth,selectedDayOfMonth);
        dt=dateFormatter.format(c.getTime());

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        cal.add(Calendar.DATE,-1);
        prevdt = dateFormatter.format(cal.getTime());

        FloatingActionButton bt = (FloatingActionButton)findViewById(R.id.refreshbt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if(!isConnected)
                {
                    Snackbar.make(vw, "Check Internet Connection", Snackbar.LENGTH_LONG).show();
                }
                else {
                    flg=0;
                    fetchrecord task = new fetchrecord(rewards.this);
                    task.execute();
                }
            }
        });

        FloatingActionButton infobt = (FloatingActionButton)findViewById(R.id.infobt);
        infobt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // set dialog message
                alertDialogBuilder
                        .setTitle("healthGuide rewards")
                        .setMessage("score = no. of consecutive days for which records are made.\n\nA miss will lead to loss of progress.")
                        .setCancelable(true)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }
        });

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(!isConnected)
        {
            Snackbar.make(vw, "Check Internet Connection", Snackbar.LENGTH_LONG).show();
        }
        else {
            fetchrecord task = new fetchrecord(rewards.this);
            task.execute();
        }
    }

    private class fetchrecord extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pd;

        public fetchrecord(rewards activity){
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
                rewardref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        valoocounter = dataSnapshot.child("counter").getValue(String.class);
                        firebaselastrecorded = dataSnapshot.child("lastrecorded").getValue(String.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Snackbar.make(vw,"Something went wrong. Try again.",Snackbar.LENGTH_LONG).show();
                    }
                });

                flg=1;
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
                    if(flg==1) {

                        if(!firebaselastrecorded.equals("0")) {
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                            try {
                                curdate = df.parse(dt);
                                prevcurdate = df.parse(prevdt);
                                lastdate = df.parse(firebaselastrecorded);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (curdate.compareTo(lastdate)==0 || prevcurdate.compareTo(lastdate)==0) {
                                count.setText(valoocounter);
                            }
                            else {
                                count.setText("0");
                                rewardref.child("counter").setValue("0");
                                Snackbar.make(vw, "Progress lost.\nRecords were not made daily.", Snackbar.LENGTH_LONG).show();
                            }
                        }
                        else count.setText("0");

                        int dayscount = Integer.parseInt(count.getText().toString());
                        if (dayscount >= 30) {
                            bronze.setAlpha(1.0f);
                            btv.setVisibility(View.INVISIBLE);
                        }
                        if (dayscount >= 45) {
                            silver.setAlpha(1.0f);
                            stv.setVisibility(View.INVISIBLE);
                        }
                        if (dayscount >= 60) {
                            gold.setAlpha(1.0f);
                            gtv.setVisibility(View.INVISIBLE);
                        }
                    }
                    else Snackbar.make(vw,"Something went wrong. Try again.",Snackbar.LENGTH_LONG).show();

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
