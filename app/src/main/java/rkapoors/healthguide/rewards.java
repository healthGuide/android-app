package rkapoors.healthguide;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class rewards extends AppCompatActivity {
    ImageView bronze, silver, gold;
    TextView count,btv,stv,gtv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        count = (TextView)findViewById(R.id.counter);
        btv = (TextView)findViewById(R.id.bronzetv);
        stv = (TextView)findViewById(R.id.silvertv);
        gtv = (TextView)findViewById(R.id.goldtv);

        bronze = (ImageView)findViewById(R.id.bronze);
        silver = (ImageView)findViewById(R.id.silver);
        gold = (ImageView)findViewById(R.id.gold);

        setTitle("My Rewards");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //yourView.setAlpha(0.5f);
        int dayscount = Integer.parseInt(count.getText().toString());
        if(dayscount>=60) {bronze.setAlpha(1.0f);btv.setVisibility(View.GONE);}
        if(dayscount>=120) {silver.setAlpha(1.0f);stv.setVisibility(View.GONE);}
        if(dayscount>=180) {gold.setAlpha(1.0f);gtv.setVisibility(View.GONE);}
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
