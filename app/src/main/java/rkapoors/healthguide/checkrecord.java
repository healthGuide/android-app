package rkapoors.healthguide;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class checkrecord extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkrecord);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragContainer);

        String fdate = getIntent().getStringExtra("fromdate");
        String tdate = getIntent().getStringExtra("todate");

        if (fragment == null) {
            fragment = new CardFragment();

            Bundle arguments = new Bundle();
            arguments.putString("fromdate", fdate);
            arguments.putString("todate", tdate);
            fragment.setArguments(arguments);
            fm.beginTransaction().add(R.id.fragContainer, fragment).commit();
        }
    }
}