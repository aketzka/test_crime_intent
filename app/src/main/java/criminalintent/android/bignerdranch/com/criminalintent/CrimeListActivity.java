package criminalintent.android.bignerdranch.com.criminalintent;

import android.support.v4.app.Fragment;
import android.view.Menu;

import java.util.UUID;

/**
 * Created by User on 26.12.2017.
 */

public class CrimeListActivity extends SingleFragmentActivity {

    protected Fragment createFragment () {

        return new CrimeListFragment();
    }
}
