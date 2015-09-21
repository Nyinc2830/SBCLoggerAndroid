package com.greenenergyresearch.jamesfolk.datalogger;

/**
 * Created by jamesfolk on 9/21/15.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class SplashScreen extends Activity
{
    protected class Update extends AsyncTask<Context, Integer, String>
    {
        //private boolean postToastText;
        //private String toastText;

        Update()
        {
            //postToastText = false;
            //toastText = new String("");
        }

        @Override
        protected String doInBackground(Context... params)
        {
            try
            {
                Thread.sleep(5000);
            }
            catch (InterruptedException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            publishProgress(0);

            return "COMPLETE!";
        }

        // -- gets called just before thread begins
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);

            SplashScreen.this.startActivity(new Intent(SplashScreen.this, ServerChooser.class));
            SplashScreen.this.finish();
        }

        // -- called if the cancel button is pressed
        @Override
        protected void onCancelled() {
            super.onCancelled();

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }


    protected Update m_Update;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        //m_Update = new Update();
        //m_Update.execute(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(null == m_Update)
            m_Update = new Update();
        m_Update.execute(this);
    }
}
