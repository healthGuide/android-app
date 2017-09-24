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
import java.util.ArrayList;

public class CardFragment extends Fragment {

    ArrayList<checkrecorddata> listitems = new ArrayList<>();
    RecyclerView MyRecyclerView;
    String dtarr[] = {"3-9-17","4-9-17","5-9-17","6-9-17","16-9-17","16-9-17","17-9-17","19-9-17","26-9-17"};
    String tmarr[] = {"10:00","11:00","12:00","13:00","13:00","13:00","13:00","13:00","13:00"};
    String cmarr[] = {"before lunch","after dinner","before breakfast","before dinner","before breakfast","before breakfast","before breakfast","before breakfast","before breakfast"};
    String grarr[] = {"113","123","230","150","189","156","163","145","169"};
    String othercomm[]={"-","-","took 20 units @ night","took 10 units @ noon","took 15 units @ night","-","took 10 units","-","-"};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeList();
        getActivity().setTitle("Records");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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
        public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
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

        public TextView dtTextView,tmTextView,cmTextView,grTextView,othercmTextView;

        public MyViewHolder(View v) {
            super(v);
            dtTextView = (TextView) v.findViewById(R.id.dttv);
            tmTextView = (TextView) v.findViewById(R.id.tmtv);
            cmTextView = (TextView) v.findViewById(R.id.cmtv);
            grTextView = (TextView) v.findViewById(R.id.grtv);
            othercmTextView = (TextView) v.findViewById(R.id.othercmtv);
        }}

                public void initializeList() {
                    listitems.clear();

                    for(int i =0;i<9;i++){                                          //set Counter here
                        checkrecorddata item = new checkrecorddata();
                        item.setdt(dtarr[i]);
                        item.settm(tmarr[i]);
                        item.setcomment(cmarr[i]);
                        item.setglucoreading(grarr[i]);
                        item.setothercm(othercomm[i]);
                        listitems.add(item);
                    }
                }
            }
