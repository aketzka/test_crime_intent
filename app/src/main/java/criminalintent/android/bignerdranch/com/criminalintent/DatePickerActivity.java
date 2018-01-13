package criminalintent.android.bignerdranch.com.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.Date;

/**
 * Created by User on 07.01.2018.
 */

public class DatePickerActivity extends SingleFragmentActivity {
    private static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date_picker_activity_date";
    @Override
    protected Fragment createFragment() {
        Date date = (Date)getIntent().getSerializableExtra(EXTRA_DATE);
        return DatePickerFragment.newInstance(date);
    }

    public static Intent newIntent(Context context, Date date){
        Intent intent = new Intent(context, DatePickerActivity.class);
        intent.putExtra(EXTRA_DATE, date);
        return intent;
    }
}
