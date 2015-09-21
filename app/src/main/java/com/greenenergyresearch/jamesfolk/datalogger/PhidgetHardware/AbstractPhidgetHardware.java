package com.greenenergyresearch.jamesfolk.datalogger.PhidgetHardware;

import android.os.Bundle;
import android.widget.TabHost;
import android.widget.Toast;


import com.greenenergyresearch.jamesfolk.datalogger.TabsFragmentActivity.TabInfo;
import com.greenenergyresearch.jamesfolk.datalogger.PhidgetData.*;
import com.greenenergyresearch.jamesfolk.datalogger.PhidgetHardware.*;

import com.phidgets.Phidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.AttachEvent;
import com.phidgets.event.AttachListener;
import com.phidgets.event.DetachEvent;
import com.phidgets.event.DetachListener;

public abstract class AbstractPhidgetHardware 
{
	private Phidget m_PhidgetHardware;
	private int m_Index;
	
	public AbstractPhidgetHardware()
	{
		m_PhidgetHardware = null;
		m_Index = -1;
	}
	public void open(int serialNumber, String ipAddress, int port, String password) throws PhidgetException
	{
		//m_PhidgetHardware.addAttachListener(new AbstractPhidgetAttachListener());
		
		m_PhidgetHardware.open(serialNumber, ipAddress, port, password);
	}
	public void close() throws PhidgetException
	{
		m_PhidgetHardware.close();
	}
	
	public abstract PhidgetDataPoll poll() throws PhidgetException;
	public int getSerialNumber() throws PhidgetException
	{
		return m_PhidgetHardware.getSerialNumber();
	}
	
	public int getDeviceClass() throws PhidgetException
	{
		return m_PhidgetHardware.getDeviceClass();
	}
	
	public AbstractPhidgetHardware(Phidget phidgetHardware, int index)
	{
		m_PhidgetHardware = phidgetHardware;
		m_Index = index;
	}
	public Phidget getHardware()
	{
		return m_PhidgetHardware;
	}
	public int getIndex()
	{
		return m_Index;
	}
	
	public abstract int getNumberOfSeries();
	public abstract String getSeriesTitle(int index);
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	class AbstractPhidgetAttachListener implements AttachListener
    {

		@Override
		public void attached(final AttachEvent attachEvent) 
		{
			AttachEventHandler handler = new AttachEventHandler(attachEvent.getSource());
			
			// This is synchronized in case more than one device is attached before one completes attaching
			synchronized(handler) 
			{
				//runOnUiThread(handler);
				
				try
				{
					handler.wait();
				}
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}
    	
    } 
	
	class AttachEventHandler implements Runnable
    {
		public AttachEventHandler(Phidget device) 
		{
			m_PhidgetHardware = device;
		}

		public void run() 
		{
//			try 
//			{
//				Toast.makeText(TabsFragmentActivity.this,
//						m_PhidgetDevice.getDeviceName() + " connected.",
//						Toast.LENGTH_SHORT).show();
//
//				String tag = createTabTagFromPhidget(m_PhidgetDevice);
//		        String indicator = m_PhidgetDevice.getDeviceName();
//		        
//		        TabHost.TabSpec tabSpec = getActivity().mTabHost.newTabSpec(tag).setIndicator(indicator);
//		        
//		        TabInfo tabInfo = new TabInfo(
//		        		tabSpec, 
//		        		PhidgetGraphViewTab.class, 
//		        		m_BundleArguments, 
//		        		m_PhidgetDevice.getDeviceClass(), 
//		        		m_PhidgetDevice.getSerialNumber());
//		        
//		        getActivity().addTab(tabInfo);
//			} 
//			catch (PhidgetException e) 
//			{
//				e.printStackTrace();
//			}
	        
	    	// Notify that we're done
	    	synchronized(this) 
	    	{
	    		this.notify();
	    	}
		}
    }

}
