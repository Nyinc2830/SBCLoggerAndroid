package com.greenenergyresearch.jamesfolk.datalogger;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class EmailDialogue extends Activity
{
//	private GMailSender m_GmailSender;

	protected void sendEmail() {
//		EditText txtEmailAddress = (EditText)findViewById(R.id.txtEmail);
//		EditText txtTo = (EditText)findViewById(R.id.txtTo);
//
//		String multiLines = txtTo.getText().toString();
//		String[] toArr;
//		String delimiter = "\n";
//
//		toArr = multiLines.split(delimiter);

		Log.i("Send email", "");
		String[] TO = {""};
		String[] CC = {""};
		Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date = new Date();

		String dataString = new String(dateFormat.format(date));

		emailIntent.setData(Uri.parse("mailto:"));
		emailIntent.setType("text/plain");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
		emailIntent.putExtra(Intent.EXTRA_CC, CC);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Logged data from " + dataString);
		emailIntent.putExtra(Intent.EXTRA_TEXT, dataString);



		ArrayList<Uri> uris = new ArrayList<Uri>();
		uris.add(Uri.parse("file://" + PhidgetGraphViewTab.m_CSVFileContentPath));
		uris.add(Uri.parse("file://" + PhidgetGraphViewTab.m_ScreenShotPath));
		emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
//		startActivity(emailIntent);


//		emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + PhidgetGraphViewTab.m_CSVFileContentPath));
//		emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + PhidgetGraphViewTab.m_ScreenShotPath));

		try {
			startActivity(Intent.createChooser(emailIntent, "Send mail..."));
//			finish();
//			Log.i("Finished sending email...", "");
		}
		catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(EmailDialogue.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
		}
	}
	
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
						sendEmail();
//						EditText txtEmailAddress = (EditText)findViewById(R.id.txtEmail);
//						EditText txtPassword = (EditText)findViewById(R.id.txtPassword);
//						EditText txtTo = (EditText)findViewById(R.id.txtTo);
//
//						m_GmailSender = new GMailSender(txtEmailAddress.getText().toString(),
//								txtPassword.getText().toString());
//
//						String multiLines = txtTo.getText().toString();
//						String[] toArr;
//						String delimiter = "\n";
//
//						toArr = multiLines.split(delimiter);
//
//						m_GmailSender.setTo(toArr);
//						m_GmailSender.setFrom(txtEmailAddress.getText().toString());
//
//						SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
//						Date date = new Date();
//
//						String dataString = new String(dateFormat.format(date));
//
//						m_GmailSender.setSubject("Logged data from " + dataString);
//						m_GmailSender.setBody(dataString);
//
//						try
//						{
//
//							m_GmailSender.addAttachment(PhidgetGraphViewTab.m_CSVFileContentPath);
//							m_GmailSender.addAttachment(PhidgetGraphViewTab.m_ScreenShotPath);
//
//							if(m_GmailSender.send())
//							{
//								//Toast.makeText(EmailDialogue.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
//							}
//							else
//							{
//								//Toast.makeText(EmailDialogue.this, "Email was not sent.", Toast.LENGTH_LONG).show();
//							}
//						}
//						catch(Exception e)
//						{
//
//							//Toast.makeText(EmailDialogue.this, "There was a problem sending the email.\n" + e.getMessage(), Toast.LENGTH_LONG).show();
//						}
						
						finish();
					}
				}).start();
				
			}
		});
	}
 
}