/**
 * 
 */
package com.lifesense.ble.ui.menu;



import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lifesense.ble.LsBleManager;
import com.lifesense.ble.PairCallback;
import com.lifesense.ble.SearchCallback;
import com.lifesense.ble.bean.DeviceTypeConstants;
import com.lifesense.ble.bean.DeviceUserInfo;
import com.lifesense.ble.bean.HourSystem;
import com.lifesense.ble.bean.LengthUnit;
import com.lifesense.ble.bean.LsDeviceInfo;
import com.lifesense.ble.bean.PedometerAlarmClock;
import com.lifesense.ble.bean.PedometerUserInfo;
import com.lifesense.ble.bean.SexType;
import com.lifesense.ble.bean.UnitType;
import com.lifesense.ble.bean.WeightUserInfo;
import com.lifesense.ble.commom.BroadcastType;
import com.lifesense.ble.commom.DeviceType;
import com.lifesense.ble.ui.MainActivity;
import com.lifesense.ble.ui.R;
import com.lifesense.ble.ui.ScreenFragmentMessage;
import com.lifesense.ble.ui.tools.AsyncTaskRunner;
import com.lifesense.ble.ui.tools.SettingInfoManager;
import com.lifesense.ble.ui.view.OnDialogClickListener;
import com.lifesense.ble.ui.view.ShowTextDialogFragment;
import com.lifesense.ble.ui.view.scanresults.DeviceAdapter;
import com.lifesense.ui.bean.ActionType;
import com.lifesense.ui.bean.AlarmClockInfo;
import com.lifesense.ui.bean.BleDeviceUserInfo;
import com.lifesense.ui.bean.DistanceUnitType;
import com.lifesense.ui.bean.GenderType;
import com.lifesense.ui.bean.HourFormatType;
import com.lifesense.ui.bean.SettingInfo;
import com.lifesense.ui.bean.WeekStartType;
import com.lifesense.ui.bean.WeightUnitType;


/**
 * @author CaiChiXiang
 *
 */
@SuppressLint("NewApi")
public class SearchDeviceFragment extends Fragment{

	/**
	 * The container view which has layout change animations turned on. In this sample, this view
	 * is a {@link android.widget.LinearLayout}.
	 */
	private ViewGroup mContainerView;
	private View rootView;
	private View loadingView;
	private View scanResultsLayout;
	private TextView progressBarTextView;
 
	private MenuItem searchMenuItem;
	private FragmentManager mFragmentManager;
	private LsBleManager mlsBleManager;
	private List<DeviceType> mScanDeviceType;
	private SettingInfo mSettingInfo;
	private List<String> mProductBarcodes;
	private Context mAppContext;
	private BroadcastType mBroadcastType;
	private DeviceAdapter mListAdapter;
	private List<LsDeviceInfo> mDeviceList;
	private boolean isScanning;
	private Handler updateListViewHanlder;
	private ArrayList<LsDeviceInfo> tempList;
	private  ListView scanResultListView;
	private  AlertDialog userListDialog;
	private List<DeviceUserInfo> mDeviceUserList;
	private TextView showScanFilterTextView;
	private boolean hasScanResults;
	private Timer scanResultTimer;  
	private boolean isPairingProcess;

	private OnDialogClickListener mDialogClickListener=new OnDialogClickListener() {

		@Override
		public void onDialogCancel(ActionType actionType) 
		{
			if(actionType==ActionType.PAIRED_RESULTS
					||actionType==ActionType.ADD_DEVICE)
			{
				showDeviceListFragment();
			}
		}
	};

	private PairCallback mPairCallback=new PairCallback() 
	{

		@Override
		public void onDiscoverUserInfo(List userList) 
		{
			if(userList!=null)
			{
				mDeviceUserList=(List<DeviceUserInfo>)userList;
				showDeviceUserInfo(mDeviceUserList);
			}
		}

		@Override
		public void onPairResults(final LsDeviceInfo lsDevice, final int status)
		{
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() 
				{
					if(lsDevice!=null && status==0)
					{
						scanResultsLayout.setVisibility(View.VISIBLE);
						loadingView.setVisibility(View.GONE);

						ShowTextDialogFragment showInfoDialog=new ShowTextDialogFragment(lsDevice,ActionType.PAIRED_RESULTS,mDialogClickListener);
						showInfoDialog.show(mFragmentManager, "show info");

						AsyncTaskRunner runner = new AsyncTaskRunner(mAppContext,lsDevice);
						runner.execute();	
					}
					else
					{
						scanResultsLayout.setVisibility(View.VISIBLE);
						loadingView.setVisibility(View.GONE);
						showPromptDialog("Prompt", "Pairing failed, please try again",ActionType.PAIRING_PROCESS);
						//showBleConnectMsg("Pairing failed, please try again");
					}
				}
			});
		}
	};

	private SearchCallback mSearchCallback=new SearchCallback() 
	{

		@Override
		public void onSearchResults(final LsDeviceInfo lsDevice) 
		{
			if(lsDevice!=null)
			{
				hasScanResults=true;
				if(scanResultTimer!=null)
				{
					scanResultTimer.cancel();	
				}
				getActivity().runOnUiThread(new Runnable() 
				{
					@SuppressWarnings("unchecked")
					@Override
					public void run() 
					{
						if(loadingView.getVisibility()==View.VISIBLE)
						{
							System.err.println("is here=========");
							loadingView.setVisibility(View.GONE);
							mContainerView.setVisibility(View.GONE);

							initScanResultsListView();
						}
						if(!isDeviceExists(lsDevice.getDeviceName()))
						{
							System.err.println("scan results "+lsDevice.toString());
							tempList.add(lsDevice);
							mListAdapter.add(lsDevice);
							mListAdapter.notifyDataSetChanged();
						}
						else
						{
							updateListViewBackground(lsDevice.getDeviceName());
						}


					}
				});
			}
		}
	};


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		rootView=inflater.inflate(R.layout.fragment_search_device, container, false);
		mContainerView = (ViewGroup) rootView.findViewById(R.id.container);

		scanResultsLayout=rootView.findViewById(R.id.scan_results_list_view);
		scanResultListView=(ListView) scanResultsLayout.findViewById(android.R.id.list);
		loadingView=rootView.findViewById(R.id.loadingPanel);
		progressBarTextView=(TextView) rootView.findViewById(R.id.progress_bar_text_view);

		showScanFilterTextView=(TextView) rootView.findViewById(android.R.id.empty);
		setHasOptionsMenu(true);

		return rootView;
	}


	/**
	 * 
	 */
	private void showDeviceListFragment() 
	{
		MainActivity mainActivity=(MainActivity) getActivity();
		mainActivity.getScreenFragmentHandler();
		Message msg = mainActivity.getScreenFragmentHandler().obtainMessage(); 
		msg.arg1=ScreenFragmentMessage.MSG_SHOW_PAIRED_DEVICE_LIST;
		mainActivity.getScreenFragmentHandler().sendMessage(msg);
		
	}


	public SearchDeviceFragment(){}


	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		System.err.println("Fragment Life cycle = onCreate================= ");
		super.onCreate(savedInstanceState);
		mlsBleManager=LsBleManager.newInstance();
		mAppContext=getActivity().getApplicationContext();
		updateListViewHanlder = new Handler();
		if(mFragmentManager==null)
		{
			mFragmentManager= getFragmentManager();
		}

	}



	@Override
	public void onStart() 
	{
		System.err.println("Fragment Life cycle = onStart================= ");
		isScanning=false;
		mContainerView.removeAllViews();

		//从SharedPreferences首选项文件中读取已经设置的信息
		readScanFilterSetting();

		//read setting info from share preferences
		readSettingInfo();

		mDeviceList=new ArrayList<LsDeviceInfo>();
		mListAdapter = new DeviceAdapter(mAppContext, (ArrayList<LsDeviceInfo>) mDeviceList);
		tempList=new ArrayList<LsDeviceInfo>();
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
		System.err.println("Fragment Life cycle = onStop================= ");
		if(mlsBleManager!=null)
		{
			if(isPairingProcess)
			{
				//cancel pairing process
				mlsBleManager.cancelPairingProcess();
			}
			mlsBleManager.stopSearch();
			
		}
		mSettingInfo.setProductBarcodes(mProductBarcodes);
		//save custom product barcodes
		SettingInfoManager.saveSettingInfo(getActivity().getApplicationContext(), SettingInfo.class.getName(), mSettingInfo);
		if(scanResultTimer!=null)
		{
			scanResultTimer.cancel();	
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) 
	{
		System.err.println("Fragment Life cycle = onCreateOptionsMenu================= ");
		inflater.inflate(R.menu.menu_fragment_search_device, menu);

		searchMenuItem=menu.findItem(R.id.action_search_item);
		if(isScanning)
		{
			mlsBleManager.stopSearch();
			loadingView.setVisibility(View.GONE);
		}

		super.onCreateOptionsMenu(menu, inflater);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) 
		{
		case android.R.id.home:
		{
			// Navigate "up" the demo structure to the launchpad activity.
			return true;
		}
		case R.id.action_search_item:
		{
			if(mContainerView.getVisibility()==View.VISIBLE)
			{
				// Hide the "empty" view since there is now at least one item in the list.
				showScanFilterTextView.setVisibility(View.GONE);
			}

			if(!mlsBleManager.isSupportLowEnergy())
			{
				showPromptDialog("Prompt", "Not support Bluetooth Low Energy",ActionType.PROMPT_INFO);
			}
			if(!mlsBleManager.isOpenBluetooth())
			{
				showPromptDialog("Prompt", "Please turn on Bluetooth",ActionType.PROMPT_INFO);
			}
			else
			{
				hasScanResults=false;
				final View refreshView;
				//
				LayoutInflater inflater = (LayoutInflater)getActivity().getActionBar().getThemedContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				refreshView = inflater.inflate(R.layout.actionbar_search_progress, null);
				refreshView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v)
					{
						MenuItemCompat.setActionView(searchMenuItem, null);
						loadingView.setVisibility(View.GONE);

						mlsBleManager.stopSearch();
						isScanning=false;
						if(!hasScanResults)
						{
							showScanFilterTextView.setVisibility(View.VISIBLE);
						}
					}
				});

				MenuItemCompat.setActionView(searchMenuItem, refreshView);

				if(scanResultsLayout!=null && scanResultsLayout.getVisibility()==View.VISIBLE)
				{
					scanResultsLayout.setVisibility(View.GONE);
				}

				loadingView.setVisibility(View.VISIBLE);

				//search lifesense bluetooth
				mListAdapter.clear();
				tempList.clear();
				isPairingProcess=false;
				isScanning=mlsBleManager.searchLsDevice(mSearchCallback, getDeviceTypes(), getBroadcastType());
				initScanResultsTimer(0);
			}


			return true;
		}
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * 
	 */
	private void readSettingInfo() 
	{
		mSettingInfo=SettingInfoManager.getSettingInfo(getActivity().getApplicationContext(), SettingInfo.class.getName());
		if(mSettingInfo!=null)
		{

		}
		else
		{
			mSettingInfo=new SettingInfo();
			mProductBarcodes=new ArrayList<String>();
		}
	}

	/**
	 * 初始化ScanResults layout and list view
	 *
	 */
	private void initScanResultsListView()
	{
		scanResultsLayout.setVisibility(View.VISIBLE);
		scanResultListView.setVisibility(View.VISIBLE);
		scanResultListView.setAdapter(mListAdapter);
		scanResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) 
			{
				MenuItemCompat.setActionView(searchMenuItem, null);
				mlsBleManager.stopSearch();
				isScanning=false;

				LsDeviceInfo device = (LsDeviceInfo) parent.getAdapter().getItem(position);
				System.err.println("select device info :"+device.toString());
				if(device.getPairStatus()==1)
				{
					
					searchMenuItem.setEnabled(false);
					scanResultsLayout.setVisibility(View.GONE);
					progressBarTextView.setText("");
					progressBarTextView.setMovementMethod(new ScrollingMovementMethod());
					showBleConnectMsg("pairing device,please wait...");
					loadingView.setVisibility(View.VISIBLE);
					
					//设置配对时的用户信息
					if(DeviceTypeConstants.PEDOMETER.equals(device.getDeviceType()))
					{
						setPedometerAlarmClockOnPairingMode();
						setPedometerUserInfoOnPairingMode();
					}
					if(DeviceTypeConstants.FAT_SCALE.equals(device.getDeviceType())
							||DeviceTypeConstants.WEIGHT_SCALE.equals(device.getDeviceType()))
					{
						setWeightUserInfoOnPairingMode();
					}
					
					//set custom broacast Id to device,if need
//					setCustomBroadcastId();
					isPairingProcess=true;			
					mlsBleManager.pairingWithDevice(device, mPairCallback);
//					mlsBleManager.startPairing(device, mPairCallback);
				}
				else if(device.getProtocolType().equals("A4")||device.getProtocolType().equals("GENERIC_FAT"))
				{
					searchMenuItem.setEnabled(true);
					ShowTextDialogFragment showInfoDialog=new ShowTextDialogFragment(device,ActionType.ADD_DEVICE,mDialogClickListener);
					showInfoDialog.show(mFragmentManager, "show info");
//					device.setDeviceId("ffffffff");
					device.setDeviceId(device.getDeviceName());//set the device id to save ,when 
					AsyncTaskRunner runner = new AsyncTaskRunner(mAppContext,device);
					runner.execute();
				}
				else
				{
					searchMenuItem.setEnabled(true);
					String broadcastId=device.getBroadcastID();
					LsDeviceInfo deviceInfo=SettingInfoManager.getPairedDeviceInfoByBroadcastID(mAppContext, broadcastId);
					if(deviceInfo!=null)
					{
						ShowTextDialogFragment showInfoDialog=new ShowTextDialogFragment(deviceInfo,ActionType.PAIRED_RESULTS,mDialogClickListener);
						showInfoDialog.show(mFragmentManager, "show info");
					}
					else
					{
						ShowTextDialogFragment showInfoDialog=new ShowTextDialogFragment(device,ActionType.SCAN_RESULTS,mDialogClickListener);
						showInfoDialog.show(mFragmentManager, "show info");
					}
				}
			}

	
		});

	}

	
	/**
	 * @param msg
	 */
	protected void showBleConnectMsg(final String msg) 
	{
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if(progressBarTextView!=null)
				{
					
					progressBarTextView.append("msg >> "+msg+"\n");
//					progressBarTextView.setTextSize(10);
				}
				
			}
		});
		
		
	}


	private void setCustomBroadcastId() {
		/*
		String customBroadcastID="83A47B0A";
		String deviceName=null;
		List<DeviceType> deviceTypes=new ArrayList<DeviceType>();
		deviceTypes.add(DeviceType.SPHYGMOMANOMETER);
		deviceTypes.add(DeviceType.WEIGHT_SCALE);
		deviceTypes.add(DeviceType.FAT_SCALE);
		*/
		//set custom broadcast id for  blood pressure monitor and weight scale,if need
//		mlsBleManager.setCustomBroadcastID(customBroadcastID, deviceName, deviceTypes);
		
	}
	
	/**
	 * @param deviceId
	 */
	private void setWeightUserInfoOnPairingMode() 
	{
		if(mSettingInfo!=null && mSettingInfo.getDeviceUserInfo()!=null)
		{
			BleDeviceUserInfo userInfo=mSettingInfo.getDeviceUserInfo();
		
			WeightUserInfo weightUserInfo=new WeightUserInfo();
			weightUserInfo.setAge(userInfo.getUserAge());
			weightUserInfo.setHeight(userInfo.getUserHeight());
			
			weightUserInfo.setAthleteActivityLevel(userInfo.getAthleteLevel());	
			weightUserInfo.setGoalWeight(userInfo.getWeightTarget());
			
			if(userInfo.getWeightUnit()==WeightUnitType.LB)
			{
				weightUserInfo.setUnit(UnitType.UNIT_LB); 
			}
			else weightUserInfo.setUnit(UnitType.UNIT_KG);
			
	
			if(userInfo.getUserGender()==GenderType.FEMALE)
			{
				weightUserInfo.setSex(SexType.FEMALE);
			}
			else 	weightUserInfo.setSex(SexType.MALE);
			
			if(userInfo.getAthleteLevel()==0)
			{
				weightUserInfo.setAthlete(false);
			}
			else weightUserInfo.setAthlete(true);
					
			System.out.println("配对过程，设置体重秤用户信息"+weightUserInfo.toString());
			mlsBleManager.setProductUserInfo(weightUserInfo); 
		}
		
	}

	/**
	 * @param deviceId
	 */
	private void setPedometerAlarmClockOnPairingMode() 
	{
		if(mSettingInfo!=null && mSettingInfo.getAlarmClockInfo()!=null)
		{
			
			AlarmClockInfo	alarmClockInfo=mSettingInfo.getAlarmClockInfo();
			
			PedometerAlarmClock peAlarmClock=new PedometerAlarmClock();
			peAlarmClock.setSwitch1(1);
			peAlarmClock.setDay1(alarmClockInfo.getAlarmDays());
			peAlarmClock.setHour1(alarmClockInfo.getAlarmHour());
			peAlarmClock.setMinute1(alarmClockInfo.getAlarmMinute());
			

			mlsBleManager.setPedometerAlarmClock(peAlarmClock);
		}
	
		
	}

	/**
	 * 在测量数据同步过程中设置用户信息
	 * @param deviceId
	 */
	private void setPedometerUserInfoOnPairingMode()
	{
		if(mSettingInfo!=null && mSettingInfo.getDeviceUserInfo()!=null)
		{
			
			BleDeviceUserInfo userInfo=mSettingInfo.getDeviceUserInfo();
			
			PedometerUserInfo pedometerUserInfo=new PedometerUserInfo();
			pedometerUserInfo.setProductUserNumber((byte)1);
			
			pedometerUserInfo.setWeight(userInfo.getUserWeight());
			pedometerUserInfo.setHeight(userInfo.getUserHeight());
		
			pedometerUserInfo.setAge(userInfo.getUserAge());

			if(userInfo.getAthleteLevel()==0)
			{
				pedometerUserInfo.setAthlete(false);
			}
			else pedometerUserInfo.setAthlete(true);
			
			pedometerUserInfo.setAthleteActivityLevel(userInfo.getAthleteLevel());
			
			if(userInfo.getUserGender()==GenderType.FEMALE)
			{
				pedometerUserInfo.setUserGender(SexType.FEMALE);
			}
			else pedometerUserInfo.setUserGender(SexType.MALE);
			
			if(userInfo.getWeekStart()==WeekStartType.MONDAY)
			{
				pedometerUserInfo.setWeekStart(1); //1 for Sunday,2 for Monday
			}
			else 	pedometerUserInfo.setWeekStart(2); //1 for Sunday,2 for Monday
		

			pedometerUserInfo.setWeekTargetSteps(userInfo.getMovingTarget());
			
			if(userInfo.getHourFormat()==HourFormatType.HOUR_12)
			{
				pedometerUserInfo.setHourSystem(HourSystem.HOUR_12);
			}
			else
			{
				pedometerUserInfo.setHourSystem(HourSystem.HOUR_24);
			}
			
			if(userInfo.getDistanceUnit()==DistanceUnitType.KILOMETER)
			{
				pedometerUserInfo.setLengthUnit(LengthUnit.KILOMETER);
			}
			else
			{
				pedometerUserInfo.setLengthUnit(LengthUnit.MILE);
			}
			
			
			System.out.println("配对过程，设置手环用户信息"+pedometerUserInfo.toString());
			mlsBleManager.setPedometerUserInfo(pedometerUserInfo);
			
		}
	}
	

	/**
	 * 在界面显示手动输入的条形码
	 * @param barcodeText
	 */
	/*
	private void addItem(String barcodeText) 
    {
		final  ViewGroup newView = (ViewGroup) LayoutInflater.from(this.getActivity()).inflate(
	                R.layout.list_item_example, mContainerView, false);
	    ((TextView) newView.findViewById(android.R.id.text1)).setText(barcodeText);

        // Set a click listener for the "X" button in the row that will remove the row.
        newView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Remove the row from its parent (the container view).
                // Because mContainerView has android:animateLayoutChanges set to true,
                // this removal is automatically animated.
                mContainerView.removeView(newView);

                //delete barcode from share preferences
                String deleteText=((TextView) newView.findViewById(android.R.id.text1)).getText().toString();
                SettingInfoManager.deleteProductBarcode(deleteText, mSettingInfo);

                // If there are no rows remaining, show the empty view.
                if (mContainerView.getChildCount() == 0) {
                    rootView.findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
                }
            }
        });

        // Because mContainerView has android:animateLayoutChanges set to true,
        // adding this view is automatically animated.
        mContainerView.addView(newView, 0);
    }
	 */

	/**
	 * 显示输入产品条形码对话框
	 */
	/*
	private void showInputBarcodeDialog() 
	{
		ContextThemeWrapper ctw = new ContextThemeWrapper(this.getActivity(), android.R.style.Theme_Holo_Light);
		AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		builder.setTitle("Enter a product barcode");

		// Set up the input
		final EditText input = new EditText(this.getActivity());
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        inputBarcodeText = input.getText().toString();
		        addItem(inputBarcodeText);
		        mProductBarcodes.add(inputBarcodeText);
		    }
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});
		builder.show();
	}
	 */
	/**
	 * 显示提示对话框
	 * @param context
	 * @param title
	 * @param message
	 */
	private void showPromptDialog(String title, String message,final ActionType actionType) 
	{
		ContextThemeWrapper ctw = new ContextThemeWrapper(this.getActivity(), android.R.style.Theme_Holo_Light);
		AlertDialog.Builder promptDialog= new AlertDialog.Builder(ctw)
		.setTitle(title)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Activity appActivity=getActivity();
				if(appActivity!=null)
				{
					appActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() 
						{
							if(scanResultTimer!=null)
							{
								scanResultTimer.cancel();	
							}
							MenuItemCompat.setActionView(searchMenuItem, null);
							loadingView.setVisibility(View.GONE);
							searchMenuItem.setEnabled(true);
							showScanFilterTextView.setVisibility(View.VISIBLE);
						}
					});
				}
				
				
			}

		})
		.setMessage(message);
		promptDialog.create().show();
	}

	/**
	 * 初始化重新启动扫描的定时器，若15秒内，扫描回调接口无响应则返回提示结果
	 * @param delayTime
	 */
	private void initScanResultsTimer(int delayTime)
	{
		int scanTime=15*1000+delayTime;
		if(scanResultTimer!=null)
		{
			scanResultTimer.cancel();	
		}
		scanResultTimer=new Timer();
		scanResultTimer.schedule(new TimerTask() {

			@Override
			public void run() 
			{
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if(!hasScanResults)
						{
							loadingView.setVisibility(View.GONE);

							AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
							.setTitle("Prompt")
							.setPositiveButton("OK",new DialogInterface.OnClickListener() 
							{
								@Override
								public void onClick(DialogInterface dialog,int which) 
								{
									MenuItemCompat.setActionView(searchMenuItem, null);
									mlsBleManager.stopSearch();
									isScanning=false;
									if(!hasScanResults)
									{
										showScanFilterTextView.setVisibility(View.VISIBLE);
									}

								}

							}).setMessage("No results!Please reset the scan filter and try again");

							builder.create().show();

						}

					}
				});


			}
		}, scanTime);
	}

	/**
	 * 
	 */
	private void readScanFilterSetting() 
	{
		PreferenceManager.setDefaultValues(getActivity(), R.xml.setting, false);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String broadcastType=prefs.getString("broadcast_type", null);
		Set<String> deviceTypes=prefs.getStringSet("device_type", null);

		mBroadcastType=BroadcastType.ALL;

		if(broadcastType!=null)
		{
			if(Integer.valueOf(broadcastType)==1)
			{
				mBroadcastType=BroadcastType.PAIR;
			}
			if(Integer.valueOf(broadcastType)==2)
			{
				mBroadcastType=BroadcastType.NORMAL;
			}
		}



		if(deviceTypes!=null && deviceTypes.size()>0)
		{
			showScanFilter(mBroadcastType.toString(),null);

			mScanDeviceType=new ArrayList<DeviceType>();
			for(String value:deviceTypes)
			{
				if(value.equals(DeviceType.FAT_SCALE.toString()))
				{
					mScanDeviceType.add(DeviceType.FAT_SCALE);

				}
				else if(value.equals(DeviceType.HEIGHT_RULER.toString()))
				{
					mScanDeviceType.add(DeviceType.HEIGHT_RULER);
				}
				else if(value.equals(DeviceType.KITCHEN_SCALE.toString()))
				{
					mScanDeviceType.add(DeviceType.KITCHEN_SCALE);
				}
				else if(value.equals(DeviceType.PEDOMETER.toString()))
				{
					mScanDeviceType.add(DeviceType.PEDOMETER);
				}
				else if(value.equals(DeviceType.SPHYGMOMANOMETER.toString()))
				{
					mScanDeviceType.add(DeviceType.SPHYGMOMANOMETER);
				}
				else if(value.equals(DeviceType.WEIGHT_SCALE.toString()))
				{
					mScanDeviceType.add(DeviceType.WEIGHT_SCALE);
				}

				System.err.println("my device type multi choose:"+value);
			}
			showScanFilter(mBroadcastType.toString(),mScanDeviceType);
		}


	}

	private void showScanFilter(String msg, List<DeviceType> deviceTypes)
	{
		if(deviceTypes==null)
		{
			showScanFilterTextView.setText("");
			showScanFilterTextView.append("Scan filter"+"\n");
			showScanFilterTextView.append("-------------------------------------------"+"\n");
			showScanFilterTextView.append("Broadcast Type:"+msg+"\n");
			showScanFilterTextView.append("-------------------------------------------"+"\n");
			showScanFilterTextView.append("Device Type:"+"\n");
			showScanFilterTextView.append("-------------------------------------------"+"\n");
		}
		else
		{
			for(DeviceType value:deviceTypes)
			{
				String str=value.toString().toLowerCase().replace("_", " ");
				showScanFilterTextView.append(str+"\n");
			}
		}


	}

	private boolean isDeviceExists(String name) 
	{
		if(name==null || name.length()==0)
		{
			return false;
		}
		if(tempList!=null && tempList.size()>0)
		{
			for (int i = 0; i < tempList.size(); i++)
			{
				LsDeviceInfo tempDeInfo=tempList.get(i);
				if (tempDeInfo!=null && tempDeInfo.getDeviceName()!=null 
						&& tempDeInfo.getDeviceName().equals(name)) 
				{
					return true;
				}
			}
			return false;
		}
		else return false;
	}


	private void setListViewBackgroundColor(final View view) 
	{
		view.setBackgroundColor(Color.GREEN);
		updateListViewHanlder.postDelayed(new Runnable() {
			@Override
			public void run() {
				view.setBackgroundColor(Color.WHITE);
			}
		}, 1000);
	}

	@SuppressWarnings("unchecked")
	private void updateListViewBackground(String name) 
	{
		if (!tempList.isEmpty()) 
		{
			for (LsDeviceInfo dev : tempList) 
			{
				if (dev != null && dev.getDeviceName()!=null && dev.getDeviceName().equals(name)) 
				{
					final View view = scanResultListView.getChildAt(mListAdapter.getPosition(dev));
					if (view != null) 
					{
						setListViewBackgroundColor(view);
					}
				}
			}
		}
	}


	@SuppressWarnings("null")
	private void showDeviceUserInfo(List<DeviceUserInfo> userList) 
	{
		ContextThemeWrapper ctw = new ContextThemeWrapper(this.getActivity(), android.R.style.Theme_Holo_Light);
		// Strings to Show In Dialog with Radio Buttons
		final CharSequence[] items ;
		if(userList!=null && userList.size()>0)
		{
			items=new String[userList.size()];
			for(DeviceUserInfo  user:userList)
			{
				if(user!=null)
				{
					user.getDeviceId();
					int index=userList.indexOf(user);
					if(user.getUserName().isEmpty())
					{
						items[index]=user.getUserNumber()+"   "+"unknow";
					}
					else 
					{
						items[index]=user.getUserNumber()+"   "+user.getUserName();
					}
				}	
			}
		}
		else items=new String[2];
		// Creating and Building the Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		builder.setTitle("Select User Number");
		builder.setSingleChoiceItems(items, -1,new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				//不通过解除绑定，直接更换设备的用户
				userListDialog.dismiss();
				showInputDialog(which+1);
			}
		});
		userListDialog = builder.create();
		userListDialog.show();
	}

	private void showInputDialog(final int userNumber)
	{
		ContextThemeWrapper ctw = new ContextThemeWrapper(this.getActivity(), android.R.style.Theme_Holo_Light);
		AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		builder.setTitle("Enter your user name(Less than 16 characters)");
		// Set up the input
		final EditText input = new EditText(ctw);
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		builder.setView(input);
		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				String userName=input.getText().toString();
				if(userName!=null && userName.length()<16)
				{
					writeUserAccountToDevice(userNumber,input.getText().toString());
				}
				else
				{
					dialog.cancel();
					Toast.makeText(getActivity(), "Error,entering invalid...", Toast.LENGTH_LONG);
				}

			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				showDeviceUserInfo(mDeviceUserList);
			}
		});
		builder.create();
		builder.show();
	}

	private void writeUserAccountToDevice(int userNumber,String name) 
	{
		mlsBleManager.bindDeviceUser(userNumber, name.trim());
	}

	/**
	 * test case
	 */
	private List<DeviceType> getDeviceTypes()
	{	
		if(mScanDeviceType==null)
		{
			mScanDeviceType=new ArrayList<DeviceType>();
			mScanDeviceType.add(DeviceType.SPHYGMOMANOMETER);
			mScanDeviceType.add(DeviceType.FAT_SCALE);
			mScanDeviceType.add(DeviceType.WEIGHT_SCALE);
			mScanDeviceType.add(DeviceType.HEIGHT_RULER);
			mScanDeviceType.add(DeviceType.PEDOMETER);
			mScanDeviceType.add(DeviceType.KITCHEN_SCALE);
		}
		System.err.println("当前扫描的设备类型："+mScanDeviceType.toString());
		return mScanDeviceType;
	}

	private BroadcastType getBroadcastType()
	{
		System.err.println("当前扫描的广播类型："+mBroadcastType);
		return mBroadcastType;
	}

}
