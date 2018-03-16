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
import android.widget.ImageView;

public class MySampleFragment extends Fragment {

    private static View mView;
    ImageView img;

    public MySampleFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.sample_fragment, container, false);

        img = (ImageView)mView.findViewById(R.id.schedimg);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent schedlist = new Intent(getActivity(),schedact.class);
                startActivity(schedlist);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) mView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(getActivity(), notification.class);
                startActivity(intent);
            }
        });


        return mView;
    }
}
