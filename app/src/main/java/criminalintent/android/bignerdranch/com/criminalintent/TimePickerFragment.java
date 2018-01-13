package criminalintent.android.bignerdranch.com.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by User on 07.01.2018.
 */

public class TimePickerFragment extends DialogFragment {
    public static final String ARG_TIME = "crime_time";
    public static final String EXTRA_TIME = "com.bignerdranch.android.criminalintent.time";
    TimePicker mTimePicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_TIME);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        mTimePicker = (TimePicker)view.findViewById(R.id.crime_time_time_picker);
        mTimePicker.setCurrentHour(hours);
        mTimePicker.setCurrentMinute(minutes);
        return new AlertDialog.Builder(getActivity()).setView(view).
                setTitle(R.string.time_picker_title).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int hour = 0;
                int minute = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    hour = mTimePicker.getHour();
                    minute = mTimePicker.getMinute();
                }
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                Date date = calendar.getTime();
                sendResult(date);
            }
        }).create();
    }

    private void sendResult(Date time){
        if(getTargetFragment() == null)
            return;
        Intent data = new Intent();
        data.putExtra(EXTRA_TIME, time);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
    }

    public static TimePickerFragment newInstanse(Date date){
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_TIME, date);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}
