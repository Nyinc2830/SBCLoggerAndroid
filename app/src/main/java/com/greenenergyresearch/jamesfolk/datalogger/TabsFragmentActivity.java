package com.greenenergyresearch.jamesfolk.datalogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.phidgets.Manager;
import com.phidgets.Phidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.AttachEvent;
import com.phidgets.event.AttachListener;
import com.phidgets.event.DetachEvent;
import com.phidgets.event.DetachListener;
import com.phidgets.event.ServerConnectEvent;
import com.phidgets.event.ServerConnectListener;
import com.phidgets.event.ServerDisconnectEvent;
import com.phidgets.event.ServerDisconnectListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.GetChars;
import android.view.View;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabContentFactory;

import com.camera.simplemjpeg.*;

//import com.example.phidgetgraphingapplication.PhidgetGraphViewTab;
/**
 *
 */
public class TabsFragmentActivity extends FragmentActivity implements TabHost.OnTabChangeListener 
{

	private TabHost mTabHost;
	private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, TabsFragmentActivity.TabInfo>();
	static ArrayList<TabHost.TabSpec> m_TabSpeclist = new ArrayList<TabHost.TabSpec>(); 
	private TabInfo mLastTab = null;
	private Manager m_DeviceManager;
	
	private String m_IPAddress;
	private String m_Password;
	private int m_Port;
	private int m_PollMilliseconds = 1000;
	private int m_CurrentMillisecondsLeft = 0;
	
	private static ListView m_ListView = null;
	
	public static void addItem(TabsFragmentActivity activity, String phidgetType, String key)
	{
		if(m_ListView == null)
		{
			m_ListView = new ListView(activity);
		}
		else
		{
			
		}
	}
	
	protected TabsFragmentActivity getActivity()
	{
		return this;
	}
	
//	private static void addTab(TabsFragmentActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) 
//	{
//		tabSpec.setContent(activity.new TabFactory(activity));
//        String tag = tabSpec.getTag();
//
//        // Check to see if we already have a fragment for this tab, probably
//        // from a previously saved state.  If so, deactivate it, because our
//        // initial state is that a tab isn't shown.
//        tabInfo.fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
//        if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
//            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
//            ft.detach(tabInfo.fragment);
//            ft.commit();
//            activity.getSupportFragmentManager().executePendingTransactions();
//        }
//
//        tabHost.addTab(tabSpec);
//	}
	private static void addTab(TabsFragmentActivity activity, TabInfo tabInfo)
	{
		tabInfo.tabSpec.setContent(activity.new TabFactory(activity));
        //String tag = tabInfo.tabSpec.getTag();
        //int count = tabHost.getTabWidget().getChildCount();

        // Check to see if we already have a fragment for this tab, probably
        // from a previously saved state.  If so, deactivate it, because our
        // initial state is that a tab isn't shown.
        tabInfo.fragment = activity.getSupportFragmentManager().findFragmentByTag(tabInfo.tabSpec.getTag());
        if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) 
        {
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.attach(tabInfo.fragment);
            ft.commit();
            activity.getSupportFragmentManager().executePendingTransactions();
        }

        activity.mTabHost.addTab(tabInfo.tabSpec);
        
		m_TabSpeclist.add(tabInfo.tabSpec);
		
		activity.mapTabInfo.put(tabInfo.tabSpec.getTag(), tabInfo);
		
		//activity.mTabHost.setCurrentTab(0);
		//activity.onTabChanged(tabInfo.tabSpec.getTag());
	}
	
	public void removeAllTabs()
	{
		mTabHost.setCurrentTab(0);
		mTabHost.clearAllTabs();
	}
	
	private void removeTab(TabHost.TabSpec tabSpec)
	{
		m_TabSpeclist.remove(tabSpec);
		this.mapTabInfo.remove(tabSpec.getTag());
		
		mTabHost.setCurrentTab(0);
		mTabHost.clearAllTabs();  // clear all tabs from the tabhost
		
		int index = 0;
		for(TabHost.TabSpec spec : m_TabSpeclist) // add all that you remember back
		{
			mTabHost.addTab(spec);
			mTabHost.setCurrentTab(index++);
		}	 
	}
	
	public String createTabTagFromPhidget(Phidget phidget) throws PhidgetException
	{
		int deviceID = phidget.getDeviceID();
		int deviceClass = phidget.getDeviceClass();
		int deviceVersion = phidget.getDeviceVersion();
		String deviceType = phidget.getDeviceType();
		
    	return deviceType + " " + deviceClass + " " + deviceVersion + " " + deviceID;
	}
	/**
	 * 
	 */
	public class TabInfo 
	{
		 private TabHost.TabSpec tabSpec;
         private Class<?> clss;
         private Bundle args;
         private Fragment fragment;
         private int phidgetDeviceClass;
         private int phidgetDeviceSerialNumber;
         //private PhidgetGraphViewTab fragment;
         
         TabInfo(TabHost.TabSpec tabSpec, Class<?> clazz, Bundle args, int deviceClass, int deviceSerialNumber) 
         {
        	 this.tabSpec = tabSpec;
        	 this.clss = clazz;
        	 this.args = args;
        	 this.phidgetDeviceClass = deviceClass;
        	 this.phidgetDeviceSerialNumber = deviceSerialNumber;
         }
		
	}
	/**
	 * 
	 *
	 */
	class TabFactory implements TabContentFactory {

		private final Context mContext;

	    /**
	     * @param context
	     */
	    public TabFactory(Context context) {
	        mContext = context;
	    }

	    /** (non-Javadoc)
	     * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
	     */
	    public View createTabContent(String tag) {
	        View v = new View(mContext);
	        v.setMinimumWidth(0);
	        v.setMinimumHeight(0);
	        return v;
	    }

	}
	
	class PhidgetConnectListener implements ServerConnectListener
	{

		@Override
		public void serverConnected(com.phidgets.event.ServerConnectEvent arg0) {
			// TODO Auto-generated method stub
			ServerConnectEvent handler = new ServerConnectEvent();
			// This is synchronized in case more than one device is attached before one completes attaching
			synchronized(handler) {
				runOnUiThread(handler);
				try {
					handler.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	class PhidgetDisconnectListener implements ServerDisconnectListener
	{
		@Override
		public void serverDisconnected(
				com.phidgets.event.ServerDisconnectEvent arg0) 
		{

			ServerDisconnectEvent handler = new ServerDisconnectEvent();
			
			// This is synchronized in case more than one device is attached before one completes attaching
			synchronized(handler) 
			{
				runOnUiThread(handler);
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
	
	class ServerConnectEvent implements Runnable
	{
		public void run() 
		{
			
			Toast.makeText(TabsFragmentActivity.this,
					"Connected to the server",
					Toast.LENGTH_SHORT).show();
			
			m_DeviceManager.addAttachListener(new PhidgetTabAttachListener());
	    	m_DeviceManager.addDetachListener(new PhidgetTabDetachListener());
	        
	    	// Notify that we're done
	    	synchronized(this) 
	    	{
	    		this.notify();
	    	}
		}	
	}
	
	class ServerDisconnectEvent implements Runnable
	{
		public void run() 
		{
			
			Toast.makeText(TabsFragmentActivity.this,
					"Disconnected to the server",
					Toast.LENGTH_SHORT).show();
			
			getActivity().disconnect();
	        
	    	// Notify that we're done
	    	synchronized(this) 
	    	{
	    		this.notify();
	    	}
		}	
	}
	
	class PhidgetTabAttachListener implements AttachListener
    {

		@Override
		public void attached(final AttachEvent attachEvent) 
		{
			AttachEventHandler handler = new AttachEventHandler(attachEvent.getSource());
			
			// This is synchronized in case more than one device is attached before one completes attaching
			synchronized(handler) 
			{
				runOnUiThread(handler);
				
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
	
	
	class PhidgetTabDetachListener implements DetachListener
    {

		@Override
		public void detached(final DetachEvent detachEvent) 
		{
			DetachEventHandler handler = new DetachEventHandler(detachEvent.getSource());
			// This is synchronized in case more than one device is attached before one completes attaching
			synchronized(handler) 
			{
				runOnUiThread(handler);
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
    	Phidget m_PhidgetDevice;
    	
    	Bundle m_BundleArguments;
    	
		public AttachEventHandler(Phidget device) 
		{
			this.m_PhidgetDevice = device;
			
//			try 
//			{
//				TabsFragmentActivity.phidgetMap.put(this.m_PhidgetDevice.getSerialNumber(), this.m_PhidgetDevice);
//			} 
//			catch (PhidgetException e1) 
//			{
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
		}

		public void run() 
		{
			try 
			{
				Toast.makeText(TabsFragmentActivity.this,
						m_PhidgetDevice.getDeviceName() + " connected.",
						Toast.LENGTH_SHORT).show();

				String tag = createTabTagFromPhidget(m_PhidgetDevice);
		        String indicator = m_PhidgetDevice.getDeviceName();
		        
		        indicator = indicator.replace("Phidget ", "");
		        //indicator = indicator.substring(8, indicator.length());
		        
		        
		        TabHost.TabSpec tabSpec = getActivity().mTabHost.newTabSpec(tag).setIndicator(indicator);
		        //TODO
		        TabInfo tabInfo = new TabInfo(
		        		tabSpec, 
		        		PhidgetGraphViewTab.class, 
		        		m_BundleArguments, 
		        		m_PhidgetDevice.getDeviceClass(), 
		        		m_PhidgetDevice.getSerialNumber());
		        TabsFragmentActivity.addTab(getActivity(), tabInfo);
		        
		        
		        
		        //getActivity().addTab(tabInfo);
			} 
			catch (PhidgetException e) 
			{
				e.printStackTrace();
			}
	        
	    	// Notify that we're done
	    	synchronized(this) 
	    	{
	    		this.notify();
	    	}
		}
    }
	    
    class DetachEventHandler implements Runnable 
    {
    	Phidget m_PhidgetDevice;

    	
    	public DetachEventHandler(Phidget device) 
    	{
    		this.m_PhidgetDevice = device;
    	}
	    	
		public void run() 
		{
			try 
			{	
				Toast.makeText(TabsFragmentActivity.this,
						m_PhidgetDevice.getDeviceName() + " disconnected.",
						Toast.LENGTH_SHORT).show();
				
				
				String tag = createTabTagFromPhidget(m_PhidgetDevice);
		        TabInfo tabInfo = getActivity().mapTabInfo.get(tag);
		        getActivity().removeTab(tabInfo.tabSpec);
			} 
			catch (PhidgetException e) 
			{
				e.printStackTrace();
			}
	        
	    	// Notify that we're done
	    	synchronized(this) 
	    	{
	    		this.notify();
	    	}
		}
    }
    
    
	/** (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabs_layout);
		initialiseTabHost(savedInstanceState);
		
		if (savedInstanceState != null) 
		{
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); //set the tab as per the saved state
        }
	}

	/** (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState(Bundle outState) 
    {
        outState.putString("tab", mTabHost.getCurrentTabTag()); //save the tab selected
        super.onSaveInstanceState(outState);
    }
	
	/**
	 * Initialise the Tab Host
	 */
	private void initialiseTabHost(Bundle args)
	{
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        
        TabHost.TabSpec tabSpec = null;
        TabInfo tabInfo = null;
        String tag = new String("BaseTab");
        String indicator = new String("Main");
        tabSpec = this.mTabHost.newTabSpec(tag).setIndicator(indicator);
    	//tabInfo = new TabInfo(tabSpec, Tab1Fragment.class, args, 0, 0);
    	tabInfo = new TabInfo(tabSpec, MjpegActivity.class, args, 0, 0);
    	
        TabsFragmentActivity.addTab(getActivity(), tabInfo);
        
//        TabHost.TabSpec tabSpec = null;
//    	TabInfo tabInfo = null;
//        String tag = "BaseTab";
//    	String indicator = "BaseTab";
//    	tabSpec = this.mTabHost.newTabSpec(tag).setIndicator(indicator);
//    	tabInfo = new TabInfo(tabSpec, Tab1Fragment.class, args, 0, 0);
//    	getActivity().addTab(tabInfo);
//    	
//    	
        
//        
        mTabHost.setCurrentTab(0);
        this.onTabChanged(mTabHost.getCurrentTabTag());
        
        mTabHost.setOnTabChangedListener(this);
        
        m_IPAddress = getIntent().getStringExtra("address");
        m_Password = getIntent().getStringExtra("password");
    	m_Port = Integer.parseInt(getIntent().getStringExtra("port"));
        m_PollMilliseconds = Integer.parseInt(getIntent().getStringExtra("poll")); 
        m_CurrentMillisecondsLeft = m_PollMilliseconds;
        
        try
        {
			m_DeviceManager = new Manager();
			
			m_DeviceManager.addAttachListener(new PhidgetTabAttachListener());
	    	m_DeviceManager.addDetachListener(new PhidgetTabDetachListener());
	    	
	    	//m_DeviceManager.addServerConnectListener(new PhidgetConnectListener());
	    	m_DeviceManager.addServerDisconnectListener(new PhidgetDisconnectListener());
		} 
        catch (PhidgetException e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        connect();        
	}
	
//	private static void removeTab(TabsFragmentActivity activity, TabHost tabHost, TabInfo tabInfo) 
//	{
//		int count = tabHost.getTabWidget().getChildCount();
//		String currentTag = "";
//		View view = null;
//		
//		
//		view = tabHost.getTabWidget().findViewWithTag(tabInfo.tabSpec.getTag());
//		
//		tabHost.getTabWidget().removeView(view);
//		
////		if(count > 1)
////		{
////			String tag = tabInfo.tag;
////			int index_to_remove = -1;
////			int index_to_set_current = tabHost.getCurrentTab();
////			int currentIndex = 0;
////			
////			do
////			{
////				tabHost.setCurrentTab(currentIndex);
////				currentTag = tabHost.getCurrentTabTag();
////				
////				 if(currentTag.equals(tag))
////					 index_to_remove = currentIndex;
////					
////				currentIndex++;
////			}
////			while(index_to_remove == -1);
////			
////			view = tabHost.getTabWidget().getChildTabViewAt(index_to_remove);
////			
////			while(index_to_remove == index_to_set_current)
////			{
////				index_to_set_current++;
////				index_to_set_current %= count;
////			}
////			
////			tabHost.setCurrentTab(index_to_set_current);
////			currentTag = tabHost.getCurrentTabTag();
////		}
////		tabHost.getTabWidget().removeView(view);
////		//activity.onTabChanged(currentTag);
//	}
	
	/**
	 * @param activity
	 * @param tabHost
	 * @param tabSpec
	 * @param clss
	 * @param args
	 */
//	private static void addTab(TabsFragmentActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo)
//	{
//		// Attach a Tab view factory to the spec
//		tabSpec.setContent(activity.new TabFactory(activity));
//        String tag = tabSpec.getTag();
//        //int count = tabHost.getTabWidget().getChildCount();
//
//        // Check to see if we already have a fragment for this tab, probably
//        // from a previously saved state.  If so, deactivate it, because our
//        // initial state is that a tab isn't shown.
//        tabInfo.fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
//        if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) 
//        {
//            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
//           
//            ft.detach(tabInfo.fragment);
//            ft.commit();
//            activity.getSupportFragmentManager().executePendingTransactions();
//        }
//
//        tabHost.addTab(tabSpec);
////        if(count == 0)
////        {
////        	activity.onTabChanged(tabInfo.tabSpec.getTag());
////        }
//	}

	/** (non-Javadoc)
	 * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
	 */
	public void onTabChanged(String tag) 
	{
		TabInfo newTab = this.mapTabInfo.get(tag);
		
		if (mLastTab != newTab) 
		{
			FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
			
            if (mLastTab != null)
            {
                if (mLastTab.fragment != null) 
                {
                	ft.detach(mLastTab.fragment);
                }
            }
            if (newTab != null) 
            {
                if (newTab.fragment == null) 
                {
                	try
                	{
                		if(newTab.args == null)
                		{
                			newTab.args = new Bundle();
                			newTab.args.putString("address", m_IPAddress);
                			newTab.args.putString("password", m_Password);
                			newTab.args.putInt("port", m_Port);
                			newTab.args.putInt("poll", m_PollMilliseconds);
                			newTab.args.putInt("deviceclass", newTab.phidgetDeviceClass);
                			newTab.args.putInt("deviceserial", newTab.phidgetDeviceSerialNumber);
                		}
	                    newTab.fragment = Fragment.instantiate(this, newTab.clss.getName(), newTab.args);
	                    
	                    //TODO
	                    BaseTab phidgetTab = (BaseTab)newTab.fragment;
	                    
	                    phidgetTab.setBundle(newTab.args);

	                    ft.add(R.id.realtabcontent, newTab.fragment, newTab.tabSpec.getTag());
                	}
                	catch(Fragment.InstantiationException e)
                	{
                		e.printStackTrace();
                	}
                } 
                else 
                {
                    ft.attach(newTab.fragment);
                }
            }

            mLastTab = newTab;
            ft.commit();
            this.getSupportFragmentManager().executePendingTransactions();
		}
    }	

	void disconnect()
	{
		removeAllTabs();
		
		try 
		{
			m_DeviceManager.close();
		}
		catch (PhidgetException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		com.phidgets.usb.Manager.Uninitialize();
		
		Intent nextScreen = new Intent(getApplicationContext(), ServerChooser.class);
		
		startActivity(nextScreen);
		finish();
	}
	
	void connect()
	{
		try 
		{
			com.phidgets.usb.Manager.Initialize(this);

			// This will only open the first device it sees on the webservice
			// Make sure to change the IP address (above) and port to the one for your computer connected to the Phidget
			
        	m_DeviceManager.open(m_IPAddress, m_Port, m_Password);
        } 
        catch (PhidgetException pe) 
        {
	        pe.printStackTrace();
		} 
	}
}
