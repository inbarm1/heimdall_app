package com.example.inbar.heimdall;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by idan on 10/01/2018.
 */

public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        private int day=1;
        private int month=1;

    public int getDay() {
        return day;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {

        return month+1;
    }

    private int year=1990;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
//            final Calendar c = Calendar.getInstance()

//            int day=c.get(Calendar.DAY_OF_MONTH);
//            int month = c.get(Calendar.MONTH);
//            int year = c.get(Calendar.YEAR);

            // Create a new instance of DatePicker and return it
            //new DatePickerDialog(getActivity(), this, year, month, day);
            return new DatePickerDialog(getActivity(),R.style.DialogTheme,this,year,month,day);
        }


    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int i, int i1, int i2) {
            TextView dateText=getActivity().findViewById(R.id.birth_date_selection);
            day=datePicker.getDayOfMonth();
            month=datePicker.getMonth();
            year=datePicker.getYear();
            dateText.setText(String.format("%d/%d/%d",datePicker.getDayOfMonth(),datePicker.getMonth()+1,datePicker.getYear()));
        }
    }
