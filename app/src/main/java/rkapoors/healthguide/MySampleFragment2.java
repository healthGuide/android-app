package rkapoors.healthguide;

/**
 * Created by KAPOOR's on 09-09-2017.
 */

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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
        selectedYear=c.get(Calendar.YEAR);
        selectedMonth=c.get(Calendar.MONTH);
        selectedDayOfMonth=c.get(Calendar.DAY_OF_MONTH);

        String datetoshow=Integer.toString(selectedDayOfMonth)+"-"+Integer.toString(selectedMonth+1)+"-"+Integer.toString(selectedYear);

        mView = inflater.inflate(R.layout.sample_fragment2, container, false);

        //String sampleText = getArguments().getString("bString");      GET from args

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
                Intent chka=new Intent(getActivity(),checkrecord.class);
                startActivity(chka);
            }
        });

        return mView;
    }
}
