package com.greenenergyresearch.jamesfolk.datalogger.PhidgetData;

import java.util.Vector;

public class InterfaceKitPhidgetData  implements AbstractPhidgetData
{
	private Vector<Integer> m_RawValues;
	
	public InterfaceKitPhidgetData(Vector<Integer> rawValues)
	{
		m_RawValues = new Vector<Integer>(rawValues);
	}
	
	public int getNumYGraphValues()
	{
		return m_RawValues.size();
	}
	public double getYGraphValue(int index)
	{
		return m_RawValues.elementAt(index).doubleValue();
	}
	
	public String toString()
	{
		String str = new String("");
		int i = 0;
		for(i = 0; i < getNumYGraphValues() - 1; i++)
		{
			str += "" + getYGraphValue(i) + ", ";
		}
		str += "" + getYGraphValue(i);
		
		return str;
	}
	

}
