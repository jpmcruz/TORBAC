package jason.cruz.torbac;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class Main_Screen extends Activity {

	Button bt_GenParams, bt_RKeyGenerateMaster, bt_RKeyGenerate, bt_RoleResponse, bt_RoleChallenge;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main__screen);
		
		bt_GenParams = (Button)findViewById(R.id.bt_GenParams);
		bt_RKeyGenerateMaster = (Button)findViewById(R.id.bt_RKeyGenerateMaster);
		bt_RKeyGenerate = (Button)findViewById(R.id.bt_RKeyGenerate);
		bt_RoleResponse = (Button)findViewById(R.id.bt_RoleResponse);
		bt_RoleChallenge = (Button)findViewById(R.id.bt_RoleChallenge);
		
		bt_GenParams.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent("cruz.jason.gen_params"));
			}
		});
		bt_RKeyGenerateMaster.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				startActivity(new Intent("cruz.jason.rkey_generate_master"));
			}
		});
		bt_RKeyGenerate.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				startActivity(new Intent("cruz.jason.rkey_generate"));
			}
		});

		bt_RoleChallenge.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//startActivity(new Intent("cruz.jason.role_challenge"));
				startActivity(new Intent(Main_Screen.this, RoleChallenge2.class));
		//		finish();
			}
		});
		bt_RoleResponse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//startActivity(new Intent("cruz.jason.role_response"));
				startActivity(new Intent(Main_Screen.this, RoleResponse2.class));
		//		finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main__screen, menu);
		return true;
	}

}
