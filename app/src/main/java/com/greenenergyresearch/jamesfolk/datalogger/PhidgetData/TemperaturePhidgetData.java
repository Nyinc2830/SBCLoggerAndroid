package com.greenenergyresearch.jamesfolk.datalogger.PhidgetData;

public class TemperaturePhidgetData implements AbstractPhidgetData
{
	private double m_Temperature;
	
	public TemperaturePhidgetData(double temperature)
	{
		m_Temperature = temperature;
	}
	
	public int getNumYGraphValues()
	{
		return 1;
	}
	public double getYGraphValue(int index)
	{
		return m_Temperature;
	}
	
	public String toString()
	{
		return new String("" + getYGraphValue(0));
	}
	

}
