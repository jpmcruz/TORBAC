package jason.cruz.torbac;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import jason.cruz.torbac.DFragment.EditNameDialogListener;


public class RKeyGenerateMaster extends ActionBarActivity implements EditNameDialogListener {
	
	Button bt_Calculate;
	EditText et_RoleName, et_MasterFile;
	TextView tv_Status, tv_Calculating;
    private static final int REQUEST_PATH = 1;
    String curFileName, curPath, curContent, complete;
    HIBEEngine	HE;
    HIBEKey key;
	FragmentManager fm;
	int flag_master, flag_save;
	Spinner spinner_master;
	 private static final int READ_BLOCK_SIZE = 100;
	 String s="";
	 List<String> base_list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rkey_generate_master);
		fm = getSupportFragmentManager();
	
		et_RoleName = (EditText)findViewById(R.id.et_RoleName);
		tv_Status = (TextView)findViewById(R.id.txt_Status);
		tv_Calculating = (TextView)findViewById(R.id.txt_Calculating);
		bt_Calculate= (Button)findViewById(R.id.bt_Calculate);
		spinner_master = (Spinner)findViewById(R.id.spinner_master);
		flag_master = 0;
		flag_save = 0;
		
		setTitle("RKey Generate Master");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		populate_spinner_base();
		
		bt_Calculate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((flag_master == 0) || et_RoleName.getText().toString().equals(""))
				{
					tv_Status.setText("Make sure the Master file is chosen, and you have entered Role Name.");
				}
				else
				{
					try {
						key = HE.generateKey(et_RoleName.getText().toString());
						tv_Calculating.setText(key.toString());
						tv_Status.setText("Key calculated. You may now save the master key.");
						flag_save = 1;
					} catch (RuntimeException ee) {
						tv_Status.setText("Try again.");
					}
				}
			}
		});

	}
	
	private void populate_spinner_base() {
		// TODO Auto-generated method stub
		base_list = new ArrayList<String>();
		File sdCard = Environment.getExternalStorageDirectory();
       	File directory = new File (sdCard.getAbsolutePath() +
       	"/MyFiles/");
		File[] sdDirList = directory.listFiles();
	     for (int i = 0; i < sdDirList.length; i++) {
	    	 if(sdDirList[i].getName().contains(".master.txt"))
	    	    {
	    		  base_list.add(sdDirList[i].toString());
	    	    }
	    }
	     ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_design, base_list);
	     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     spinner_master.setAdapter(adapter);
	     spinner_master.setOnItemSelectedListener(
	                new AdapterView.OnItemSelectedListener() {
	                    @Override
	                    public void onItemSelected(AdapterView<?> arg0, View arg1,
	                            int arg2, long arg3) {
	                        int position = spinner_master.getSelectedItemPosition();
	                        try{
	                        File file = new File(base_list.get(position).toString());
	            	    	FileInputStream fIn = new FileInputStream(file);
	            	        InputStreamReader isr = new InputStreamReader(fIn);
	                    	char[] inputBuffer = new char[READ_BLOCK_SIZE];
	                    	s = "";
	                    	int charRead;
	                    	while ((charRead = isr.read(inputBuffer))>0)
	                    	{
	                    	String readString = String.copyValueOf(inputBuffer, 0, charRead);
	                    	s += readString;
	                    	inputBuffer = new char[READ_BLOCK_SIZE];
	                    	}          	            	
	                    	
	                    	if (s.contains("seed")) {
		                		try {
		    						HE = new HIBEEngine(base_list.get(position).toString());
		    						tv_Status.setText("Master file valid. Enter a role name and press the Calculate Key button.");
		    						flag_master = 1;
		    					} catch (RuntimeException ee) {
		    						tv_Status.setText("Chosen file is not a master file. Try again.");
		    					}
	                    	}
	                    	if (!s.contains("seed")) {
	  							tv_Status.setText("The chosen file is not a master file. Try again.");
	  								flag_master = 0;
	  						} 	  
	                		} catch (FileNotFoundException e) {
	                			e.printStackTrace();
	                		} catch (IOException e) {
	                			e.printStackTrace();
	                		}
	                    }
	                    @Override
	                    public void onNothingSelected(AdapterView<?> arg0) {
	                    }
	                }
	            );
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
	           	if (string_type.equals("key"))
	           	{
		           	osw.write(key.toString());
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
		getMenuInflater().inflate(R.menu.menu_rkeygeneratemaster, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
         
        switch (item.getItemId())
        {
        case R.id.save_rkey:
        if (flag_save == 1)
		{
			Bundle args = new Bundle();
			args.putString("type", "key");
			DFragment editNameDialog = new DFragment();
			editNameDialog.setArguments(args);
	    	editNameDialog.show(fm, "file_saver");
		}
		else
		{
			tv_Status.setText("Master key not yet created.");
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
