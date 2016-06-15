/**
 * 
 */
package com.lifesense.ble.ui.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lifesense.ble.DeviceConnectState;
import com.lifesense.ble.LsBleManager;
import com.lifesense.ble.OnConnectListener;
import com.lifesense.ble.bean.BloodPressureData;
import com.lifesense.ble.bean.LsDeviceInfo;
import com.lifesense.ble.ui.R;

/**
 * @author CaiChiXiang
 *
 */
public class ConnectDeviceFragment extends Fragment {

	

	private View rootView,measureWaitingPanel,showInfoPanel;
	private MenuItem clearMenuItem;
	private TextView infoTextView,titleTextView,stateTextView;
	private LsBleManager mlsBleManager;
	private Button connectBtn;
	private LsDeviceInfo device;
	private ProgressDialog connectingDialog;
	private boolean isConnect,isStartMeasured;
	/**
	 * 命令启动血压计开始测量的连接监听器回调对象
	 */
	private OnConnectListener connectListener=new OnConnectListener() 
	{
		
		@Override
		public void onWaitingForStartMeasuring(String deviceId) 
		{
			getActivity().runOnUiThread( new Runnable() {
				public void run() {
					showStartMeasuringPromptDialog("Start Measuring","Do you want to start measuring now?");
				}
			});
			
		}
		
		@Override
		public void onReceiveBloodPressureData(final BloodPressureData bData) 
		{
			getActivity().runOnUiThread(new  Runnable() 
			{
				public void run() 
				{
					if(isStartMeasured)
					{
						showInfoPanel.setVisibility(View.VISIBLE);
						measureWaitingPanel.setVisibility(View.GONE);
					}
					
//					infoTextView.append("DeviceSn：" + bData.getDeviceSn() + "\n");
					infoTextView.append("BroadcastID：" + bData.getBroadcastId() + "\n");
					infoTextView.append("Measuring time：" + bData.getDate() + "\n");
					infoTextView.append("userNumber：" + bData.getUserId() + "\n");
					infoTextView.append("Systolic：" + bData.getSystolic() + "\n");
					infoTextView.append("Diastolic ：" + bData.getDiastolic() + "\n");
					infoTextView.append("PulseRate：" + bData.getPulseRate() + "\n");
					infoTextView.append("MeanArterialPressure：" + bData.getMeanArterialPressure() + "\n");
					infoTextView.append("Unit：" + bData.getDeviceSelectedUnit() + "\n");
					infoTextView.append("Battery：" + bData.getBattery() + "\n");
					infoTextView.append("----------------------------------"+"\n");
				}
			});
			
		}
		
		@Override
		public void onConnectStateChange(final DeviceConnectState connectState) 
		{
			getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() 
				{
					if(connectingDialog!=null)
					{
						connectingDialog.dismiss();
					}
					
					if(isStartMeasured)
					{
						showInfoPanel.setVisibility(View.VISIBLE);
						measureWaitingPanel.setVisibility(View.GONE);
					}
					
					String tempState="unknown";
					if(connectState==DeviceConnectState.CONNECTED_SUCCESS)
					{
						isConnect=true;
						connectBtn.setText("Disconnect");
						tempState="Connect Success";
						stateTextView.setBackgroundColor(Color.GREEN);
					}
					if(connectState==DeviceConnectState.CONNECTED_FAILED)
					{
						isConnect=false;
						connectBtn.setText("Connect");
						tempState="Connect Failed";
						stateTextView.setBackgroundColor(Color.RED);
					}
					if(connectState==DeviceConnectState.DISCONNECTED)
					{
						isConnect=false;
						connectBtn.setText("Connect");
						tempState="DisConnect";
						stateTextView.setBackgroundColor(Color.RED);
					}
					stateTextView.setText("Status : "+tempState);
					rootView.findViewById(android.R.id.empty).setVisibility(View.GONE);
					infoTextView.setVisibility(View.VISIBLE);
					Toast.makeText(getActivity(), "Connect state:"+connectState.toString(), Toast.LENGTH_LONG).show();
					
				}
			});
			
		}
	};

	public ConnectDeviceFragment(){}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		System.err.println("Fragment Life cycle = onCreateView================= ");
		rootView=inflater.inflate(R.layout.fragment_connect_device, container, false);
		infoTextView=(TextView) rootView.findViewById(R.id.show_info_content_tv);
		titleTextView=(TextView) rootView.findViewById(R.id.title_device_name_tv);
		stateTextView=(TextView) rootView.findViewById(R.id.title_connect_state_tv);
		connectBtn=(Button) rootView.findViewById(R.id.action_connect);
		showInfoPanel=rootView.findViewById(R.id.show_info_panel);
		measureWaitingPanel=rootView.findViewById(R.id.measure_waiting_panel);
		
		infoTextView.setMovementMethod(new ScrollingMovementMethod());
		
		setHasOptionsMenu(true);
		
    	return rootView;
	}

    
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		System.err.println("Fragment Life cycle = onCreate================= ");
		super.onCreate(savedInstanceState);
		final Bundle args = getArguments();
		
		if (args.containsKey("LS_DEVICE_INFO")) 
		{
			device = args.getParcelable("LS_DEVICE_INFO");
			if(device!=null)
			{
				System.err.println("Fragment get device info ="+device.toString());	
			}	
		}
		mlsBleManager=LsBleManager.newInstance();
	}
	
	
	
	@Override
	public void onResume() {
		System.err.println("Fragment Life cycle = onResume================= ");
		super.onResume();
		
		isConnect=false;
		
		if(device!=null)
		{
			titleTextView.setText("Device : "+device.getDeviceName());
		}
	
		connectBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				if(device!=null)
				{			
					isStartMeasured=false;
					if(!isConnect && connectBtn.getText().equals("Connect"))
					{
						System.err.println("连接设备=====================");
						if("BLOOD_PRESSURE_COMMAND_START_PROTOCOL".equals(device.getProtocolType()))
						{
							infoTextView.setText("");
							mlsBleManager.connectDevice(device, connectListener);
							connectBtn.setEnabled(false);
							showConnectingProgressDialog("Connected device", "Connecting, please wait...");
						}
						else
						{
							showPromptDialog("Prompt", "Failed to connect,protocol type unsupported!");
						}
						
					}
					else
					{
						System.err.println("断开连接=====================");
						isConnect=false;
						connectBtn.setEnabled(true);
						mlsBleManager.disconnectDevice();
						connectBtn.setText("Connect");
					}
				}
				else
				{
					showPromptDialog("Prompt", "Failed to get device info.");
				}
			}
		});
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) 
	{
		inflater.inflate(R.menu.menu_fragment_connect_device, menu);
		clearMenuItem=menu.findItem(R.id.action_clear_item);
		super.onCreateOptionsMenu(menu, inflater);
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
			case R.id.action_clear_item:
			{
				showPromptDialog("Prompt", "Clear all the text messages?");
			}break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	/**
	 * 显示连接等待的提示对话框
	 * @param title
	 * @param message
	 */
	private void showConnectingProgressDialog(String title,String message)
	{
		ContextThemeWrapper ctw = new ContextThemeWrapper(this.getActivity(), android.R.style.Theme_Holo_Light);
		connectingDialog = ProgressDialog.show(ctw, title, message, true);
		connectingDialog.setCancelable(true);
		connectingDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) 
			{
				connectBtn.setEnabled(true);
				
			}
		});
	}
	
	
	/**
	 * 显示启动测量的对话框
	 * @param title
	 * @param message
	 */
	private void showStartMeasuringPromptDialog(String title, String message) 
	{
		ContextThemeWrapper ctw = new ContextThemeWrapper(this.getActivity(), android.R.style.Theme_Holo_Light);
		AlertDialog.Builder builder = new AlertDialog.Builder(ctw).setTitle(title)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() 
				{
					@SuppressLint("SimpleDateFormat") 
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						System.out.println("启动测量====================");
						mlsBleManager.startMeasuring();
						isStartMeasured=true;
						showInfoPanel.setVisibility(View.GONE);
						measureWaitingPanel.setVisibility(View.VISIBLE);
					}

				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						
						System.err.println("cancel measuring========================");
					}		
				}).setMessage(message);
		builder.create().show();
	}
	
	
	/**
	 * 显示提示对话框
	 * @param context
	 * @param title
	 * @param message
	 */
	private void showPromptDialog(String title, String message) 
	{
		ContextThemeWrapper ctw = new ContextThemeWrapper(this.getActivity(), android.R.style.Theme_Holo_Light);
		AlertDialog.Builder promptDialog= new AlertDialog.Builder(ctw)
			.setTitle(title)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				infoTextView.setText("");
			}

		})
		.setMessage(message);
		promptDialog.create().show();
	}
	
}
