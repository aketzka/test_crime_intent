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
    private List<Crime> mCrimes;

    public static CrimeLab get(Context context){
        if (sCrimeLab == null)
            sCrimeLab = new CrimeLab(context);
        return sCrimeLab;
    }

    private CrimeLab(Context context){
            mCrimes = new ArrayList<>();
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
