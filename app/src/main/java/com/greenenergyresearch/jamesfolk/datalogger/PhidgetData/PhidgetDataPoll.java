package com.greenenergyresearch.jamesfolk.datalogger.PhidgetData;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PhidgetDataPoll 
{
	private long m_TimeStamp;
	private AbstractPhidgetData m_Data;
	private int m_index;
	private int m_serialnumber;
	private int m_deviceclass;
	
	SimpleDateFormat m_DateFormat;
	Date m_Date;
	
	public PhidgetDataPoll(long timestamp, AbstractPhidgetData data, int index, int serialnumber, int deviceclass)
	{
		m_TimeStamp = timestamp;
		m_Data = data;
		m_index = index;
		m_serialnumber = serialnumber;
		m_deviceclass = deviceclass;
		
		m_DateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		m_Date = new Date();
	}
	
	public boolean hasData()
	{
		if(m_Data != null)
		{
			if(m_Data.getNumYGraphValues() > 0)
			{
				return true;
			}
		}
		return false;
	}
	
	public double getYGraphValue(int index)
	{
		double val = 0.0;
		
		if(index < m_Data.getNumYGraphValues())
		{
			val = m_Data.getYGraphValue(index);
		}
		return val;
	}
	
	public String toString()
	{
		String dataString = new String("NULL");
		
		if(m_Data != null)
		{
			dataString = m_Data.toString();
		}
		
		m_Date.setTime(m_TimeStamp);
		return new String(m_serialnumber + ", " + m_index + ", " + m_DateFormat.format(m_Date) + ", " + dataString + "\n");
	}

}
