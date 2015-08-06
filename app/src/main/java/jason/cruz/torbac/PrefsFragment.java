package jason.cruz.torbac;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.test.PerformanceTestCase;
import android.widget.Toast;

public class PrefsFragment extends PreferenceFragment {

	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	  // TODO Auto-generated method stub
	  super.onCreate(savedInstanceState);
	  
		File sdCard = Environment.getExternalStorageDirectory();
       	File directory = new File (sdCard.getAbsolutePath() +
       	"/MyFiles/");
	  
	  // Load the preferences from an XML resource
	        addPreferencesFromResource(R.xml.preferences);
	        
	        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
	  	  
	  	  String pref_BaseFolder = mySharedPreferences.getString("base_preference", "" + directory.toString());
	  	  Preference setSummaryPref_Base = (Preference) findPreference("base_preference");
	  	  setSummaryPref_Base.setSummary("" + pref_BaseFolder);
	  	 Preference setSummaryPref_Role = (Preference) findPreference("role_preference");
	  	setSummaryPref_Role.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	  		 
		    public boolean onPreferenceClick(Preference preference) {
		    	startActivity(new Intent("cruz.jason.rkey_generate"));
		    return true;
		    }
	 });
	 
	 }
	 
}