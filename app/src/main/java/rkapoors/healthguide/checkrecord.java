package rkapoors.healthguide;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class checkrecord extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkrecord);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragContainer);

        String fdate = getIntent().getStringExtra("fromdate");
        String tdate = getIntent().getStringExtra("todate");

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (fragment == null && isConnected) {
            fragment = new CardFragment();

            Bundle arguments = new Bundle();
            arguments.putString("fromdate", fdate);
            arguments.putString("todate", tdate);
            fragment.setArguments(arguments);
            fm.beginTransaction().add(R.id.fragContainer, fragment).commit();
        }
        else Toast.makeText(getApplicationContext(), "Check Internet Connection", Toast.LENGTH_LONG).show();
    }
}