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

public class RKeyGenerate extends ActionBarActivity implements EditNameDialogListener {
	
	Button bt_Calculate;
	EditText et_Append;
	TextView tv_Status, tv_Calculating, tv_RoleName;
	String baseFileName, basePath, baseContent, parentFileName, parentPath, parentContent;
	HIBEEngine	hengine, HE;
	HIBEKey	hkey, pkey, HK, key;
	FragmentManager fm;
	String base_complete, parent_complete, univ_roleName;
	int flag_base, flag_parent, flag_save;
	Spinner spinner_base, spinner_parent;
	 private static final int READ_BLOCK_SIZE = 100;
	 String s="";
	 String s2="";
	 List<String> parent_list, base_list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rkey_generate);
		
		et_Append = (EditText)findViewById(R.id.et_Append);
		tv_Status = (TextView)findViewById(R.id.txt_Status);
		tv_Calculating = (TextView)findViewById(R.id.txt_Calculating);
		tv_RoleName = (TextView)findViewById(R.id.tv_RoleName);
		bt_Calculate= (Button)findViewById(R.id.bt_Calculate);
		spinner_base = (Spinner)findViewById(R.id.spinner_base);
		spinner_parent= (Spinner)findViewById(R.id.spinner_parent);
		
		setTitle("RKey Generate");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		populate_spinner_base();
		populate_spinner_parent();
		
		fm = getSupportFragmentManager();
		flag_base = 0;
		flag_parent = 0;
		flag_save = 0;
		


		
		bt_Calculate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (flag_base == 1 && flag_parent == 1)
				{
					if (et_Append.getText().toString().equals(""))
					{	
						tv_Status.setText("Enter name of sub-role to be created.");
					}
					else
					{
						try {
							pkey = hengine.readKeyFromFile(parent_list.get(0).toString());
							key = hengine.generateKey(et_Append.getText().toString(), pkey);
							tv_Calculating.setText(key.toString());
							tv_Status.setText("Sub-role Created. You can now save this file.");
							flag_save = 1;
						} catch (RuntimeException ee) {
							tv_Status.setText("Try Again.");
						}
					}
			}
			else
			{
				tv_Status.setText(tv_Status.getText().toString());
			}
			}
		});
	}
	
	private void populate_spinner_base() {
		
		base_list = new ArrayList<String>();
		File sdCard = Environment.getExternalStorageDirectory();
       	File directory = new File (sdCard.getAbsolutePath() +
       	"/MyFiles/");
		File[] sdDirList = directory.listFiles();
	     for (int i = 0; i < sdDirList.length; i++) {
	    	 if(sdDirList[i].getName().contains(".base.txt"))
	    	    {
	    		  base_list.add(sdDirList[i].toString());
	    	    }
	    }
	     ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_design, base_list);
	     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     spinner_base.setAdapter(adapter);
	     spinner_base.setOnItemSelectedListener(
	                new AdapterView.OnItemSelectedListener() {
	                    @Override
	                    public void onItemSelected(AdapterView<?> arg0, View arg1,
	                            int arg2, long arg3) {
	                        int position = spinner_base.getSelectedItemPosition();
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
	                    	if (!s.contains("seed")) {
		                		try {
		    						hengine = new HIBEEngine(base_list.get(position).toString());
		    						tv_Status.setText("Base file valid.");
		    						flag_base = 1;
		    						if (flag_base == 1 && flag_parent == 1)
		    						{
		    							tv_Status.setText(tv_Status.getText().toString() + "\nEnter a Role Name and Press Calculate.");
		    						}
		    					} catch (RuntimeException ee) {
		    						tv_Status.setText("Chosen file is not a base file. Try again.");
		    						flag_base = 0;
		    					}
	                    	}
	                    	if (s.contains("seed")) {
	  							tv_Status.setText("The chosen file is not a base file. Try again.");
	  								flag_base = 1;
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

	private void populate_spinner_parent() {
		// TODO Auto-generated method stub
		parent_list = new ArrayList<String>();
		File sdCard2 = Environment.getExternalStorageDirectory();
       	File directory2 = new File (sdCard2.getAbsolutePath() +
       	"/MyFiles/");
		File[] sdDirList2 = directory2.listFiles();
	     for (int i = 0; i < sdDirList2.length; i++) {
	    	 if(sdDirList2[i].getName().contains(".rkey.txt"))
	    	    {
	    		 parent_list.add(sdDirList2[i].toString());
	    	    }
	    }
	     ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.spinner_design, parent_list);
	     adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     spinner_parent.setAdapter(adapter2);
	     spinner_parent.setOnItemSelectedListener(
	                new AdapterView.OnItemSelectedListener() {
	                    @Override
	                    public void onItemSelected(AdapterView<?> arg0, View arg1,
	                            int arg2, long arg3) {
	                        int position2 = spinner_parent.getSelectedItemPosition();
	                        try{
	                        File file2 = new File(parent_list.get(position2).toString());
	            	    	FileInputStream fIn2 = new FileInputStream(file2);
	            	        InputStreamReader isr2 = new InputStreamReader(fIn2);
	                    	char[] inputBuffer2 = new char[READ_BLOCK_SIZE];
	                    	s2 = "";
	                    	int charRead2;
	                    	while ((charRead2 = isr2.read(inputBuffer2))>0)
	                    	{
	                    	String readString2 = String.copyValueOf(inputBuffer2, 0, charRead2);
	                    	s2 += readString2;
	                    	inputBuffer2 = new char[READ_BLOCK_SIZE];
	                    	}
	                    	
	                    	hengine = new HIBEEngine(base_list.get(0).toString());
	    					pkey = new HIBEKey();
	    					pkey = hengine.readKeyFromFile(parent_list.get(position2).toString());
	    					univ_roleName = pkey.hid.toDotForm();
	    					tv_RoleName.setText(pkey.hid.toDotForm() + ".");
	    					tv_Status.setText(tv_Status.getText().toString() + " Parent Key File Valid.");
	    					flag_parent = 1;
	    					if (flag_base == 1 && flag_parent == 1)
	    					{
	    						tv_Status.setText(tv_Status.getText().toString() + "\nEnter a Role Name and Press Calculate.");
	    					}
	                    	              	
	                  		} catch (RuntimeException ee) {
	        					tv_Status.setText(tv_Status.getText().toString() + "\nThe chosen file is not a key file. Try again.");
	        					flag_parent = 0;
	        				
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



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_rkeygenerate, menu);
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
			args.putString("file_name", univ_roleName + "-" + et_Append.getText().toString());
			DFragment editNameDialog = new DFragment();
			editNameDialog.setArguments(args);
	    	editNameDialog.show(fm, "file_saver");	
			}
			else
			{
				tv_Status.setText(tv_Status.getText().toString() + "\nTry Again.");
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
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			finish();
		}
		return false;
	}
}
