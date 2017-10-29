package rkapoors.healthguide;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class checkrecord extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    List<checkrecorddata> list;
    RecyclerView recycle;
    Button ftbt;

    String uidofuser="";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkrecord);
        recycle = (RecyclerView) findViewById(R.id.cardView);
        ftbt = (Button)findViewById(R.id.fetchbt);

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
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", databaseError.toException());
            }
        });

        ftbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recycleadapter recycadp = new recycleadapter(list,checkrecord.this);
                RecyclerView.LayoutManager recyclayout = new LinearLayoutManager(checkrecord.this);
                recycle.setLayoutManager(recyclayout);
                recycle.setItemAnimator( new DefaultItemAnimator());
                recycle.setAdapter(recycadp);
            }
        });
    }
}