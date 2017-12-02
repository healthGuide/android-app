package rkapoors.healthguide;

/**
 * Created by KAPOOR's on 09-09-2017.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

public class MySampleFragment3 extends Fragment {

    private static View mView;

    int longClickDuration=3000;
    private boolean isLongPress = false;

    //private CountDownTimer countDownTimer;

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

        mView = inflater.inflate(R.layout.sample_fragment3, container, false);

        //String sampleText = getArguments().getString("bString");      GET from args

        final ImageButton alertb = (ImageButton) mView.findViewById(R.id.alertb);
        final ImageButton contactb = (ImageButton) mView.findViewById(R.id.cbt);

        contactb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),contacts.class));
            }
        });

        alertb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isLongPress = true;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isLongPress) {
                                Toast.makeText(getActivity(), "SMS with LOCATION will be sent", Toast.LENGTH_SHORT).show();
                                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                                vibrator.vibrate(100);
                                // set your code here
                                // Don't forgot to add <uses-permission android:name="android.permission.VIBRATE" /> to vibrate.
                            }
                        }
                    }, longClickDuration);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                   isLongPress = false;
                    //countDownTimer.cancel();
                }
                else if(event.getAction()==MotionEvent.ACTION_CANCEL){    //prevents touch event to pass to other view
                  isLongPress = false;
                    //countDownTimer.cancel();
                }
                return true;                                             //touch event absorbed by view
            }
        });
        return mView;
    }
}
