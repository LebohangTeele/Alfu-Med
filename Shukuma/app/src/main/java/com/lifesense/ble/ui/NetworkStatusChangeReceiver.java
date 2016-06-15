/**
 * 
 */
package com.lifesense.ble.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * @author CaiChiXiang
 *
 */
public class NetworkStatusChangeReceiver extends BroadcastReceiver{

	private Context appContext;
	private OnNetworkStatusChangeListener onNetworkStatusChangeListener;
	
	public NetworkStatusChangeReceiver(Context context,OnNetworkStatusChangeListener statusChangeListener)
	{
		if(context!=null && statusChangeListener!=null)
		{
			appContext=context;
			onNetworkStatusChangeListener=statusChangeListener;
		}
	}
	
	public NetworkStatusChangeReceiver(){}
	
	
	public boolean isNetworkConnected()
	{
		
		
		if(appContext!=null)
		{
			ConnectivityManager cm = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo[] netInfo = cm.getAllNetworkInfo();
			if(cm.getActiveNetworkInfo()!=null)
			{
				System.err.println("当前网络状态=========="+cm.getActiveNetworkInfo().isConnectedOrConnecting());
			}

			boolean haveConnectedWifi=false;
			boolean haveConnectedMobile=false;
			for (NetworkInfo ni : netInfo) 
			{
				if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				{
					if (ni.isConnectedOrConnecting())
					{
						haveConnectedWifi = true;
						if(onNetworkStatusChangeListener!=null)
						{
							onNetworkStatusChangeListener.onNetworkStatusChange("wifi network", true);
						}
					}
					else
					{
						if(onNetworkStatusChangeListener!=null)
						{
							onNetworkStatusChangeListener.onNetworkStatusChange("wifi network", false);
						}
					} 

				}

				else if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				{
					if (ni.isConnectedOrConnecting())
					{
						haveConnectedMobile = true;
						if(onNetworkStatusChangeListener!=null)
						{
							onNetworkStatusChangeListener.onNetworkStatusChange("mobile network", true);
						}
					}
					else
					{
						if(onNetworkStatusChangeListener!=null)
						{
							onNetworkStatusChangeListener.onNetworkStatusChange("mobile network", false);
						}
					}       
				}



			}
			
			boolean isConnected= haveConnectedWifi || haveConnectedMobile;
			if(!isConnected)
			{
				if(onNetworkStatusChangeListener!=null)
				{
					onNetworkStatusChangeListener.onNetworkStatusChange("no network", false);
				}
			}
			return isConnected;
		}
		else return false;
		




	}
	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) 
	{
//		  NetworkInfo mNetworkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
//		  NetworkInfo.State state = (mNetworkInfo == null ? NetworkInfo.State.UNKNOWN : mNetworkInfo.getState());
		    
		 Log.w("Network Listener", "Network Type Changed=========");
		 if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION))
		 {
			 isNetworkConnected();
		 }
	}

}
