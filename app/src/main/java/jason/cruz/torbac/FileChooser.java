package jason.cruz.torbac;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class FileChooser extends ListActivity {

	private File currentDir;
    private FileArrayAdapter adapter;
    private static final int READ_BLOCK_SIZE = 100;
    String s="";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        currentDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyFiles");
        fill(currentDir); 
    }
    private void fill(File f)
    {
    	File[]dirs = f.listFiles(); 
		 this.setTitle("Current Dir: "+f.getName());
		 List<Item>dir = new ArrayList<Item>();
		 List<Item>fls = new ArrayList<Item>();
		 try{
			 for(File ff: dirs)
			 { 
				Date lastModDate = new Date(ff.lastModified()); 
				DateFormat formater = DateFormat.getDateTimeInstance();
				String date_modify = formater.format(lastModDate);
				if(ff.isDirectory()){
					
					
					File[] fbuf = ff.listFiles(); 
					int buf = 0;
					if(fbuf != null){ 
						buf = fbuf.length;
					} 
					else buf = 0; 
					String num_item = String.valueOf(buf);
					if(buf == 0) num_item = num_item + " item";
					else num_item = num_item + " items";
					
					//String formated = lastModDate.toString();
					dir.add(new Item(ff.getName(),num_item,date_modify,ff.getAbsolutePath(),"directory_icon")); 
				}
				else
				{
					
					fls.add(new Item(ff.getName(),ff.length() + " Byte", date_modify, ff.getAbsolutePath(),"file_icon"));
				}
			 }
		 }catch(Exception e)
		 {    
			 
		 }
		 Collections.sort(dir);
		 Collections.sort(fls);
		 dir.addAll(fls);
		 if(!f.getName().equalsIgnoreCase("sdcard"))
			 dir.add(0,new Item("..","Parent Directory","",f.getParent(),"directory_up"));
		 adapter = new FileArrayAdapter(FileChooser.this,R.layout.file_view,dir);
		 this.setListAdapter(adapter); 
    }
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Item o = adapter.getItem(position);
		if(o.getImage().equalsIgnoreCase("directory_icon")||o.getImage().equalsIgnoreCase("directory_up")){
				currentDir = new File(o.getPath());
				fill(currentDir);
		}
		else
		{
			onFileClick(o);
		}
	}
    private void onFileClick(Item o)
    {

		try {
			
	        File file = new File(currentDir.toString(), o.getName());

	    	FileInputStream fIn = new FileInputStream(file);
	        InputStreamReader isr = new InputStreamReader(fIn);
	        
        	char[] inputBuffer = new char[READ_BLOCK_SIZE];
        	s = "";
        	int charRead;
        	while ((charRead = isr.read(inputBuffer))>0)
        	{
        	//---convert the chars to a String---
        	String readString = String.copyValueOf(inputBuffer, 0, charRead);
        	s += readString;
        	inputBuffer = new char[READ_BLOCK_SIZE];
        	}
        	//---set the EditText to the text that has been read---
        //	textBox.setText(s);
        	Toast.makeText(getBaseContext(), "File loaded successfully!",
        	Toast.LENGTH_SHORT).show();


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	Toast.makeText(this, "Folder Clicked: "+ currentDir, Toast.LENGTH_SHORT).show();
    	Intent intent = new Intent();
        intent.putExtra("GetPath",currentDir.toString());
        intent.putExtra("GetFileName",o.getName());
        intent.putExtra("Content", s);
        setResult(RESULT_OK, intent);
        finish();
    }
}
