package rkapoors.healthguide;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class doctor extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    EditText mail;
    Button upbt;

    int flg;
    String uidofuser="",uidofdoc="",naamofuser="";
    String docmail="",mailofuser="",readid="";

    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        mail = (EditText)findViewById(R.id.email);
        upbt = (Button)findViewById(R.id.btn);

        setTitle("Doctor");
        ActionBar actionBar =getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {uidofuser = user.getUid();mailofuser=user.getEmail();}

        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference();

        databaseReference.child("users").child(uidofuser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                naamofuser = dataSnapshot.child("name").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child("users").child(uidofuser).child("doctor").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String curdoc = dataSnapshot.child("email").getValue(String.class);
                if(curdoc!=null) mail.setText(curdoc);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        upbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flg=0;

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(coordinatorLayout.getWindowToken(), 0);

                docmail=mail.getText().toString().trim();

                if(TextUtils.isEmpty(docmail)){
                    Snackbar.make(coordinatorLayout,"Doctor's email can't be empty",Snackbar.LENGTH_LONG).show();
                    return;
                }
                fetchrecord task = new fetchrecord(doctor.this);
                task.execute();
            }
        });
    }

    private class fetchrecord extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pd;

        public fetchrecord(doctor activity){
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
                    if(flg==1){
                        DatabaseReference temp = databaseReference.child("doctors").child(uidofdoc).child("patients");
                        readid = temp.push().getKey();
                        temp.child(readid).child("uid").setValue(uidofuser);
                        temp.child(readid).child("email").setValue(mailofuser);
                        temp.child(readid).child("name").setValue(naamofuser);

                        databaseReference.child("users").child(uidofuser).child("doctor").child("uid").setValue(uidofdoc);
                        databaseReference.child("users").child(uidofuser).child("doctor").child("email").setValue(docmail);

                        Snackbar.make(coordinatorLayout,"Updated Successfully.",Snackbar.LENGTH_LONG).show();
                    }
                    else{
                        Snackbar.make(coordinatorLayout,"Doctor is not registered with healthGuide-Dr.",Snackbar.LENGTH_LONG).show();
                    }

                    pd.dismiss();
                }
            },1000);    //show for atlest 1000 msec
        }

        @Override
        protected Void doInBackground(Void... params){
            try{
                    databaseReference.child("doctors").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot dts : dataSnapshot.getChildren()){
                                if(dts.child("email").getValue(String.class).equals(docmail)){
                                    flg=1;
                                    uidofdoc = dts.getKey();
                                    break;
                                }
                            }
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
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
