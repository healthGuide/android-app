package rkapoors.healthguide;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class checkrecord extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    List<checkrecorddata> list;
    RecyclerView recycle;
    Button ftbt;
    TextView frdt,todt;

    String uidofuser="",prevdt="";
    RelativeLayout relativeLayout;

    Date fromtithi, totithi, firebasetithi, prevdate;
    DateFormat df;
    SimpleDateFormat dateFormatter;

    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkrecord);

        setTitle("Records");
        ActionBar actionBar =getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recycle = (RecyclerView) findViewById(R.id.cardView);
        ftbt = (Button)findViewById(R.id.fetchbt);
        frdt = (TextView)findViewById(R.id.fttv);
        todt = (TextView)findViewById(R.id.tttv);
        relativeLayout = (RelativeLayout)findViewById(R.id.rellayout);

        frdt.setText(getIntent().getStringExtra("fromdate"));
        todt.setText(getIntent().getStringExtra("todate"));

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        cal.add(Calendar.DATE,-61);              //go back 61 days
        prevdt = dateFormatter.format(cal.getTime());

        df = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
        try {
            fromtithi = df.parse(frdt.getText().toString());
            totithi = df.parse(todt.getText().toString());
            prevdate = df.parse(prevdt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) uidofuser = user.getUid();

        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference();

        fetchrecord task = new fetchrecord(checkrecord.this);
        task.execute();

        ftbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag=0;
                fetchrecord task = new fetchrecord(checkrecord.this);
                task.execute();
            }
        });

        FloatingActionButton fbt = (FloatingActionButton)findViewById(R.id.grbt);
        fbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gract = new Intent(checkrecord.this,grview.class);
                gract.putExtra("patientuid",uidofuser);
                startActivity(gract);
            }
        });
    }

    private class fetchrecord extends AsyncTask<Void, Void, Void>{
        private ProgressDialog pd;

        public fetchrecord(checkrecord activity){
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
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                public void run() {
                    if(flag==1){
                        recycleadapter recycadp = new recycleadapter(list,checkrecord.this);
                        recycadp.notifyDataSetChanged();
                        RecyclerView.LayoutManager recyclayout = new LinearLayoutManager(checkrecord.this);
                        recycle.setLayoutManager(recyclayout);
                        recycle.setItemAnimator( new DefaultItemAnimator());
                        recycle.setAdapter(recycadp);
                    }
                    else{
                        Snackbar.make(relativeLayout,"Check Connection or Constraints.",Snackbar.LENGTH_LONG).show();
                    }

                    pd.dismiss();
                }
            },5000);    //show for atlest 500 msec
        }

        @Override
        protected Void doInBackground(Void... params){
            try{
                databaseReference.child("users").child(uidofuser).child("records").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        list = new ArrayList<checkrecorddata>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String tithi = ds.getKey();
                            try {
                                firebasetithi = df.parse(tithi);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if(prevdate.compareTo(firebasetithi)>=0){
                                ds.getRef().setValue(null);
                            }
                            else if(firebasetithi.compareTo(fromtithi)>=0 && firebasetithi.compareTo(totithi)<=0) {
                                for (DataSnapshot dts : ds.getChildren()) {
                                    checkrecorddata value = dts.getValue(checkrecorddata.class);
                                    checkrecorddata temp = new checkrecorddata();
                                    String samay = value.gettime();
                                    String kab = value.getcomment();
                                    String kitnamed = value.getdosage();
                                    String kitnival = value.getvalue();
                                    temp.setdt(tithi);
                                    temp.settime(samay);
                                    temp.setcomment(kab);
                                    temp.setdosage(kitnamed);
                                    temp.setvalue(kitnival);
                                    list.add(temp);
                                    flag = 1;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Failed to read value
                        Snackbar.make(relativeLayout,"Connection Lost. Try Again.",Snackbar.LENGTH_LONG).show();
                    }
                });
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}