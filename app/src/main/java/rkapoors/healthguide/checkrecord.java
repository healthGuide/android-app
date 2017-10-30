package rkapoors.healthguide;

import android.os.Bundle;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class checkrecord extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    List<checkrecorddata> list;
    RecyclerView recycle;
    Button ftbt;
    TextView frdt,todt;

    String uidofuser="";
    RelativeLayout relativeLayout;

    Date fromtithi, totithi, firebasetithi;

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

        final DateFormat df = new SimpleDateFormat("dd-MM-yyyy",Locale.US);
        try {
            fromtithi = df.parse(frdt.getText().toString());
            totithi = df.parse(todt.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) uidofuser = user.getUid();

        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference();
        databaseReference.child("users").child(uidofuser).addValueEventListener(new ValueEventListener() {
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
                    if(firebasetithi.compareTo(fromtithi)>=0 && firebasetithi.compareTo(totithi)<=0) {
                        for (DataSnapshot dts : ds.getChildren()) {
                            checkrecorddata value = dts.getValue(checkrecorddata.class);
                            checkrecorddata temp = new checkrecorddata();
                            String samay = value.gettm();
                            String kab = value.getcomment();
                            String kitnamed = value.getothercm();
                            String kitnival = value.getglucoreading();
                            temp.setdt(tithi);
                            temp.settm(samay);
                            temp.setcomment(kab);
                            temp.setothercm(kitnamed);
                            temp.setglucoreading(kitnival);
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

        ftbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag==1){
                recycleadapter recycadp = new recycleadapter(list,checkrecord.this);
                RecyclerView.LayoutManager recyclayout = new LinearLayoutManager(checkrecord.this);
                recycle.setLayoutManager(recyclayout);
                recycle.setItemAnimator( new DefaultItemAnimator());
                recycle.setAdapter(recycadp);}
                else{
                    Snackbar.make(relativeLayout,"Check Connection or Constraints.",Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}