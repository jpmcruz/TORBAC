package jason.cruz.torbac;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.app.ActionBar;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import jason.cruz.torbac.DFragment.EditNameDialogListener;
import android.support.v7.app.ActionBarActivity;

public class GenParams extends ActionBarActivity implements EditNameDialogListener {
	
	Button bt_GenParams, bt_MasterSave, bt_BaseSave;
	EditText et_Qbits, et_Rbits;
	TextView tv_Status;
	HIBEEngine	HE; 
	int rbits, qbits, flag;
	FragmentManager fm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gen_params);
		fm = getSupportFragmentManager();
		
		et_Qbits = (EditText)findViewById(R.id.et_Qbits);
		et_Rbits = (EditText)findViewById(R.id.et_Rbits);
		tv_Status = (TextView)findViewById(R.id.txt_Status);
		bt_GenParams = (Button)findViewById(R.id.bt_GenParams);
		bt_MasterSave = (Button)findViewById(R.id.bt_MasterSave);
		bt_BaseSave = (Button)findViewById(R.id.bt_BaseSave);
		flag = 0;
		
		setTitle("Generate Parameters");
//        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		bt_GenParams.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					rbits = Integer.parseInt(et_Rbits.getText().toString());
					qbits = Integer.parseInt(et_Qbits.getText().toString());
					if ((rbits <= 0) || (qbits <= 0)) throw(new NumberFormatException());
					HE = new HIBEEngine(rbits, qbits);
					tv_Status.setText("Keys Created. You can now save the master and base keys.");	
					flag = 1;
				} catch (NumberFormatException ee) {
					tv_Status.setText("Try again.");
				}
			}
		});
		
		bt_MasterSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (flag == 0)
				{
					tv_Status.setText("Keys not yet created. Enter positive integers on the rbits and qbits and Press the Create Button.");
				}
				else
				{
					Bundle args = new Bundle();
					args.putString("type", "master");
					DFragment editNameDialog = new DFragment();
					editNameDialog.setArguments(args);
			    	editNameDialog.show(fm, "file_saver");
				}
			}
		});
		
		bt_BaseSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (flag == 0)
				{
					tv_Status.setText("Keys not yet created. Enter positive integers on the rbits and qbits and Press the Create Button.");
				}
				else
				{
					Bundle args = new Bundle();
					args.putString("type", "base");
					DFragment editNameDialog = new DFragment();
					editNameDialog.setArguments(args);
			    	editNameDialog.show(fm, "file_saver");
				}
			}
		});
	}
	
	public void onFinishEditDialog(String inputText, String string_type) {
		try
	     {
	        	//---SD Card Storage---
	     		File sdCard = Environment.getExternalStorageDirectory();
	           	File directory = new File (sdCard.getAbsolutePath() +
	           	"/MyFiles");
	          	directory.mkdirs();
	           	File file = new File(directory, inputText);
	           	FileOutputStream fOut = new FileOutputStream(file);
	           	OutputStreamWriter osw = new
	           	OutputStreamWriter(fOut);
	           	//---write the string to the file---
	           	if (string_type.equals("master"))
	           	{
		           	osw.write(HE.toString());
		           	osw.flush();
		            osw.close();
		            //---display file saved message---
				     Toast.makeText(getBaseContext(), "File saved successfully!",
				     Toast.LENGTH_LONG).show();
	           	}
	           	if (string_type.equals("base"))
	           	{
		           	osw.write(HE.toStringPublic());
		           	osw.flush();
		            osw.close();
		            //---display file saved message---
				     Toast.makeText(getBaseContext(), "File saved successfully!",
				     Toast.LENGTH_LONG).show();
	           	}
	     }
	     catch (IOException ioe)
	     {
	     ioe.printStackTrace();
	     }
	 }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_genparams, menu);
        return super.onCreateOptionsMenu(menu);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
         
        switch (item.getItemId())
        {
        case R.id.save_master:
        	if (flag == 0)
			{
				tv_Status.setText("Keys not yet created. Enter positive integers on the rbits and qbits and Press the Create Button.");
			}
			else
			{
				Bundle args = new Bundle();
				args.putString("type", "master");
				DFragment editNameDialog = new DFragment();
				editNameDialog.setArguments(args);
		    	editNameDialog.show(fm, "file_saver");
			}
        	return true;
        case R.id.save_base:
        	if (flag == 0)
			{
				tv_Status.setText("Keys not yet created. Enter positive integers on the rbits and qbits and Press the Create Button.");
			}
			else
			{
				Bundle args = new Bundle();
				args.putString("type", "base");
				DFragment editNameDialog = new DFragment();
				editNameDialog.setArguments(args);
		    	editNameDialog.show(fm, "file_saver");
			}
        	return true;
        	
        case R.id.settings:
            Toast.makeText(getBaseContext(), "Settings", Toast.LENGTH_SHORT).show();
            return true;
            
        case android.R.id.home:
        	finish();
            return true;
        

        default:
            return super.onOptionsItemSelected(item);
        }
    }    
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			finish();
		}
		return false;
	}
}
