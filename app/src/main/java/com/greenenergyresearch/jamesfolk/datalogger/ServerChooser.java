package com.greenenergyresearch.jamesfolk.datalogger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class ServerChooser extends Activity
{
	public class CustomOnItemSelectedListener implements OnItemSelectedListener 
	{
		 

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) 
		{
			
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		 
	}
	
	EditText inputServerAddress;
	EditText inputServerPort;
	EditText inputServerPassword;
	Spinner inputPollDataSpinner;
	//EditText inputPollData;

	
	@Override
    public void onBackPressed()
    {
		finish();
    }
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_chooser);
        
        
        inputServerAddress = (EditText)findViewById(R.id.txtServerAddress);
        inputServerPort = (EditText)findViewById(R.id.txtServerPort);
        inputServerPassword = (EditText)findViewById(R.id.txtServerPassword);
        inputPollDataSpinner = (Spinner)findViewById(R.id.cboPollData);
        
        inputPollDataSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        //inputPollData = (EditText)findViewById(R.id.txtPollMilliseconds);
        
        Button btnConnect = (Button) findViewById(R.id.btnConnect);
        
        btnConnect.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) 
			{
				Intent nextScreen = new Intent(getApplicationContext(), TabsFragmentActivity.class);
				
				//Intent nextScreen = null;
				nextScreen.putExtra("address", inputServerAddress.getText().toString());
				nextScreen.putExtra("port", inputServerPort.getText().toString());
				nextScreen.putExtra("password", inputServerPassword.getText().toString());
				
				int  pollMilliseconds = 1000;
				switch(inputPollDataSpinner.getSelectedItemPosition())
				{
				case 0:
					pollMilliseconds = 1000;
					break;
				case 1:
					pollMilliseconds = 1000 * 10;
					break;
				case 2:
					pollMilliseconds = 1000 * 30;
					break;
				case 3:
					pollMilliseconds = 1000 * 60;
					break;
				
				}
				
				
				nextScreen.putExtra("poll", Integer.toString(pollMilliseconds));
				
				ServerChooser.this.startActivity(nextScreen);
				
				startActivity(nextScreen);
			}
		});
        
    }
}
