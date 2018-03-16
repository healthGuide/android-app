package rkapoors.healthguide;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class recact extends AppCompatActivity {

    int selectedYear;
    int selectedMonth;
    int selectedDayOfMonth;
    public TextView fdt,fbt;
    public TextView tdt,tbt;

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;

    private RelativeLayout relativeLayout;
    Button nr,chk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recact);

        setTitle("records");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Calendar c = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        selectedYear=c.get(Calendar.YEAR);
        selectedMonth=c.get(Calendar.MONTH);
        selectedDayOfMonth=c.get(Calendar.DAY_OF_MONTH);
        c.set(selectedYear,selectedMonth,selectedDayOfMonth);

        String datetoshow=dateFormatter.format(c.getTime());

        //String sampleText = getArguments().getString("bString");      GET from args
        relativeLayout=(RelativeLayout)findViewById(R.id.content);

        fdt=(TextView)findViewById(R.id.fromdt);
        fdt.setText(datetoshow);

        tdt=(TextView)findViewById(R.id.todt);
        tdt.setText(datetoshow);

        fbt=(TextView)findViewById(R.id.frombt);
        fbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar newCalendar = Calendar.getInstance();
                dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                datePickerDialog = new DatePickerDialog(recact.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        fdt.setText(dateFormatter.format(newDate.getTime()));
                    }
                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });

        tbt=(TextView)findViewById(R.id.tobt);
        tbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar newCalendar = Calendar.getInstance();
                dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                datePickerDialog = new DatePickerDialog(recact.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        tdt.setText(dateFormatter.format(newDate.getTime()));
                    }
                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });

        nr=(Button)findViewById(R.id.nr);
        nr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nra=new Intent(recact.this,newrecord.class);
                startActivity(nra);
            }
        });

        chk=(Button)findViewById(R.id.chk);
        chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                if(!isConnected)
                {
                    Snackbar snackbar=Snackbar.make(relativeLayout, "Check Internet Connection", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                }
                else{
                    Intent chka = new Intent(recact.this, checkrecord.class);
                    chka.putExtra("fromdate", fdt.getText().toString());
                    chka.putExtra("todate", tdt.getText().toString());
                    startActivity(chka);
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
