package com.example.inbar.heimdall.Law;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.TextView;

import com.example.inbar.heimdall.R;

import java.util.Calendar;

/**
 * Created by ramhillel on 14/01/18.
 */

public class LawDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public Calendar c = Calendar.getInstance();
    private int day = c.get(Calendar.DAY_OF_MONTH);
    private int month = c.get(Calendar.MONTH);
    private int year = c.get(Calendar.YEAR);
    private boolean isStart = true;

    public int getDay() {
        return day;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {

        return month+1;
    }

    public void setToDate() {
        this.isStart = false;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), R.style.DialogTheme,this,year,month,day);
    }


    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int i, int i1, int i2) {
        TextView dateText = (TextView) getActivity().findViewById(isStart? R.id.fromDateText:R.id.toDateText);
        day=datePicker.getDayOfMonth();
        month=datePicker.getMonth();
        year=datePicker.getYear();
        c.set(year, month, day);
        dateText.setText(String.format("%d/%d/%d",datePicker.getDayOfMonth(),datePicker.getMonth()+1,datePicker.getYear()));
    }
}
