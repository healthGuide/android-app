package rkapoors.healthguide;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class aboutus extends AppCompatActivity {

    Button hlpbt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        setTitle("About us");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        hlpbt = (Button)findViewById(R.id.helpbt);
        hlpbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent hlpact = new Intent(aboutus.this,helpandsupport.class);
                startActivity(hlpact);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
