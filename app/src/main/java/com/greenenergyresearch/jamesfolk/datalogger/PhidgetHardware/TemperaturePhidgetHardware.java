package com.greenenergyresearch.jamesfolk.datalogger.PhidgetHardware;

import com.greenenergyresearch.jamesfolk.datalogger.TabsFragmentActivity;
import com.greenenergyresearch.jamesfolk.datalogger.PhidgetData.*;
import com.greenenergyresearch.jamesfolk.datalogger.PhidgetHardware.*;
import com.phidgets.Phidget;
import com.phidgets.PhidgetException;
import com.phidgets.TemperatureSensorPhidget;

public class TemperaturePhidgetHardware extends AbstractPhidgetHardware
{

	public TemperaturePhidgetHardware(Phidget phidgetHardware, int index)
	{
		super(phidgetHardware, index);
	}

	@Override
	public PhidgetDataPoll poll() throws PhidgetException 
	{

		TemperatureSensorPhidget temperatureSensorPhidget = (TemperatureSensorPhidget)getHardware();
		

		if(!temperatureSensorPhidget.isAttached() ||
				!temperatureSensorPhidget.isAttachedToServer())
			return null;
		
		TemperaturePhidgetData data = new TemperaturePhidgetData(temperatureSensorPhidget.getTemperature(getIndex()));
		
		PhidgetDataPoll _poll = new PhidgetDataPoll(System.currentTimeMillis(), data, getIndex(), temperatureSensorPhidget.getSerialNumber(), temperatureSensorPhidget.getDeviceClass());
		
		return _poll;
	}

	@Override
	public int getNumberOfSeries()
	{
		// TODO Auto-generated method stub
		//TemperatureSensorPhidget temperatureSensorPhidget = (TemperatureSensorPhidget)getHardware();\
		
		return 1;
	}

	@Override
	public String getSeriesTitle(int index) {
		// TODO Auto-generated method stub
		return "Temperature Phidget " + (index + 1);
	}

}
