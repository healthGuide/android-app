package rkapoors.healthguide;

/**
 * Created by KAPOOR's on 09-09-2017.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MySampleFragment2 extends Fragment{

    private static View mView;
    ImageView imgrec;

    public MySampleFragment2(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.sample_fragment2, container, false);

        imgrec = (ImageView)mView.findViewById(R.id.rimg);
        imgrec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ract = new Intent(getActivity(),recact.class);
                startActivity(ract);
            }
        });

        return mView;
    }
}
