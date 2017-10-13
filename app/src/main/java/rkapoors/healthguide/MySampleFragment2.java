package rkapoors.healthguide;

/**
 * Created by KAPOOR's on 09-09-2017.
 */

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MySampleFragment2 extends Fragment{

    private static View mView;
    int selectedYear;
    int selectedMonth;
    int selectedDayOfMonth;
    public TextView fdt;
    public TextView tdt;

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;

    private RelativeLayout relativeLayout;
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

        Calendar c = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        selectedYear=c.get(Calendar.YEAR);
        selectedMonth=c.get(Calendar.MONTH);
        selectedDayOfMonth=c.get(Calendar.DAY_OF_MONTH);
        c.set(selectedYear,selectedMonth,selectedDayOfMonth);

        String datetoshow=dateFormatter.format(c.getTime());

        mView = inflater.inflate(R.layout.sample_fragment2, container, false);

        //String sampleText = getArguments().getString("bString");      GET from args
        relativeLayout=(RelativeLayout)mView.findViewById(R.id.content);

        fdt=(TextView)mView.findViewById(R.id.fromdt);
        fdt.setText(datetoshow);

        tdt=(TextView)mView.findViewById(R.id.todt);
        tdt.setText(datetoshow);

        TextView fbt=(TextView) mView.findViewById(R.id.frombt);
        fbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar newCalendar = Calendar.getInstance();
                dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        fdt.setText(dateFormatter.format(newDate.getTime()));
                    }
                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });

        TextView tbt=(TextView) mView.findViewById(R.id.tobt);
        tbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar newCalendar = Calendar.getInstance();
                dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        tdt.setText(dateFormatter.format(newDate.getTime()));
                    }
                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });

        Button nr=(Button)mView.findViewById(R.id.nr);
        nr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nra=new Intent(getActivity(),newrecord.class);
                startActivity(nra);
            }
        });

        Button chk=(Button)mView.findViewById(R.id.chk);
        chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
                    Intent chka = new Intent(getActivity(), checkrecord.class);
                    chka.putExtra("fromdate", fdt.getText().toString());
                    chka.putExtra("todate", tdt.getText().toString());
                    startActivity(chka);
                }
            }
        });

        return mView;
    }
}
