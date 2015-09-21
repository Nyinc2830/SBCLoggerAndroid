package com.greenenergyresearch.jamesfolk.datalogger.PhidgetHardware;

import com.greenenergyresearch.jamesfolk.datalogger.TabsFragmentActivity;
import com.greenenergyresearch.jamesfolk.datalogger.PhidgetData.*;
import com.greenenergyresearch.jamesfolk.datalogger.PhidgetHardware.*;
import com.phidgets.AccelerometerPhidget;
import com.phidgets.Phidget;
import com.phidgets.PhidgetException;
import com.phidgets.TemperatureSensorPhidget;

public class AccelerometerPhidgetHardware extends AbstractPhidgetHardware 
{
	public AccelerometerPhidgetHardware(Phidget phidgetHardware, int index)
	{
		super(phidgetHardware, index);
	}
	
	@Override
	public PhidgetDataPoll poll() throws PhidgetException 
	{
		AccelerometerPhidget accelerometerPhidget = (AccelerometerPhidget)getHardware();
		
		if(!accelerometerPhidget.isAttached() ||
				!accelerometerPhidget.isAttachedToServer())
			return null;
		
		int axisCount = accelerometerPhidget.getAxisCount();
		double accelerations[] = new double[axisCount];
		for(int i = 0; i < axisCount; i++)
		{
			accelerations[i] = accelerometerPhidget.getAcceleration(i);
			//System.out.print(accelerations[i]);
		}
		//System.out.println();
		
		AccelerometerPhidgetData data = new AccelerometerPhidgetData(accelerations);

		PhidgetDataPoll _poll = new PhidgetDataPoll(System.currentTimeMillis(), data, getIndex(), accelerometerPhidget.getSerialNumber(), accelerometerPhidget.getDeviceClass());
		
		return _poll;
	}

	@Override
	public int getNumberOfSeries()
	{
		// TODO Auto-generated method stub
		AccelerometerPhidget accelerometerPhidget = (AccelerometerPhidget)getHardware();
		
		int d = 0;
		
		try {
			d = accelerometerPhidget.getAxisCount();
			//d = d / 3;
		} catch (PhidgetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return (d);
	}

	@Override
	public String getSeriesTitle(int index) 
	{
		// TODO Auto-generated method stub
		int m = index % 3;
		int d = index / 3;
		String title = new String("");
		
		switch(m)
		{
		case 0:
			title = "(X) - Accelerometer " + (d + 1);
			break;
		case 1:
			title = "(Y) - Accelerometer " + (d + 1);
			break;
		case 2:
			title = "(Z) - Accelerometer " + (d + 1);
			break;
		}
		
		return title;
		
	}

}
