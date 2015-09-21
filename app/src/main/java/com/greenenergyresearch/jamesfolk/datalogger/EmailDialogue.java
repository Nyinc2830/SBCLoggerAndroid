package com.greenenergyresearch.jamesfolk.datalogger;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class EmailDialogue extends Activity
{
	private GMailSender m_GmailSender;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.email_properties);
		
		Button btnSend = (Button) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new View.OnClickListener() {
			
        	
			@Override
			public void onClick(View arg0) 
			{	  
				new Thread(new Runnable() 
				{
					public void run()
					{
						EditText txtEmailAddress = (EditText)findViewById(R.id.txtEmail);
						EditText txtPassword = (EditText)findViewById(R.id.txtPassword);
						EditText txtTo = (EditText)findViewById(R.id.txtTo);
						
						m_GmailSender = new GMailSender(txtEmailAddress.getText().toString(), 
								txtPassword.getText().toString());

						String multiLines = txtTo.getText().toString();
						String[] toArr;
						String delimiter = "\n";

						toArr = multiLines.split(delimiter);
						
						m_GmailSender.setTo(toArr); 
						m_GmailSender.setFrom(txtEmailAddress.getText().toString()); 
						
						SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
						Date date = new Date();
						
						String dataString = new String(dateFormat.format(date));
						
						m_GmailSender.setSubject("Logged data from " + dataString);
						m_GmailSender.setBody(dataString);
				
						try 
						{
							
							m_GmailSender.addAttachment(PhidgetGraphViewTab.m_CSVFileContentPath);
							m_GmailSender.addAttachment(PhidgetGraphViewTab.m_ScreenShotPath);

							if(m_GmailSender.send())
							{
								//Toast.makeText(EmailDialogue.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
							}
							else
							{
								//Toast.makeText(EmailDialogue.this, "Email was not sent.", Toast.LENGTH_LONG).show();
							} 
						}
						catch(Exception e)
						{
							
							//Toast.makeText(EmailDialogue.this, "There was a problem sending the email.\n" + e.getMessage(), Toast.LENGTH_LONG).show();
						}
						
						finish();
					}
				}).start();
				
			}
		});
	}
 
}