package jason.cruz.torbac;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class DFragment extends DialogFragment implements OnEditorActionListener {

public interface EditNameDialogListener {
	void onFinishEditDialog(String inputText, String string_type);
}

private EditText mEditText;
private Button save;
String argument, argument_fileName;

public DFragment() {
// Empty constructor required for DialogFragment
}

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
{
		View view = inflater.inflate(R.layout.dialogfragment, container);
		mEditText = (EditText) view.findViewById(R.id.editText1);
		save = (Button)view.findViewById(R.id.bt_saveChallenge);
		getDialog().setTitle("Save File As...");
		Bundle mArgs = getArguments();
		argument = mArgs.getString("type");
		argument_fileName = mArgs.getString("file_name");
		if (argument.equals("master"))
		{
			mEditText.setHint("save as .master");
		}
		if (argument.equals("base"))
		{
			mEditText.setHint("save as .base");
		}		
		if (argument.equals("key"))
		{
			mEditText.setText(argument_fileName);
			mEditText.setHint("save as .rkey");
		}
		if (argument.equals("challenge"))
		{
			mEditText.setHint("save as .txt");
		}
		if (argument.equals("response"))
		{
			mEditText.setHint("save as .txt");
		}
		// Show soft keyboard automatically
		mEditText.requestFocus();
		mEditText.setOnEditorActionListener(this);

		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mEditText.getText().toString().equals(""))
				{
					Toast.makeText(getActivity(), "Enter File Name", Toast.LENGTH_LONG).show();
				}
				else
				{
				
				if (argument.equals("master"))
				{
					EditNameDialogListener activity = (EditNameDialogListener) getActivity();
				    activity.onFinishEditDialog(mEditText.getText().toString() + ".master.txt", "master");
				    dismiss();
				}
				if (argument.equals("base"))
				{
					EditNameDialogListener activity = (EditNameDialogListener) getActivity();
				    activity.onFinishEditDialog(mEditText.getText().toString() + ".base.txt", "base");
				    dismiss();
				}
				if (argument.equals("key"))
				{
					EditNameDialogListener activity = (EditNameDialogListener) getActivity();
				    activity.onFinishEditDialog(mEditText.getText().toString() + ".rkey.txt", "key");
				    dismiss();
				}
				if (argument.equals("challenge"))
				{
					EditNameDialogListener activity = (EditNameDialogListener) getActivity();
				    activity.onFinishEditDialog(mEditText.getText().toString() + ".txt", "challenge");
				    dismiss();
				}
				if (argument.equals("response"))
				{
					EditNameDialogListener activity = (EditNameDialogListener) getActivity();
				    activity.onFinishEditDialog(mEditText.getText().toString() + ".txt", "response");
				    dismiss();
				}
				}
			}
		});
		return view;
}

@Override
public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (EditorInfo.IME_ACTION_DONE == actionId) {
		    // Return input text to activity
		    EditNameDialogListener activity = (EditNameDialogListener) getActivity();
		    activity.onFinishEditDialog(mEditText.getText().toString(), argument);
		    this.dismiss();
		    return true;
		}
		return false;
		}



}