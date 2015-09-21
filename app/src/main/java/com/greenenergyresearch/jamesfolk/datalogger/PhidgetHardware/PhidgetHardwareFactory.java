package com.greenenergyresearch.jamesfolk.datalogger.PhidgetHardware;

import java.util.Vector;

import android.annotation.SuppressLint;

import com.greenenergyresearch.jamesfolk.datalogger.TabsFragmentActivity;
import com.greenenergyresearch.jamesfolk.datalogger.PhidgetData.*;
import com.greenenergyresearch.jamesfolk.datalogger.PhidgetHardware.*;
import com.phidgets.AccelerometerPhidget;
import com.phidgets.InterfaceKitPhidget;
import com.phidgets.Phidget;
import com.phidgets.PhidgetException;
import com.phidgets.TemperatureSensorPhidget;

public class PhidgetHardwareFactory
{
	private static int temperatureSensorIndex = 0;
	private static int interfaceKitIndex = 0;
	private static int accelerometerIndex = 0;

	Vector<PhidgetDataPoll> m_PhidgetPolls;
	
	private AbstractPhidgetHardware m_CurrentHardWare;
	
	private int m_deviceClass = -1;

	
	public PhidgetHardwareFactory()
	{
		m_PhidgetPolls = new Vector<PhidgetDataPoll>();		
	}
	
	public int numPolls()
	{
		return m_PhidgetPolls.size();
	}

	private String m_XTitle = new String("default X Title");
	private String m_YTitle = new String("default Y Title");
	private String m_SeriesTitle = new String("default Series Title");
	
	public String getXTitle()
	{
		return m_XTitle;
	}
	
	public String getYTitle()
	{
		return m_YTitle;
	}
	
	private void openInternal(int deviceSerialNumber, String ipAddress, int port, String password) throws PhidgetException
	{
		//Phidget phidget = TabsFragmentActivity.phidgetMap.get(deviceSerialNumber);
		
		if(m_CurrentHardWare == null)
		{
			switch(m_deviceClass)
			{
			case com.phidgets.Phidget.PHIDCLASS_INTERFACEKIT:
			{
				System.out.println("PHIDCLASS_INTERFACEKIT");
				m_CurrentHardWare = new InterfaceKitHardware(new InterfaceKitPhidget(), interfaceKitIndex++);
			
				m_XTitle = "TIME (s)";
				m_YTitle = "UNKNOWN";
				m_SeriesTitle = "InterfaceKit " + interfaceKitIndex;
			}
				break;
			case com.phidgets.Phidget.PHIDCLASS_TEMPERATURESENSOR:
			{
				System.out.println("PHIDCLASS_TEMPERATURESENSOR");
				m_CurrentHardWare = new TemperaturePhidgetHardware(new TemperatureSensorPhidget(), temperatureSensorIndex++);
				
				m_XTitle = "TIME (s)";
				m_YTitle = "TEMPERATURE (C\u00b0)";
				m_SeriesTitle = "Temperature " + temperatureSensorIndex;
			}
				break;
			case com.phidgets.Phidget.PHIDCLASS_ACCELEROMETER:
			{
				System.out.println("PHIDCLASS_ACCELEROMETER");
				m_CurrentHardWare = new AccelerometerPhidgetHardware(new AccelerometerPhidget(), accelerometerIndex++);
				
				m_XTitle = "TIME (s)";
				m_YTitle = "g (Earth Gravitation)";
				m_SeriesTitle = "Accelerometer " + accelerometerIndex;
			}
				break;
			}
	
			
			if(m_CurrentHardWare != null)
			{
				m_CurrentHardWare.open(deviceSerialNumber, ipAddress, port, password);
			}
		}
	}

	public void open(int deviceClass, int deviceSerialNumber, String ipAddress, int port, String password) throws PhidgetException
	{
	
		if(m_deviceClass == -1 || m_deviceClass == deviceClass)
		{
			m_deviceClass = deviceClass;

			openInternal(deviceSerialNumber, ipAddress, port, password);
		}
	}
	
	private void closeInternal()
	{
		switch(m_deviceClass)
		{
		case com.phidgets.Phidget.PHIDCLASS_TEMPERATURESENSOR:
		{
			System.out.println("PHIDCLASS_TEMPERATURESENSOR");
			temperatureSensorIndex--;
		}
			break;
			
		case com.phidgets.Phidget.PHIDCLASS_INTERFACEKIT:
		{
			System.out.println("PHIDCLASS_INTERFACEKIT");
			interfaceKitIndex--;
		}
			break;
		case com.phidgets.Phidget.PHIDCLASS_ACCELEROMETER:
		{
			System.out.println("PHIDCLASS_ACCELEROMETER");
			accelerometerIndex--;
		}
			break;
		}
	}
	
	public void close() throws PhidgetException
	{
		closeInternal();
		m_CurrentHardWare.close();
	}

	@SuppressLint("UseValueOf")
	public Vector<Double> poll(boolean log) throws Exception 
	{
		Vector<Double> values = new Vector<Double>();
		//double value = 0.0f;
		String detailMessage = new String("");

		if(m_CurrentHardWare != null)
		{
			PhidgetDataPoll poll = m_CurrentHardWare.poll();

			if(poll != null)
			{
				if(log)
				{
					m_PhidgetPolls.add(poll);
				}
				
				Double dbl = null;
				
				for(int i = 0; i < m_CurrentHardWare.getNumberOfSeries(); i++)
				{	
					dbl = new Double(poll.getYGraphValue(i));
					values.add(dbl);
				}
				//value = poll.getYGraphValue(0);
				
				return values;
			}
			else
			{
				detailMessage = "Current Poll is null.";
			}
		}
		else
		{
			detailMessage = "Current Hardware is null.";
		}
		
		Exception pe = new Exception(detailMessage);
		
		throw pe;
	}
	
	public String toString()
	{
		String ret = new String("Serial #, Device Index, Log Time, Data Value\n");
		
		for(int i = 0; i < m_PhidgetPolls.size(); i++)
		{
			ret = ret + m_PhidgetPolls.get(i).toString();
		}
		
		return ret;
	}
	
	public int getNumberOfSeries() throws PhidgetException
	{
		if(m_CurrentHardWare != null)
		{
			return m_CurrentHardWare.getNumberOfSeries();
		}
		return 0;
	}
	
	public String getSeriesTitle(int index)
	{
		if(m_CurrentHardWare != null)
		{
			return m_CurrentHardWare.getSeriesTitle(index);
		}
		return "NOT SET";
	}
}
