package com.greenenergyresearch.jamesfolk.datalogger.PhidgetData;

public class AccelerometerPhidgetData implements AbstractPhidgetData
{
	private double [] m_Acc;
	
	public AccelerometerPhidgetData(double [] acc)
	{
		m_Acc = new double[acc.length];
		for(int i = 0; i < acc.length; i++)
			m_Acc[i] = acc[i];
	}
	
	public int getNumYGraphValues()
	{
		return m_Acc.length;
	}
	
	public double getYGraphValue(int index)
	{
		return m_Acc[index];
	}
	
	public String toString()
	{
		return new String("" + getYGraphValue(0));
	}
	

}
