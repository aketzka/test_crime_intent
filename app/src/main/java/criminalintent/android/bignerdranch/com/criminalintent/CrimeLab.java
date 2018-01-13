package criminalintent.android.bignerdranch.com.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by aketza on 22.12.17.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mAppContext;
    private List<Crime> mCrimes;

    public static CrimeLab get(Context context){
        if (sCrimeLab == null)
            sCrimeLab = new CrimeLab(context);
        return sCrimeLab;
    }

    private CrimeLab(Context context){
            mCrimes = new ArrayList<>();
            mAppContext = context;
            for(int i = 0; i < 5; i++){
                Crime crime = new Crime();
                crime.setTitle("Crime #" + i);
                crime.setSolved(i%3 == 0);
                mCrimes.add(crime);
            }
    }

    public void addCrime(Crime crime){
        mCrimes.add(crime);
    }

    public List<Crime> getCrimes(){
        return mCrimes;
    }

    public Crime getCrime(UUID crimeID){
        for(Crime crime : mCrimes){
            if (crime.getId().equals(crimeID))
                return crime;
        }
        return null;
    }

}
