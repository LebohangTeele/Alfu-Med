package com.lifesense.ble.ui;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.lifesense.ble.LsBleManager;
import com.lifesense.ble.ReceiveDataCallback;
import com.lifesense.ble.bean.BloodPressureData;
import com.lifesense.ble.bean.DeviceTypeConstants;
import com.lifesense.ble.bean.HeightData;
import com.lifesense.ble.bean.PedometerData;
import com.lifesense.ble.bean.WeightData_A2;
import com.lifesense.ble.bean.WeightData_A3;
import com.lifesense.ble.ui.menu.PairedDeviceListFragment;

public class ShowMeasureDataActivity extends Activity {

	private TextView showTextView;
    private List<BloodPressureData> mBloodPressureDataList;
	private List<WeightData_A3> mWeightDataList;
	private List<WeightData_A2> weightData_A2s;
	private List<PedometerData> pedometerDatas;
	private List<HeightData> heightDatas;
	
	private ReceiveDataCallback reDataCallback=new ReceiveDataCallback()
	{
		
		public void onReceiveWeightDta_A2(WeightData_A2 wData_A2)
		{
			showWeightMeasureData_A2(wData_A2);
		};	
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_show_measure_data);
		showTextView=(TextView) findViewById(R.id.measureDataView);
		showTextView.setMovementMethod(new ScrollingMovementMethod());
		String deviceType=getIntent().getStringExtra("DeviceType");

		showMeasureData(deviceType);
		LsBleManager.newInstance().stopDataReceiveService();
		
//		DeviceInfoWithDate currentDevicve=getIntent().getParcelableExtra("pairedDeviceInfo");
//		LsBleManager.newInstance().addMeasureDevice(currentDevicve.getLsDeviceInfo());
//		LsBleManager.newInstance().startDataReceiveService(reDataCallback);
	}

	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LsBleManager.newInstance().stopDataReceiveService();
	}



	private void showMeasureData(String deviceType)
	{
		if(deviceType!=null)
		{
			if(deviceType.equals(DeviceTypeConstants.SPHYGMOMAN_METER))
			{
				mBloodPressureDataList=PairedDeviceListFragment.bpDataList;
				showBloodPressureMeasureData(mBloodPressureDataList);
				}
			else if(deviceType.equals(DeviceTypeConstants.FAT_SCALE))
			{
				mWeightDataList=PairedDeviceListFragment.wDataList_A3;
				showWeightMeasureData(mWeightDataList);
				}
			else if(deviceType.equals(DeviceTypeConstants.WEIGHT_SCALE))
			{
				weightData_A2s=PairedDeviceListFragment.wDataList_A2;
				showWeightMeasureData_A2(weightData_A2s);
				}
			else if(deviceType.equals(DeviceTypeConstants.PEDOMETER))
			{
				pedometerDatas=PairedDeviceListFragment.pDataList;
				showPedometerMeasureData(pedometerDatas);
				
				}
			else if(deviceType.equals(DeviceTypeConstants.HEIGHT_RULER))
			{
				heightDatas=PairedDeviceListFragment.hDataList;
				showHeightMeasureData(heightDatas);
				}
		   else if(deviceType.equals(DeviceTypeConstants.SPHYGMOMAN_METER))
		   {
			   mBloodPressureDataList=PairedDeviceListFragment.bpDataList;
				showBloodPressureMeasureData(mBloodPressureDataList);
		   		}
			
		}
		else showTextView.setText("no measure data now!");
		
	}


	


	private void showHeightMeasureData(List<HeightData> hDatas) 
	{
		if(hDatas!=null)
		{
			showTextView.append("------The total number of records:"+hDatas.size()+"----"+"\n");
			for(HeightData hData:hDatas)
			{ 
				  showTextView.append("Record Number:"+hDatas.indexOf(hData)+"\n");
	        	  showTextView.append("Measuring time："+ hData.getDate()+"\n");
	        	  showTextView.append("DeviceSn："+ hData.getDeviceSn()+"\n");
	        	  showTextView.append("UserNumber："+ hData.getUserNo()+"\n");
	        	  showTextView.append("Height："+ hData.getHeight()+"\n");
	        	  showTextView.append("Unit ："+ hData.getUnit()+"\n");
	        	  showTextView.append("Battery："+ hData.getBattery()+"\n");
	        	  showTextView.append("----------------------------------"+"\n");
			}
			
		}
		else 
		{
			  showTextView.append("No measured Record"+"\n");
			}
		
	}


	private void showPedometerMeasureData(List<PedometerData> pDatas)
	{
		if(pDatas!=null)
		{
			showTextView.append("------The total number of records:"+pDatas.size()+"----"+"\n");
			for(PedometerData pData:pDatas)
			{ 
				  showTextView.append("Record Number:"+pDatas.indexOf(pData)+"\n");
				  showTextView.append("Measuring time："+ pData.getDate()+"\n");
	        	  showTextView.append("DeviceSN："+ pData.getDeviceSn()+"\n");
	        	  showTextView.append("UserNumber："+ pData.getUserNo()+"\n");
	        	  showTextView.append("WalkSteps："+ pData.getWalkSteps()+"\n");
	        	  showTextView.append("RunSteps ："+ pData.getRunSteps()+"\n");
	        	  showTextView.append("Calories："+ pData.getCalories()+"\n");
	        	  showTextView.append("ExerciseTime："+ pData.getExerciseTime()+"\n");
	        	  showTextView.append("Distance："+ pData.getDistance()+"\n");
				  showTextView.append("Battery："+ pData.getBattery()+"\n");
				  showTextView.append("SleepStatus："+ pData.getSleepStatus()+"\n");
				  showTextView.append("IntensityLevel："+ pData.getIntensityLevel()+"\n");
	        	  showTextView.append("----------------------------------"+"\n");
			}
			
		}
		else 
		{
			  showTextView.append("No measured Record"+"\n");
			}
		
	}

	private void showWeightMeasureData_A2(final WeightData_A2 wData)
	{
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if(wData!=null)
				{
					showTextView.append("------new measured data----"+"\n");
					//			for(WeightData_A2 wData:wData_A2s)
					//			{ 
					//				  showTextView.append("Record Number:"+wData_A2s.indexOf(wData)+"\n");
					showTextView.append("Measuring time："+ wData.getDate()+"\n");
					showTextView.append("DeviceSN："+ wData.getDeviceSn()+"\n");
					showTextView.append("UserNumber："+ wData.getUserNo()+"\n");
					showTextView.append("Weight："+ wData.getWeight()+"\n");
					showTextView.append("Fat rate："+ wData.getPbf()+"\n");
					showTextView.append("Resistance_1："+ wData.getResistance_1()+"\n");
					showTextView.append("Resistance_2："+ wData.getResistance_2()+"\n");
					showTextView.append("Unit："+ wData.getDeviceSelectedUnit()+"\n");
					showTextView.append("VoltageData："+ wData.getVoltageData()+"\n");
					showTextView.append("Battery level:"+(int)wData.getBattery()+"\n");
					showTextView.append("----------------------------------"+"\n");
					//			}

				}
				else 
				{
					showTextView.append("No measured Record"+"\n");
				}

				
			}
		});
		
	}
	private void showWeightMeasureData_A2(List<WeightData_A2> wData_A2s)
	{
		if(wData_A2s!=null)
		{
			showTextView.append("------The total number of records:"+wData_A2s.size()+"----"+"\n");
			for(WeightData_A2 wData:wData_A2s)
			{ 
				  showTextView.append("Record Number:"+wData_A2s.indexOf(wData)+"\n");
				  showTextView.append("Measuring time："+ wData.getDate()+"\n");
		    	  showTextView.append("DeviceSN："+ wData.getDeviceSn()+"\n");
		    	  showTextView.append("UserNumber："+ wData.getUserNo()+"\n");
		    	  showTextView.append("Weight："+ wData.getWeight()+"\n");
		    	  showTextView.append("Fat rate："+ wData.getPbf()+"\n");
		    	  showTextView.append("Resistance_1："+ wData.getResistance_1()+"\n");
		    	  showTextView.append("Resistance_2："+ wData.getResistance_2()+"\n");
		    	  showTextView.append("Unit："+ wData.getDeviceSelectedUnit()+"\n");
		    	  showTextView.append("VoltageData："+ wData.getVoltageData()+"\n");
		    	  showTextView.append("Battery level:"+(int)wData.getBattery()+"\n");
		    	  if(wData.getResistance_2()>0)
		    	  {
		    		  showTextView.append("BodyFatRatio："+ wData.getBodyFatRatio()+"\n");
		    		  showTextView.append("BodyWaterRatio："+ wData.getBodyWaterRatio()+"\n");
		    		  showTextView.append("MuscleMassRatio："+ wData.getMuscleMassRatio()+"\n");
		    		  showTextView.append("BoneDensity："+ wData.getBoneDensity()+"\n");
		    		  showTextView.append("BasalMetabolism："+ wData.getBasalMetabolism()+"\n");
		    	  }
	        	  showTextView.append("----------------------------------"+"\n");
			}
			
		}
		else 
		{
			  showTextView.append("No measured Record"+"\n");
			}
		
	}


	private void showWeightMeasureData(List<WeightData_A3> wDataList) {
		
		if(wDataList!=null && wDataList.size()>0)
		{
			showTextView.append("------The total number of records:"+wDataList.size()+"----"+"\n");
			for(WeightData_A3 wData:wDataList)
			{
				 showTextView.append("Record Number:"+wDataList.indexOf(wData)+"\n");
				 showTextView.append("Measuring time："+ wData.getDate()+"\n");
				 showTextView.append("DeviceSn：" + wData.getDeviceSn() + "\n");
		    	  showTextView.append("mpedance："+ wData.getImpedance()+"\n");
		    	  showTextView.append("UserNumber："+ wData.getUserId()+"\n");
		    	  showTextView.append("isAppendMeasurement："+ wData.isAppendMeasurement()+"\n");
		    	  showTextView.append("Battery："+ wData.getBattery()+"\n");
		    	  showTextView.append("WeightStatus："+ wData.getWeightStatus()+"\n");
		    	  showTextView.append("ImpedanceStatus："+ wData.getImpedanceStatus()+"\n");
		    	  showTextView.append("AccuracyStatus："+ wData.getAccuracyStatus()+"\n");    	  
		    	  showTextView.append("Weight："+ wData.getWeight()+"\n");
		    	  showTextView.append("BodyFatRatio："+ wData.getBodyFatRatio()+"\n");
		    	  showTextView.append("BodyWaterRatio："+ wData.getBodyWaterRatio()+"\n");
		    	  showTextView.append("VisceralFatLevel："+ wData.getVisceralFatLevel()+"\n");
		    	  showTextView.append("MuscleMassRatio："+ wData.getMuscleMassRatio()+"\n");
		    	  showTextView.append("BoneDensity："+ wData.getBoneDensity()+"\n");
		    	  showTextView.append("BasalMetabolism："+ wData.getBasalMetabolism()+"\n");
		    	  showTextView.append("Unit："+ wData.getDeviceSelectedUnit()+"\n");
		    	  showTextView.append("----------------------------------"+"\n");
			}
		}
		else {showTextView.append("No measured records"+"\n");}
	}


	private void showBloodPressureMeasureData(List<BloodPressureData> bpDataList) 
	{
		if(bpDataList!=null && bpDataList.size()>0)
		{
			showTextView.append("------The total number of records:"+bpDataList.size()+"----"+"\n");
			for(BloodPressureData bData:bpDataList)
			{
				showTextView.append("Record Number:"+bpDataList.indexOf(bData)+"\n");
				showTextView.append("DeviceSn：" + bData.getDeviceSn() + "\n");
				showTextView.append("BroadcastID：" + bData.getBroadcastId() + "\n");
				showTextView.append("Measuring time：" + bData.getDate() + "\n");
				showTextView.append("userNumber：" + bData.getUserId() + "\n");
				showTextView.append("Systolic：" + bData.getSystolic() + "\n");
				showTextView.append("Diastolic ：" + bData.getDiastolic() + "\n");
				showTextView.append("PulseRate：" + bData.getPulseRate() + "\n");
				showTextView.append("MeanArterialPressure：" + bData.getMeanArterialPressure() + "\n");
				showTextView.append("Unit：" + bData.getDeviceSelectedUnit() + "\n");
				showTextView.append("Battery：" + bData.getBattery() + "\n");
				showTextView.append("----------------------------------"+"\n");
				}
			}
		else {
			showTextView.append("No measured record"+"\n");}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	
		return true;
	}
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    overridePendingTransition(R.anim.dync_in_from_right, R.anim.dync_out_to_left );
	}
	
	
	
	public void doClick(View view)
	{
		switch (view.getId()) 
		{
			case R.id.backBtn:
			{
				onBackPressed();
				}break;
		}
	}

}
