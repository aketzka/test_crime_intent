package criminalintent.android.bignerdranch.com.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.zip.Inflater;

/**
 * Created by aketza on 21.12.17.
 */

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mSuspectButton;
    private Button mSendReportButton;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mTimeButton;
    private Button mCallButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID)getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_delete_crime:
                getActivity().finish();
                CrimeLab.get(getActivity()).deleteCrime(mCrime.getId());
                returnResult();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_crime, container, false);;
        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                    // Здесь намеренно оставлено пустое место
            }
            @Override
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mCrime.setTitle(c.toString());
                returnResult();
            }
            @Override
            public void afterTextChanged(Editable c) {
                    // И здесь тоже
            }

        });

        mDateButton = (Button)v.findViewById(R.id.crime_date);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent datePickerActivity = DatePickerActivity.newIntent(getActivity(), mCrime.getDate());
                startActivityForResult(datePickerActivity, REQUEST_DATE);
            }
        });
        updateDate();
        mTimeButton = (Button)v.findViewById(R.id.crime_time);
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerFragment dialog = TimePickerFragment.newInstanse(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(getFragmentManager(), DIALOG_TIME);
            }
        });
        updateTime();
        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Назначение флага раскрытия преступления
                mCrime.setSolved(isChecked);
                returnResult();
            }
        });
        final Intent pickIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button)v.findViewById(R.id.choose_suspect_button);
        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickIntent, PackageManager.MATCH_DEFAULT_ONLY) == null)
            mSuspectButton.setEnabled(false);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              startActivityForResult(pickIntent, REQUEST_CONTACT);
            }
        });
        if(mCrime.getSuspect() != null)
            mSuspectButton.setText(mCrime.getSuspect());

        mSendReportButton = (Button)v.findViewById(R.id.send_report_button);
        mSendReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ShareCompat.IntentBuilder.from(getActivity()).setChooserTitle(R.string.send_report)
                        .setType("text/plain").setText(getCrimeReport()).setSubject(String.valueOf(R.string.crime_report_subject)).getIntent();
                startActivity(intent);
            }
        });

        mCallButton = (Button)v.findViewById(R.id.make_call_button);
        if(mCrime.getSuspect() == null)
            mCallButton.setEnabled(false);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCrime.getSuspect() == null)
                    return;
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED)
                    return;
                String suspect = mCrime.getSuspect();
                Cursor result = null;
                Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String[] queryFields = new String[] {
                        ContactsContract.Data.DISPLAY_NAME,
                        ContactsContract.Data.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                };
                String where = ContactsContract.Data.DISPLAY_NAME + "= ?";
                //TODO: read about queries in getContentResolver
                result = getActivity().getContentResolver().query(uri, queryFields, where, new String[] {suspect}, null);
                result.moveToFirst();
                String phone = result.getString(2);

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        });
        return v;
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        DateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd");
        String dateString = dateFormat.format(mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }
    public void returnResult(){
        getActivity().setResult(Activity.RESULT_OK, getActivity().getIntent());
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_DATE && resultCode == Activity.RESULT_OK) {
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            returnResult();
            updateDate();
        }
        if(requestCode == REQUEST_TIME && resultCode == Activity.RESULT_OK) {
            Date date = (Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(date);
            returnResult();
            updateTime();
        }

        if(requestCode == REQUEST_CONTACT && resultCode == Activity.RESULT_OK && data != null) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[] {ContactsContract.Contacts.DISPLAY_NAME};
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields,
                    null, null, null);
            try{
                if(c.getCount() == 0){
                    c.close();
                    return;
                }
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
                mCallButton.setEnabled(true);
            } finally {
                c.close();
            }
        }
    }

    private void updateDate() {
        DateFormat df = new SimpleDateFormat("EEEE, MMM dd, yyyy");
        mDateButton.setText(df.format(mCrime.getDate()));
    }

    private void updateTime() {
        DateFormat df = new SimpleDateFormat("HH:mm");
        mTimeButton.setText(df.format(mCrime.getDate()));
    }

}
