package rkapoors.healthguide;

/**
 * Created by KAPOOR's on 09-09-2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MySampleFragment extends Fragment {

    private static View mView;

    /*CONSTRUCTOR

    public static final MySampleFragment newInstance(String sampleText) {
        MySampleFragment f = new MySampleFragment();

        Bundle b = new Bundle();
        b.putString("bString", sampleText);
        f.setArguments(b);

        return f;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.sample_fragment, container, false);

        //String sampleText = getArguments().getString("bString");      GET from args

        String sampleText = "Schedule fragment";

        TextView txtSampleText = (TextView) mView.findViewById(R.id.txtViewSample);
        txtSampleText.setText(sampleText);

        return mView;
    }
}
