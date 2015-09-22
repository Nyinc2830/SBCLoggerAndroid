package com.greenenergyresearch.jamesfolk.datalogger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.PanListener;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;

//import android.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.phidgets.Phidget;
import com.phidgets.PhidgetException;

import com.greenenergyresearch.jamesfolk.datalogger.PhidgetData.*;
import com.greenenergyresearch.jamesfolk.datalogger.PhidgetHardware.*;


public class PhidgetGraphViewTab extends Fragment implements BaseTab
{	

	
	protected class UpdatePhidget extends AsyncTask<Fragment, Integer, String> 
    {
		//private boolean m_HasData = false;
		private String m_StatusText = new String();
		
		int getColor(int index)
		{
			switch(index)
			{
			case 0:
				return Color.RED;
			case 1:
				return Color.WHITE;
			case 2:
				return Color.YELLOW;
			case 3:
				return Color.GREEN;
			case 4:
				return Color.BLUE;
			case 5:
				return Color.CYAN;
			case 6:
				return Color.MAGENTA;
			case 7:
				return Color.DKGRAY;
			}
			return Color.LTGRAY;
		}
		
		@Override
		protected String doInBackground(Fragment... params) 
		{	
			m_StartMilliseconds = System.currentTimeMillis();
			
			int i = 0;
			while (true) 
			{
				try 
				{
					Thread.sleep(m_PollMilliseconds);
					
					double diff_time = (System.currentTimeMillis() - m_StartMilliseconds) / 1000.0;
					m_CurrentMillisecondsLeft -= diff_time;
					double yValue = 0.0;
					
					m_StatusText = "";
					if(m_PhidgetHardwareFactory.getNumberOfSeries() == mDataset.getSeriesCount())
					{
						Vector<Double> yValues = m_PhidgetHardwareFactory.poll(m_LogData);
						
						for(int ii = 0; ii < m_PhidgetHardwareFactory.getNumberOfSeries(); ii++)
						{
							yValue = yValues.elementAt(ii).doubleValue();
							
							m_StatusText += "" + diff_time + " (s) - " + yValue + "\n";
							
							mDataset.getSeriesAt(ii).add(diff_time, yValue);
							
						}
					}
					else if(mDataset.getSeriesCount() == 0)
					{
						XYSeries series = null;
						XYSeriesRenderer renderer;
						for(int ii = 0; ii < m_PhidgetHardwareFactory.getNumberOfSeries(); ii++)
						{
							series = new XYSeries(m_PhidgetHardwareFactory.getSeriesTitle(ii));
							mDataset.addSeries(series);
							//mCurrentSeries = series;
							
							renderer = new XYSeriesRenderer();
							renderer.setDisplayChartValues(true);
							mRenderer.addSeriesRenderer(renderer);
							
							renderer.setPointStyle(PointStyle.CIRCLE);
							renderer.setFillPoints(true);
							
							
							renderer.setColor(this.getColor(ii));
						}
					}
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				
				publishProgress(i);
				i++;
			}
			// return "COMPLETE!";
		}

		// -- gets called just before thread begins
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();

		}

	
		@Override
		protected void onProgressUpdate(Integer... values) 
		{
			super.onProgressUpdate(values);
			
			if (mChartView != null) 
			{
				
				mChartView.repaint();
			}
			
			txtStatusTextView.setText(m_StatusText);
		}

		// -- called if the cancel button is pressed
		@Override
		protected void onCancelled() 
		{
			super.onCancelled();
			
		}

		@Override
		protected void onPostExecute(String result) 
		{
			super.onPostExecute(result);
		}
	}
	
	private PhidgetHardwareFactory m_PhidgetHardwareFactory = null;

	private int m_PhidgetDeviceClass;
	private int m_PhidgetDeviceSerial;
	
	private String m_IPAddress = "192.168.1.20";
	private int m_Port = 5001;
	private String m_Password = "admin";
	private int m_PollMilliseconds = 1000;
	private int m_CurrentMillisecondsLeft = 0;
	
	private long m_StartMilliseconds;
	
	public static String m_ScreenShotPath;
	public static String m_CSVFileContentPath;
	
	private boolean m_LogData;
	private double m_StartTime;
	private double m_EndTime;
	private boolean m_StartedLogging;
	
	private TextView txtStatusTextView;
	
	private Button btnEmailDialogue;

	private UpdatePhidget m_UpdatePhidgetTask = null;
	
	private GraphicalView mChartView = null;
	
	private XYMultipleSeriesDataset mDataset = null;

	private XYMultipleSeriesRenderer mRenderer = null;

	public String createImageFromBitmap(Bitmap bmp)
    {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
		String path = new String( Environment.getExternalStorageDirectory() +
                                           "/capturedscreen.jpg");
		File file = new File(path);
		try
		{
		   file.createNewFile();
		   FileOutputStream ostream = new FileOutputStream(file);
		   ostream.write(bytes.toByteArray());        
		   ostream.close();
		   return path;
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}    
		return "";
    }
	
	public String createFile(String file_content)
	{

		String path = new String( Environment.getExternalStorageDirectory() +
                "/capturedscreen.csv");
		File file = new File(path);
		try
		{
			file.createNewFile();
			FileOutputStream ostream = new FileOutputStream(file);
			ostream.write(file_content.getBytes());        
			ostream.close();
			return path;
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}    
		return "";



//		FileOutputStream fos;
//		String path = new String(android.content.ContextWrapper.getFilesDir().toString());
//		String filename = new String("/captureddata.csv");
//
//		try
//		{
//			fos = openFileOutput("captureddata.csv", MODE_PRIVATE);
//			fos.write(file_content.getBytes());
//			fos.close();
//			
//			
//			return path + filename;
//		}
//		catch(IOException ioe)
//		{
//			System.out.println(ioe.getMessage());
//		}
//		return "";
	}
	public PhidgetGraphViewTab()
	{
		m_PhidgetHardwareFactory = new PhidgetHardwareFactory();
		m_LogData = false;
		
		mDataset = new XYMultipleSeriesDataset();
		mRenderer = new XYMultipleSeriesRenderer();
		m_UpdatePhidgetTask = new UpdatePhidget();
	}
	
	public void setBundle(Bundle bundle)
	{
		m_PhidgetDeviceClass = bundle.getInt("deviceclass");
		m_PhidgetDeviceSerial = bundle.getInt("deviceserial");
		m_IPAddress = bundle.getString("address");
		m_Port = bundle.getInt("port");
		m_Password = bundle.getString("password");
		m_PollMilliseconds = bundle.getInt("poll");
		m_CurrentMillisecondsLeft = m_PollMilliseconds;
	}
	
	@Override
	public void onAttach (Activity activity)
	{
		System.out.println("onAttach");
		
		super.onAttach(activity);
	}
	
	@Override
	public void onCreate (Bundle savedInstanceState)
	{
		System.out.println("onCreate");
		
		super.onCreate(savedInstanceState);
		
		
		m_LogData = false;
        

        
		try 
		{
			m_PhidgetHardwareFactory.open(m_PhidgetDeviceClass, m_PhidgetDeviceSerial, m_IPAddress, m_Port, m_Password);
			
			mRenderer.setApplyBackgroundColor(true);
			mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
			mRenderer.setAxisTitleTextSize(16);
			mRenderer.setChartTitleTextSize(20);
			mRenderer.setLabelsTextSize(15);
			mRenderer.setLegendTextSize(15);
			mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
			mRenderer.setZoomButtonsVisible(true);
			mRenderer.setPointSize(10);
		    mRenderer.setShowGrid(true);
			mRenderer.setXTitle(m_PhidgetHardwareFactory.getXTitle());
			mRenderer.setYTitle(m_PhidgetHardwareFactory.getYTitle());
			
		} 
		catch (PhidgetException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		m_UpdatePhidgetTask.execute(this);
		
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState)
	{
		System.out.println("onActivityCreated");
		
		super.onActivityCreated(savedInstanceState);

		txtStatusTextView = (TextView) getView().findViewById(R.id.TabeventResultLabel);
		
		txtStatusTextView.setMovementMethod(new ScrollingMovementMethod());
		
		ToggleButton tglLogging = (ToggleButton) getView().findViewById(R.id.tglTabLogging);
        
        tglLogging.setOnClickListener(new View.OnClickListener() {
			
        	
			@Override
			public void onClick(View arg0) 
			{
				m_LogData = !m_LogData;
				
				double time = (System.currentTimeMillis() - m_StartMilliseconds) / 1000.0;
				
				if(m_LogData)
				{
					if(!m_StartedLogging)
					{
						m_StartedLogging = true;
						m_StartTime = time;
					}
					
					btnEmailDialogue.setEnabled(false);
				}
				else
				{
					m_EndTime = time;
					
					btnEmailDialogue.setEnabled(true);
				}
				
			}
		});
        
        btnEmailDialogue = (Button) getView().findViewById(R.id.btnTabEmailDialogue);
        
        btnEmailDialogue.setOnClickListener(new View.OnClickListener() {
			
        	public Bitmap getBitmapOfView(View v)
            {
        		View rootview = v.getRootView();
        		rootview.setDrawingCacheEnabled(true);
        		Bitmap bmp = rootview.getDrawingCache();
        		return bmp;
            }
			@Override
			public void onClick(View arg0) 
			{
				new Thread(new Runnable() {
					public void run()
					{
						LinearLayout view = (LinearLayout)getView().findViewById(R.id.chart);
						
						View imageViewCapture = view.getRootView();
						Bitmap bitmap = getBitmapOfView(imageViewCapture);
						//Bitmap bitmap = mChartView.toBitmap();
						
						m_ScreenShotPath = createImageFromBitmap(bitmap);
						m_CSVFileContentPath = createFile(m_PhidgetHardwareFactory.toString());




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
//							Toast.makeText(PhidgetGraphViewTab.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
						}

//						Intent intent=new Intent(getActivity().getApplicationContext(), EmailDialogue.class);
//						startActivity(intent);
					}
				}).start();
				
			}
		});

		LinearLayout layout = (LinearLayout) getView().findViewById(R.id.chart);
		mChartView = ChartFactory.getLineChartView(getView().getContext(), mDataset,
				mRenderer);
		mRenderer.setClickEnabled(true);
		mRenderer.setSelectableBuffer(100);
		mRenderer.setPanEnabled(true);
		
		mChartView.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				SeriesSelection seriesSelection = mChartView
						.getCurrentSeriesAndPoint();
				double[] xy = mChartView.toRealPoint(0);
				if (seriesSelection == null) 
				{
					Toast.makeText(getView().getContext(),
							"No chart element was clicked",
							Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(
							getView().getContext(),
							"Chart element in series index "
									+ seriesSelection.getSeriesIndex()
									+ " data point index "
									+ seriesSelection.getPointIndex()
									+ " was clicked"
									+ " closest point value X="
									+ seriesSelection.getXValue() + ", Y="
									+ seriesSelection.getValue()
									+ " clicked point value X="
									+ (float) xy[0] + ", Y="
									+ (float) xy[1], Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		mChartView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				SeriesSelection seriesSelection = mChartView
						.getCurrentSeriesAndPoint();
				if (seriesSelection == null) {
					Toast.makeText(getView().getContext(),
							"No chart element was long pressed",
							Toast.LENGTH_SHORT);
					return false; // no chart element was long pressed, so
									// let something
					// else handle the event
				} else {
					Toast.makeText(getView().getContext(),
							"Chart element in series index "
									+ seriesSelection.getSeriesIndex()
									+ " data point index "
									+ seriesSelection.getPointIndex()
									+ " was long pressed",
							Toast.LENGTH_SHORT);
					return true; // the element was long pressed - the event
									// has been
					// handled
				}
			}
		});
		mChartView.addZoomListener(new ZoomListener() {
			public void zoomApplied(ZoomEvent e) {
				String type = "out";
				if (e.isZoomIn()) {
					type = "in";
				}
				System.out.println("Zoom " + type + " rate "
						+ e.getZoomRate());
			}

			public void zoomReset() {
				System.out.println("Reset");
			}
		}, true, true);
		mChartView.addPanListener(new PanListener() {
			public void panApplied() {
				System.out.println("New X range=["
						+ mRenderer.getXAxisMin() + ", "
						+ mRenderer.getXAxisMax() + "], Y range=["
						+ mRenderer.getYAxisMax() + ", "
						+ mRenderer.getYAxisMax() + "]");
			}
		});
		
		layout.addView(mChartView, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}
	
	@Override
	public void onStart ()
	{
		System.out.println("onStart");
		
		
		
		super.onStart();
	}
	
	@Override
	public void onPause ()
	{
		System.out.println("onPause");
		
		super.onPause();
	}
	
	@Override
	public void onStop ()
	{
		System.out.println("onStop");
		
		super.onStop();
	}
	
	@Override
	public void onDestroyView ()
	{
		System.out.println("onDestroyView");
		
		super.onDestroyView();
		
		LinearLayout layout = (LinearLayout) getView().findViewById(R.id.chart);
		layout.removeView(mChartView);
		
		mChartView = null;
	}
	
	@Override
	public void onDestroy() 
	{
		System.out.println("onDestroy");
		
    	super.onDestroy();
    	
    	try 
    	{
			m_PhidgetHardwareFactory.close();
		} 
    	catch (PhidgetException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	@Override
	public void onDetach ()
	{
		System.out.println("onDetach");
		
		super.onDetach();
	}
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container,
			Bundle savedInstanceState) 
	{
		System.out.println("onCreateView");
		
		if (container == null) 
		{
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }
		
		return (RelativeLayout)inflater.inflate(R.layout.phidget_graph_view_tab, container, false);
	}
	
	@Override
	public void onResume() 
	{
		super.onResume();
		System.out.println("onResume");

		if (mChartView != null)
		{
			mChartView.repaint();
		}
	}
}
