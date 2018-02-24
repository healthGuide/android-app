package rkapoors.healthguide;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import java.util.Locale;

public class rewards extends AppCompatActivity {
    ImageView bronze, silver, gold;
    TextView count,btv,stv,gtv;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    String mailofuser="",uidofuser="";
    int flg=0;

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

        FloatingActionButton bt = (FloatingActionButton)findViewById(R.id.refreshbt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flg=0;
                fetchrecord task = new fetchrecord(rewards.this);
                task.execute();
            }
        });

        fetchrecord task = new fetchrecord(rewards.this);
        task.execute();
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
                mFirebaseDatabase.child("users").child(uidofuser).child("rewards").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String valoocounter = dataSnapshot.child("counter").getValue(String.class);
                        flg=1;
                        count.setText(valoocounter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
                        int dayscount = Integer.parseInt(count.getText().toString());
                        if (dayscount >= 60) {
                            bronze.setAlpha(1.0f);
                            btv.setVisibility(View.INVISIBLE);
                        }
                        if (dayscount >= 120) {
                            silver.setAlpha(1.0f);
                            stv.setVisibility(View.INVISIBLE);
                        }
                        if (dayscount >= 180) {
                            gold.setAlpha(1.0f);
                            gtv.setVisibility(View.INVISIBLE);
                        }
                    }
                    else Snackbar.make(vw,"Something went wrong. Try again.",Snackbar.LENGTH_LONG).show();

                    pd.dismiss();
                }
            },1500);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
