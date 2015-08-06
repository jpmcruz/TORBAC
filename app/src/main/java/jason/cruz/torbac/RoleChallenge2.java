package jason.cruz.torbac;

import it.unisa.dia.gas.jpbc.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RoleChallenge2 extends ActionBarActivity implements CreateNdefMessageCallback,
OnNdefPushCompleteCallback {
	
	Button bt_Challenge;
	EditText et_Base, et_RoleName;
	TextView tv_Status, tv_ChallengeData, tv_ResponseData;
    String baseFileName, basePath, baseContent, baseComplete;
	HIBEEngine	hengine;
	HIBECipher hcipher;
	Element msg;
	ElementWrapper	answer;
	FragmentManager fm;
	String upc = "";
	ImageView iv_QRImage;
	int flag_base, flag_challenge, flag_response, flag_create;
	Spinner spinner_base;
	public static final String MIME_TEXT_PLAIN = "text/plain";
	String pref_BaseFolder;
	File sdCard, directory;

	 private static final int READ_BLOCK_SIZE = 100;
	 String s="";
		private static final String TAG = "stickynotes";
	    private boolean mResumed = false;
	    private boolean mWriteMode = false;
	    NfcAdapter mNfcAdapter;
	  //  EditText mNote;
	    private static final int MESSAGE_SENT = 1;
	    PendingIntent mNfcPendingIntent;
	    IntentFilter[] mWriteTagFilters;
	    IntentFilter[] mNdefExchangeFilters;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.role_challenge2);
		
		setTitle("Role Challenge");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		fm = getSupportFragmentManager();
		et_Base = (EditText)findViewById(R.id.et_Base);
		et_RoleName = (EditText)findViewById(R.id.et_roleName);
		tv_Status = (TextView)findViewById(R.id.txt_Status);
		tv_ChallengeData = (TextView)findViewById(R.id.tv_ChallengeData);
		tv_ResponseData = (TextView)findViewById(R.id.tv_ResponseData);
		tv_ChallengeData.addTextChangedListener(mTextWatcher);
		tv_ResponseData.addTextChangedListener(mTextWatcher);
		bt_Challenge = (Button)findViewById(R.id.bt_Challenge);
		spinner_base = (Spinner)findViewById(R.id.spinner_base);
		iv_QRImage = (ImageView) findViewById(R.id.iv_QRChallenge);
		answer = new ElementWrapper();
		flag_base = 0;
		flag_challenge = 0;
		flag_response = 0;
		flag_create = 0;
		sdCard = Environment.getExternalStorageDirectory();
       	directory = new File (sdCard.getAbsolutePath() +
       	"/MyFiles/");
		loadPref();
		
		
		populate_spinner_base();
		
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
	       
        
		 mNfcPendingIntent = PendingIntent.getActivity(this, 0,
	                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

	        // Intent filters for reading a note from a tag or exchanging over p2p.
	        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
	        try {
	            ndefDetected.addDataType("text/plain");
	        } catch (MalformedMimeTypeException e) { }
	        mNdefExchangeFilters = new IntentFilter[] { ndefDetected };

	        // Intent filters for writing to a tag
	        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
	        mWriteTagFilters = new IntentFilter[] { tagDetected };
			
		bt_Challenge.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((flag_base == 0)||(et_RoleName.getText().toString().equals("")))
				{
					tv_Status.setText("Choose a valid Base File and/or Enter Role Name to Challenge.");
					flag_challenge = 0;
				}
				else
				{
				try {
					msg = hengine.pairing.getGT().newRandomElement();
					answer.elm = msg.duplicate();
					HIBECipher hcipher = hengine.encrypt(et_RoleName.getText().toString(), msg);
					tv_ChallengeData.setText(hcipher.toString());
					tv_Status.setText("Send the Challenge Data via NFC or Create a QR Code for scanning.");
					flag_challenge = 1;
				} catch (RuntimeException ee) {
					tv_Status.setText("Try Again.");
				}	
				}
			}
		});
	}
	
	private void populate_spinner_base() {
		tv_Status.setText("Choose a base file.");
		final List<String> base_list = new ArrayList<String>();
		
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
		    						tv_Status.setText("Base file valid. Type a role name to challenge.");
		    						flag_base = 1;
		    					} catch (RuntimeException ee) {
		    						tv_Status.setText("Chosen file is not a base file. Try again.");
		    					}
	                    	}
	                    	if (s.contains("seed")) {
	  							tv_Status.setText("The chosen file is not a base file. Try again.");
	  								flag_base = 0;
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

	// Listen for results.
	 @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
	     // See which child activity is calling us back.
	     
	     if (requestCode == IntentIntegrator.REQUEST_CODE){
	 	    // IntentIntegrator.REQUEST_CODE
	 	     if (resultCode != RESULT_CANCELED) {
	 				IntentResult scanResult = IntentIntegrator.parseActivityResult(
	 						requestCode, resultCode, data);
	 				if (scanResult != null) {
	 					upc = scanResult.getContents();
	 					tv_ResponseData.setText(upc);
	 					flag_response = 1;
	 			        tv_Status.setText("You can now Verify the data.");
	 				}
	 			}
	 	     }
	     if (requestCode == 0){
		 	    // IntentIntegrator.REQUEST_CODE
		 	    
		 	    	 loadPref();
		 		
		 	     }
	     
	 }

	private void loadPref() {
		// TODO Auto-generated method stub
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		  
		pref_BaseFolder = mySharedPreferences.getString("base_preference", "" + directory.toString());
		tv_Status.setText(pref_BaseFolder);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_challenge, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
         
        switch (item.getItemId())
        {
        case R.id.check:
        	if ((tv_ResponseData.getText().toString().equals("")|| flag_response==0))
			{
        		tv_Status.setText(tv_Status.getText().toString() + "\nGenerate Response and Callenge Data First.");  		
			}
			else
			{
				try {
					msg = hengine.pairing.getGT().newRandomElement();
					msg.setFromBytes(ByteArrayHex.toByteArray(tv_ResponseData.getText().toString()));
					if (msg.equals(answer.elm)) {
						tv_Status.setText("Role is Verified as a MATCH.");
						alertjason(1);
					} else {
						tv_Status.setText("Role is a MISMATCH. Try Again.");	
						alertjason(2);
					}
				} catch (RuntimeException ee) {
					tv_Status.setText("Role is a MISMATCH. Try Again.");	
				}	
			}
        	return true;
        
        case R.id.scan_response:
        	IntentIntegrator.initiateScan(RoleChallenge2.this);
        	return true;
 
        case R.id.create_challenge:
        	//if (flag3 == 2)
    		//{
        	if (tv_ChallengeData.equals("")||flag_challenge==0)
        	{
        		tv_Status.setText(tv_Status.getText().toString() + "\nGenerate Challenge Data first.");
        	}
        	else
        	{
        		Bundle args = new Bundle();
    			args.putString("response", tv_ChallengeData.getText().toString());
    			DFragmentImage dFragmentImage = new DFragmentImage();
    			dFragmentImage.setArguments(args);
    	    	dFragmentImage.show(fm, "qr_code_generator");
        	}
        return true;
 
        case R.id.settings:
            Toast.makeText(getBaseContext(), "Settings", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(RoleChallenge2.this, PreferencesSettings.class);
            startActivityForResult(intent, 0); 
            return true;
            
        case android.R.id.home:
        	finish();
            return true;
        

        default:
            return super.onOptionsItemSelected(item);
        }
    }    
	
	private void alertjason(int i) {
		// TODO Auto-generated method stub
		if (i ==1)
			{
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Result");
			alertDialog.setMessage("Role is Verified as a MATCH.");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			// here you can add functions
			}
			});
			alertDialog.setIcon(R.drawable.file_icon);
			alertDialog.show();
			}
		else
		{
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Result");
			alertDialog.setMessage("Role is a MISMATCH.");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			// here you can add functions
			}
			});
			alertDialog.setIcon(R.drawable.file_icon);
			alertDialog.show();
		}
	}

	class ElementWrapper {
		Element	elm;

		ElementWrapper() {
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
	
	@Override
    protected void onResume() {
        super.onResume();
        mResumed = true;
        // Sticky notes received from Android
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            NdefMessage[] messages = getNdefMessages(getIntent());
            byte[] payload = messages[0].getRecords()[0].getPayload();
            //setNoteBody(new String(payload));
            setIntent(new Intent()); // Consume this intent.
        }
        enableNdefExchangeMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mResumed = false;
        mNfcAdapter.disableForegroundNdefPush(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // NDEF exchange mode
        if (!mWriteMode && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            NdefMessage[] msgs = getNdefMessages(intent);
            promptForContent(msgs[0]);
        }

        // Tag writing mode
        if (mWriteMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            writeTag(getNoteAsNdef(), detectedTag);
        }
    }

    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        }

        @Override
        public void afterTextChanged(Editable arg0) {
            if (mResumed) {
                mNfcAdapter.enableForegroundNdefPush(RoleChallenge2.this, getNoteAsNdef());
            }
        }
    };

    private View.OnClickListener mTagWriter = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            // Write to a tag for as long as the dialog is shown.
            disableNdefExchangeMode();
            enableTagWriteMode();

            new AlertDialog.Builder(RoleChallenge2.this).setTitle("Touch tag to write")
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            disableTagWriteMode();
                            enableNdefExchangeMode();
                        }
                    }).create().show();
        }
    };

    private void promptForContent(final NdefMessage msg) {
    	  
    	if (flag_challenge == 0)
    	{
    		String body = new String(msg.getRecords()[0].getPayload());
            et_RoleName.setText(body);//setNoteBody(body);
           // flag_response = 1;
            challenge();
    	}
    	else
    	{
    		String body = new String(msg.getRecords()[0].getPayload());
            tv_ResponseData.setText(body);//setNoteBody(body);
            flag_response = 1;
            tv_Status.setText("You can now Verify the data.");
            verify();
    	}

    }

private void verify() {
		// TODO Auto-generated method stub
	if ((tv_ResponseData.getText().toString().equals("")|| flag_response==0))
	{
		tv_Status.setText(tv_Status.getText().toString() + "\nGenerate Response and Callenge Data First.");  		
	}
	else
	{
		try {
			msg = hengine.pairing.getGT().newRandomElement();
			msg.setFromBytes(ByteArrayHex.toByteArray(tv_ResponseData.getText().toString()));
			if (msg.equals(answer.elm)) {
				tv_Status.setText("Role is Verified as a MATCH.");
				alertjason(1);
			} else {
				tv_Status.setText("Role is a MISMATCH. Try Again.");
				alertjason(2);
			}
		} catch (RuntimeException ee) {
			tv_Status.setText("Role is a MISMATCH. Try Again.");;	
		}	
	}
	}

private void challenge() {
		// TODO Auto-generated method stub
	if ((flag_base == 0)||(et_RoleName.getText().toString().equals("")))
	{
		tv_Status.setText("Choose a valid Base File and/or Enter Role Name to Challenge.");
		flag_challenge = 0;
	}
	else
	{
	try {
		msg = hengine.pairing.getGT().newRandomElement();
		answer.elm = msg.duplicate();
		HIBECipher hcipher = hengine.encrypt(et_RoleName.getText().toString(), msg);
		tv_ChallengeData.setText(hcipher.toString());
		tv_Status.setText("Send the Challenge Data via NFC or Create a QR Code for scanning.");
		flag_challenge = 1;
	} catch (RuntimeException ee) {
		tv_Status.setText("Try Again.");
	}	
	}
	}

//    private void setNoteBody(String body) {
//        Editable text = tv_ChallengeData.getText();
//        text.clear();
//        text.append(body);
//    }

    private NdefMessage getNoteAsNdef() {
        byte[] textBytes = tv_ChallengeData.getText().toString().getBytes();
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/plain".getBytes(),
                new byte[] {}, textBytes);
        return new NdefMessage(new NdefRecord[] {
            textRecord
        });
    }

    NdefMessage[] getNdefMessages(Intent intent) {
        // Parse the intent
        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {
                    record
                });
                msgs = new NdefMessage[] {
                    msg
                };
            }
        } else {
            Log.d(TAG, "Unknown intent.");
            finish();
        }
        return msgs;
    }

    private void enableNdefExchangeMode() {
        mNfcAdapter.enableForegroundNdefPush(RoleChallenge2.this, getNoteAsNdef());
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mNdefExchangeFilters, null);
    }

    private void disableNdefExchangeMode() {
        mNfcAdapter.disableForegroundNdefPush(this);
        mNfcAdapter.disableForegroundDispatch(this);
    }

    private void enableTagWriteMode() {
        mWriteMode = true;
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mWriteTagFilters = new IntentFilter[] {
            tagDetected
        };
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, null);
    }

    private void disableTagWriteMode() {
        mWriteMode = false;
        mNfcAdapter.disableForegroundDispatch(this);
    }

    boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;

        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    toast("Tag is read-only.");
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    toast("Tag capacity is " + ndef.getMaxSize() + " bytes, message is " + size
                            + " bytes.");
                    return false;
                }

                ndef.writeNdefMessage(message);
                toast("Wrote message to pre-formatted tag.");
                return true;
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        toast("Formatted tag and wrote message");
                        return true;
                    } catch (IOException e) {
                        toast("Failed to format tag.");
                        return false;
                    }
                } else {
                    toast("Tag doesn't support NDEF.");
                    return false;
                }
            }
        } catch (Exception e) {
            toast("Failed to write tag");
        }

        return false;
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

	@Override
	public void onNdefPushComplete(NfcEvent event) {
		// TODO Auto-generated method stub
		 mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		// TODO Auto-generated method stub
		Time time = new Time();
        time.setToNow();
        String text = tv_ChallengeData.getText().toString();
        NdefMessage msg = new NdefMessage(NdefRecord.createMime(
                "text/plain", text.getBytes())
         /**
          * The Android Application Record (AAR) is commented out. When a device
          * receives a push with an AAR in it, the application specified in the AAR
          * is guaranteed to run. The AAR overrides the tag dispatch system.
          * You can add it back in to guarantee that this
          * activity starts when receiving a beamed message. For now, this code
          * uses the tag dispatch system.
          */
          //,NdefRecord.createApplicationRecord("com.example.android.beam")
        );
        return msg;
	}
	
    /** This handler receives a message from onNdefPushComplete */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_SENT:
                Toast.makeText(getApplicationContext(), "Message sent!", Toast.LENGTH_LONG).show();
                break;
            }
        }
    };
}
