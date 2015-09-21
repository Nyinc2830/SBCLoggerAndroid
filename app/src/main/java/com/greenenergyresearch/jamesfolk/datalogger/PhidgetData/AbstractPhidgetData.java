package com.greenenergyresearch.jamesfolk.datalogger.PhidgetData;

public interface AbstractPhidgetData 
{
	public abstract int getNumYGraphValues();
	public abstract double getYGraphValue(int index);
}
