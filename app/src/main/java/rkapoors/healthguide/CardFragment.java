//SOURCE   https://www.androidtutorialpoint.com/material-design/android-cardview-tutorial/

package rkapoors.healthguide;

/**
 * Created by KAPOOR's on 16-09-2017.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CardFragment extends Fragment {

    ArrayList<checkrecorddata> listitems = new ArrayList<>();
    RecyclerView MyRecyclerView;
    ArrayList<String> dtarr = new ArrayList<>();
    ArrayList<String> tmarr = new ArrayList<>();
    ArrayList<String> cmarr = new ArrayList<>();
    ArrayList<String> grarr = new ArrayList<>();
    ArrayList<String> dgarr = new ArrayList<>();

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private String fromdate;
    private String todate;
    String uidofuser = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        fromdate = arguments.getString("fromdate");
        todate = arguments.getString("todate");

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) uidofuser = user.getUid();

        dtarr.add("first");
        tmarr.add("time");
        cmarr.add("cm");
        grarr.add("ghh");
        dgarr.add("kjklj");

        dtarr.add("first");
        tmarr.add("time");
        cmarr.add("cm");
        grarr.add("ghh");
        dgarr.add("kjklj");

        DatabaseReference userref = mFirebaseDatabase.child("users");

        DatabaseReference idRef = userref.child(uidofuser);
        idRef.addListenerForSingleValueEvent(new ValueEventListener() {    // change this
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    dtarr.add(ds.getKey());
                    for (DataSnapshot dts : ds.getChildren()) {
                        Userdata uservals = dts.getValue(Userdata.class);
                        tmarr.add(uservals.time);
                        cmarr.add(uservals.comment);
                        grarr.add(uservals.value);
                        dgarr.add(uservals.dosage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        initializeList();
        getActivity().setTitle("Records");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //initializeList();
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        MyRecyclerView = (RecyclerView) view.findViewById(R.id.cardView);
        MyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        if (listitems.size() > 0 & MyRecyclerView != null) {
            MyRecyclerView.setAdapter(new MyAdapter(listitems));
        }
        MyRecyclerView.setLayoutManager(MyLayoutManager);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private ArrayList<checkrecorddata> list;

        public MyAdapter(ArrayList<checkrecorddata> Data) {
            list = Data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycle_items, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {

            holder.dtTextView.setText(list.get(position).getdt());
            holder.tmTextView.setText(list.get(position).gettm());
            holder.cmTextView.setText(list.get(position).getcomment());
            holder.grTextView.setText(list.get(position).getglucoreading());
            holder.othercmTextView.setText(list.get(position).getothercm());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView dtTextView, tmTextView, cmTextView, grTextView, othercmTextView;

        public MyViewHolder(View v) {
            super(v);
            dtTextView = (TextView) v.findViewById(R.id.dttv);
            tmTextView = (TextView) v.findViewById(R.id.tmtv);
            cmTextView = (TextView) v.findViewById(R.id.cmtv);
            grTextView = (TextView) v.findViewById(R.id.grtv);
            othercmTextView = (TextView) v.findViewById(R.id.othercmtv);
        }
    }

    public void initializeList() {

        listitems.clear();

        int lenofdata = dtarr.size();
        //Toast.makeText(getActivity(),lenofdata+" "+todate+" "+fromdate, Toast.LENGTH_SHORT).show();
        for (int i = 0; i < lenofdata; i++) {                                          //set Counter here
            checkrecorddata item = new checkrecorddata();
            item.setdt(dtarr.get(i));
            item.settm(tmarr.get(i));
            item.setcomment(cmarr.get(i));
            item.setglucoreading(grarr.get(i));
            item.setothercm(dgarr.get(i));
            listitems.add(item);
        }
    }
}
