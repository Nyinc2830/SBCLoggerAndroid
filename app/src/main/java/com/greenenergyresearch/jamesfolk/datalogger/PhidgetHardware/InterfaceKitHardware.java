package com.greenenergyresearch.jamesfolk.datalogger.PhidgetHardware;

import java.util.Vector;

import com.greenenergyresearch.jamesfolk.datalogger.PhidgetData.*;
import com.greenenergyresearch.jamesfolk.datalogger.PhidgetHardware.*;
import com.phidgets.InterfaceKitPhidget;
import com.phidgets.Phidget;
import com.phidgets.PhidgetException;

public class InterfaceKitHardware extends AbstractPhidgetHardware
{

	public InterfaceKitHardware(Phidget phidgetHardware, int index)
	{
		super(phidgetHardware, index);
	}

	@Override
	public PhidgetDataPoll poll() throws PhidgetException 
	{
		InterfaceKitPhidget interfaceKitPhidget = (InterfaceKitPhidget)getHardware();
		
		if(!interfaceKitPhidget.isAttached() ||
				!interfaceKitPhidget.isAttachedToServer())
			return null;
		
		Vector<Integer> rawValues = new Vector<Integer>();
		Integer value = null;
		
		int sensorCount = interfaceKitPhidget.getSensorCount();

		for(int i = 0; i < sensorCount; i++)
		{
			//value = Integer.valueOf(interfaceKitPhidget.getSensorRawValue(i));
			value = Integer.valueOf(interfaceKitPhidget.getSensorValue(i));
			rawValues.add(value);
		}
		
		InterfaceKitPhidgetData data = new InterfaceKitPhidgetData(rawValues);
		
		PhidgetDataPoll _poll = new PhidgetDataPoll(System.currentTimeMillis(), data, getIndex(), interfaceKitPhidget.getSerialNumber(), interfaceKitPhidget.getDeviceClass());
		
		return _poll;
	}

	@Override
	public int getNumberOfSeries()
	{
		// TODO Auto-generated method stub
		InterfaceKitPhidget interfaceKitPhidget = (InterfaceKitPhidget)getHardware();
		
		try {
			return interfaceKitPhidget.getSensorCount();
		} catch (PhidgetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public String getSeriesTitle(int index) {
		// TODO Auto-generated method stub
		return "Analog Input #" + (index + 1);
	}

}
