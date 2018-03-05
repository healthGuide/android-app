package rkapoors.healthguide;

/**
 * Created by KAPOOR's on 09-09-2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

public class MySampleFragment extends Fragment {

    private static View mView;
    TextView ftv, rtv, mexctv, eexctv;
    Switch morinstv,aftinstv,niginstv, btv;
    Button sbt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.sample_fragment, container, false);

        ftv = (TextView)mView.findViewById(R.id.ftv);
        rtv = (TextView)mView.findViewById(R.id.rtv);
        mexctv = (TextView)mView.findViewById(R.id.mexctv);
        morinstv = (Switch) mView.findViewById(R.id.morinstv);
        aftinstv = (Switch) mView.findViewById(R.id.aftinstv);
        eexctv = (TextView) mView.findViewById(R.id.eexctv);
        niginstv = (Switch) mView.findViewById(R.id.niginstv);
        btv = (Switch) mView.findViewById(R.id.btv);

        sbt = (Button)mView.findViewById(R.id.sbt);

        FloatingActionButton fab = (FloatingActionButton) mView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(getActivity(), notification.class);
                startActivity(intent);
            }
        });

        sbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return mView;
    }
}
