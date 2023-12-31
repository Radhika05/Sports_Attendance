package com.example.sportsattendance;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class MyCalendar extends DialogFragment {

    Calendar calendar = Calendar.getInstance();


    public onCalendarOkClickListener onCalendarOkClickListener;

    public void setOnCalendarOkClickListener(onCalendarOkClickListener onCalendarOkClickListener){
        this.onCalendarOkClickListener= onCalendarOkClickListener;
    }

    public interface onCalendarOkClickListener {
        void onClick(int year,int month, int day);
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(),((view, year, month, dayOfMonth) -> {
            onCalendarOkClickListener.onClick(year,month,dayOfMonth);
        }),calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
    }

    void setData(int year,int month, int day){
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,day);

    }
    String getData(){
        return DateFormat.format("dd.MM.yyyy",calendar).toString();
    }
}
