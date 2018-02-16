package rkapoors.healthguide;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class tutorial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        setTitle("Tutorial");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String[] text={"Hyperglycemia (high blood sugar)","Hypoglycemia (low blood sugar)","Prevent low/high blood sugar level","Diet / Activity",
                "Meal plan","Optimal blood sugar levels (mg/dL)","Insulin injection sites","Insulin storage","Visit to doctor","Monitoring in follow up",
        "more info ?"};
        Integer[] imageId = {R.drawable.uparrow,R.drawable.downarrow,R.drawable.updownarrow,R.drawable.diet,R.drawable.time,
        R.drawable.correct,R.drawable.injection,R.drawable.storage,R.drawable.doctor,R.drawable.eye,R.drawable.information};

        Draweradapter adapter = new Draweradapter(tutorial.this,text,imageId);

        final ListView navList = (ListView) findViewById(R.id.navList);
        navList.setAdapter(adapter);

        navList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                switch(pos){
                    case 0:
                        startActivity(new Intent(tutorial.this,hyperglycemia.class));
                        break;
                    case 1:
                        startActivity(new Intent(tutorial.this,hypoglycemia.class));
                        break;
                    case 2:
                        startActivity(new Intent(tutorial.this,fluctuation.class));
                        break;
                    case 3:
                        startActivity(new Intent(tutorial.this,diet.class));
                        break;
                    case 4:
                        startActivity(new Intent(tutorial.this,mealplan.class));
                        break;
                    case 5:
                        startActivity(new Intent(tutorial.this,optimal.class));
                        break;
                    case 6:
                        startActivity(new Intent(tutorial.this,sites.class));
                        break;
                    case 7:
                        startActivity(new Intent(tutorial.this,storage.class));
                        break;
                    case 8:
                        startActivity(new Intent(tutorial.this,doctorvisit.class));
                        break;
                    case 9:
                        startActivity(new Intent(tutorial.this,monitor.class));
                        break;
                    case 10:
                        startActivity(new Intent(tutorial.this,aboutus.class));
                        break;
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
