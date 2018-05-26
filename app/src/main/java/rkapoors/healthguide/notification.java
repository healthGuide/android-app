package rkapoors.healthguide;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

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
import java.util.Locale;
import java.util.TimeZone;

public class notification extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    Button bt;
    ListView lst;

    ArrayList<String> messagelist;
    ArrayAdapter<String> adapter;

    String prevdt="", uidofuser="";
    RelativeLayout relativeLayout;

    Date firebasetithi, prevdate;
    DateFormat df;
    SimpleDateFormat dateFormatter;

    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        setTitle("notifications");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference();

        bt = (Button)findViewById(R.id.rldbt);
        lst = (ListView)findViewById(R.id.msglist);
        relativeLayout = (RelativeLayout)findViewById(R.id.layout);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        cal.add(Calendar.DATE,-8);              //go back 8 days
        prevdt = dateFormatter.format(cal.getTime());

        df = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
        try {
            prevdate = df.parse(prevdt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) uidofuser = user.getUid();

        fetchrecord task = new fetchrecord(notification.this);
        task.execute();

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag=0;
                fetchrecord task = new fetchrecord(notification.this);
                task.execute();
            }
        });
    }

    private class fetchrecord extends AsyncTask<Void, Void, Void>{
        private ProgressDialog pd;

        public fetchrecord(notification activity){
            pd = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute(){
            pd.setMessage("Loading...");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params){
            try{

                databaseReference.child("users").child(uidofuser).child("notifs").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        messagelist = new ArrayList<>();
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            String tithi = ds.getKey();
                            try {
                                firebasetithi = df.parse(tithi);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if(prevdate.compareTo(firebasetithi)>=0){
                                ds.getRef().setValue(null);
                            }

                            else{
                                for(DataSnapshot dts : ds.getChildren()){
                                    messagelist.add("\n"+tithi+"\n\n"+dts.child("msg").getValue(String.class));
                                }
                            }
                        }
                        flag=1;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Snackbar.make(relativeLayout,"Connection Lost. Try Again.",Snackbar.LENGTH_LONG).show();
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
                public void run() {
                    if(flag==1){
                        adapter=new ArrayAdapter<>(notification.this,android.R.layout.simple_list_item_1,messagelist);
                        lst.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        if(messagelist.size()==0){
                            Snackbar.make(relativeLayout,"No new notifications.",Snackbar.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Snackbar.make(relativeLayout,"Something went wrong. Try again.",Snackbar.LENGTH_LONG).show();
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
