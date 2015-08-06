package jason.cruz.torbac;

import it.unisa.dia.gas.jpbc.Element;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.graphics.Bitmap;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

public class RoleResponse extends FragmentActivity implements CreateNdefMessageCallback,
OnNdefPushCompleteCallback {
	
	Button bt_Browse1, bt_Browse2, bt_Decrypt, bt_Finish, bt_ScanChallenge, bt_CreateResponse;
	EditText et_BaseFile, et_RoleKeyFile;
	TextView tv_Status, tv_ChallengeData, tv_ResponseData, tv_Role;
	private static final int REQUEST_PATH1 = 1;
	private static final int REQUEST_PATH2 = 2;
	String baseFileName, basePath, baseContent, roleFileName, rolePath, roleContent, base_complete, role_complete;
	HIBEEngine	hengine, HE;
	FragmentManager fm;
	HIBEKey pkey, hkey;
	HIBECipher hcipher;
	Element result;
	String upc = "";
	int flag1, flag2, flag3;
	private NdefMessage mNdefMessage;
	public static final String MIME_TEXT_PLAIN = "text/plain";
	//public static final String TAG = "NfcDemo1";
	NfcAdapter mNfcAdapter;
	private static final int MESSAGE_SENT = 1;
	
    PendingIntent mNfcPendingIntent;
    IntentFilter[] mWriteTagFilters;
    IntentFilter[] mNdefExchangeFilters;
    

	private static final String TAG = "RoleResponse";
    private boolean mResumed = false;
    private boolean mWriteMode = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.role_response);
		
		fm = getSupportFragmentManager();
		et_BaseFile = (EditText)findViewById(R.id.et_base);
		et_RoleKeyFile = (EditText)findViewById(R.id.et_RoleKeyFile);
		tv_Status = (TextView)findViewById(R.id.txt_Status);
		tv_Role = (TextView)findViewById(R.id.tv_Role);
		tv_ChallengeData = (TextView)findViewById(R.id.tv_ChallengeData);
		tv_ResponseData = (TextView)findViewById(R.id.tv_ResponseData);
		bt_Browse1 = (Button)findViewById(R.id.bt_Browse1);
		bt_Browse2 = (Button)findViewById(R.id.bt_Browse2);
		bt_Decrypt = (Button)findViewById(R.id.bt_Decrypt);
		bt_Finish = (Button)findViewById(R.id.bt_Finish);
		bt_ScanChallenge = (Button)findViewById(R.id.bt_ScanChallenge);
		bt_CreateResponse = (Button)findViewById(R.id.bt_CreateResponse);
		flag1 = 0;
		flag2 = 0;
		flag3 = 0;
		
		
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

		
		
		bt_Finish.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		bt_Browse1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			     Intent intent1 = new Intent("cruz.jason.file_chooser");
			     startActivityForResult(intent1,REQUEST_PATH1);
			}
		});
	
		bt_Browse2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			     Intent intent2 = new Intent("cruz.jason.file_chooser");
			     startActivityForResult(intent2,REQUEST_PATH2);
			}
		});
		
		bt_Decrypt.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				if ((flag1 == 1)&&(flag2 == 1)&&(!tv_ChallengeData.getText().toString().equals(""))) 
				{
				try {
					tv_Status.setText("Decrypting...");
					hcipher = hengine.readCipherFromString(tv_ChallengeData.getText().toString());
					result = hengine.decrypt(pkey, hcipher);
					tv_ResponseData.setText(ByteArrayHex.toHexStr(result.toBytes()));
					tv_Status.setText("Response Data generated.");
					flag3 = 2;
				} catch (RuntimeException ee) {
					tv_ResponseData.setText("Try Again.");
				}	
				}
				else
				{
					tv_Status.setText("Browse for the Base and Role Key Files, and then Scan Challenge QR Code.");
				}
			}
		});
		
		bt_ScanChallenge.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				IntentIntegrator.initiateScan(RoleResponse.this);
			}
		});
		
		bt_CreateResponse.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			//	WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
			//	   Display display = manager.getDefaultDisplay();
			//	   Point point = new Point();
				  // display.getSize(point);
				if (flag3 == 2)
				{
				int width = 200;
				   int height = 200;
				   int smallerDimension = width < height ? width : height;
		//		   smallerDimension = smallerDimension;	 
				   //Encode with a QR Code image
				   QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(tv_ResponseData.getText().toString(), 
				             null, 
				             Contents.Type.TEXT,  
				             BarcodeFormat.QR_CODE.toString(), 
				             smallerDimension);
				   try {
				    Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
				    ImageView myImage = (ImageView) findViewById(R.id.imageView1);
				    myImage.setImageBitmap(bitmap);
				 
				   } catch (WriterException e) {
				    e.printStackTrace();
				   }
				  
				}
				else
				{
					tv_Status.setText("Generate Response Data First.");
				}
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
	     // See which child activity is calling us back.
	     if (requestCode == REQUEST_PATH1){
	             if (resultCode == RESULT_OK) {
	                  baseFileName = data.getStringExtra("GetFileName");
	                  baseContent = data.getStringExtra("Content");
	                  basePath = data.getStringExtra("GetPath");
	                  base_complete = basePath + "/" + baseFileName;
	                  et_BaseFile.setText(base_complete);  
	                  if (!baseContent.contains("seed")) {
	                		try {
	    						hengine = new HIBEEngine(base_complete);
	    						tv_Status.setText("Base file okay.");
	    						flag1 = 1;
	    					} catch (RuntimeException ee) {
	    						tv_Status.setText("Chosen file is not a base file. Try again.");
	    					}
	                  }
	                  if (baseContent.contains("seed")) {
  							tv_Status.setText("The chosen file is not a base file. Try again.");
  								flag1 = 0;
  						} 	              
	             }
	      }
	     
	     if (requestCode == REQUEST_PATH2){
            if (resultCode == RESULT_OK) {
                    roleFileName = data.getStringExtra("GetFileName");
                    roleContent = data.getStringExtra("Content");
                    rolePath = data.getStringExtra("GetPath");
                    role_complete = rolePath + "/" + roleFileName;
                    et_RoleKeyFile.setText(role_complete);
     				try {
     					pkey = new HIBEKey();
    					pkey = hengine.readKeyFromFile(role_complete);
    					tv_Role.setText("Role of: " + pkey.hid.toDotForm());
    					tv_Status.setText("Role key file loaded.");
    					flag2 = 1;
    				} catch (RuntimeException ee) {
    					tv_Status.setText("The chosen file is not a key file. Try again.");
    					flag2 = 0;
    				}
            	}
	     }	     
	     
	     if (requestCode == IntentIntegrator.REQUEST_CODE){
	     if (resultCode != RESULT_CANCELED) {
				IntentResult scanResult = IntentIntegrator.parseActivityResult(
						requestCode, resultCode, data);
				if (scanResult != null) {
					upc = scanResult.getContents();
					tv_ChallengeData.setText(upc);
					flag3 = 1;
				}
			}
	     else
	     {
	    	 flag3 = 0;
	     }
	     }
	 }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main__screen, menu);
		return true;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{}
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
                mNfcAdapter.enableForegroundNdefPush(RoleResponse.this, getNoteAsNdef());
            }
        }
    };

    private View.OnClickListener mTagWriter = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            // Write to a tag for as long as the dialog is shown.
            disableNdefExchangeMode();
            enableTagWriteMode();

            new AlertDialog.Builder(RoleResponse.this).setTitle("Touch tag to write")
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
    	  String body = new String(msg.getRecords()[0].getPayload());
          tv_ChallengeData.setText(body);//setNoteBody(body);
    }

//    private void setNoteBody(String body) {
//        Editable text = tv_ChallengeData.getText();
//        text.clear();
//        text.append(body);
//    }

    private NdefMessage getNoteAsNdef() {
        byte[] textBytes = tv_ResponseData.getText().toString().getBytes();
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
        mNfcAdapter.enableForegroundNdefPush(RoleResponse.this, getNoteAsNdef());
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
        String text = tv_ResponseData.getText().toString();
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
