package jason.cruz.torbac;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class DFragmentImage extends DialogFragment {

private Button finish;
private ImageView qrCode; 
String argument, argument_fileName;

public DFragmentImage() {
// Empty constructor required for DialogFragment
}

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
{
		View view = inflater.inflate(R.layout.dialogfragmentimage, container);
		qrCode = (ImageView) view.findViewById(R.id.imageView1);
		finish = (Button)view.findViewById(R.id.bt_finish);
		getDialog().setTitle("QR Code");
		Bundle mArgs = getArguments();
		argument = mArgs.getString("response");
//		argument_fileName = mArgs.getString("file_name");
//		
		
		int width = 200;
		int height = 200;
		int smallerDimension = width < height ? width : height;
		QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(argument, 
		             null, 
		             Contents.Type.TEXT,  
		             BarcodeFormat.QR_CODE.toString(), 
		             smallerDimension);
		   try {
		    Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
	//	    ImageView myImage = (ImageView) findViewById(R.id.imageView1);
		    qrCode.setImageBitmap(bitmap);
		 
		   } catch (WriterException e) {
		    e.printStackTrace();
		   }
		  
		
		
		finish.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		return view;
}
}