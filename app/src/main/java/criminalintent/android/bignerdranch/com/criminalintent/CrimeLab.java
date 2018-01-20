package criminalintent.android.bignerdranch.com.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import criminalintent.android.bignerdranch.com.criminalintent.database.CrimeBaseHelper;
import criminalintent.android.bignerdranch.com.criminalintent.database.CrimeDbSchema;
import criminalintent.android.bignerdranch.com.criminalintent.database.CrimeDbSchema.CrimeTable.Cols;

/**
 * Created by aketza on 22.12.17.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDataBase;

    public static CrimeLab get(Context context){
        if (sCrimeLab == null)
            sCrimeLab = new CrimeLab(context);
        return sCrimeLab;
    }

    private CrimeLab(Context context){
            mContext = context.getApplicationContext();
            CrimeBaseHelper cbh = new CrimeBaseHelper(mContext);
            mDataBase = cbh.getWritableDatabase();
    }

    public void addCrime(Crime crime){
        ContentValues values = getContentValues(crime);
        mDataBase.insert(CrimeDbSchema.CrimeTable.NAME, null, values);
    }

    public void deleteCrime(UUID crimeID){
    }


    public List<Crime> getCrimes(){
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return crimes;
    }

    public Crime getCrime(UUID crimeID){
        CrimeCursorWrapper cursor = queryCrimes(Cols.UUID + " = ?", new String[] {crimeID.toString()});
        Crime crime = null;
        try{
            if (cursor.getCount() == 0) return null;
            cursor.moveToFirst();
            crime = cursor.getCrime();
        }finally {
            cursor.close();
        }

        return crime;
    }

    public ContentValues getContentValues(Crime crime){
        ContentValues values = new ContentValues();
        values.put(Cols.UUID, crime.getId().toString());
        values.put(Cols.TITLE, crime.getTitle());
        values.put(Cols.DATE, crime.getDate().getTime());
        values.put(Cols.SOLVED, crime.isSolved()? 1 : 0);
        values.put(Cols.SUSPECT, crime.getSuspect());

        return values;
    }

    public void updateCrime(Crime crime){
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);
        mDataBase.update(CrimeDbSchema.CrimeTable.NAME, values, Cols.UUID + " = ?", new String[] {uuidString});
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mDataBase.query(CrimeDbSchema.CrimeTable.NAME, null, whereClause, whereArgs, null, null, null);
        return new CrimeCursorWrapper(cursor);
    }

    public File getPhotoFile(Crime crime){
        File externalFileDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if(externalFileDir == null)
            return null;

        return new File(externalFileDir, crime.getPhotoFilename());
    }
}
