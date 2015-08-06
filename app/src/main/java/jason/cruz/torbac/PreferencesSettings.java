package jason.cruz.torbac;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class PreferencesSettings extends Activity {

 @Override
 protected void onCreate(Bundle savedInstanceState) {
  // TODO Auto-generated method stub
  super.onCreate(savedInstanceState);

  getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
 }

}