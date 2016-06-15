/**
 * 
 */
package com.lifesense.ble.ui.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lifesense.ble.bean.LsDeviceInfo;
import com.lifesense.ble.ui.R;
import com.lifesense.ui.bean.ActionType;

/**
 * @author CaiChiXiang
 *
 */
public class ShowTextDialogFragment  extends DialogFragment{

	private LsDeviceInfo lsDeviceInfo;
	private TextView infoTextView;
	private OnDialogClickListener mDialogClickListener;
	private ActionType mActionType;
	
	public ShowTextDialogFragment() {
		
	}
	
	public ShowTextDialogFragment(LsDeviceInfo deviceInfo,ActionType actionType,OnDialogClickListener onClickListener)
	{
		lsDeviceInfo=deviceInfo;
		mActionType=actionType;
		mDialogClickListener=onClickListener;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		ContextThemeWrapper ctw = new ContextThemeWrapper(this.getActivity(), android.R.style.Theme_Holo_Light);
		final AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_show_info, null);
		infoTextView = (TextView) dialogView.findViewById(R.id.show_info_text_view);
		
		if(mActionType==ActionType.SCAN_RESULTS)
		{
			builder.setTitle("Scan Results");
			showScanResults(lsDeviceInfo);
		}
		else
		{
			builder.setTitle("Paired Device Info");
			showPairResults(lsDeviceInfo);
		}
		
		
		
		
		final AlertDialog dialog = builder.setView(dialogView).create();
		
		Button cancelBtn = (Button) dialogView.findViewById(R.id.action_cancel);
	
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				if (v.getId() == R.id.action_cancel) 
				{
					dialog.dismiss();
				}
				mDialogClickListener.onDialogCancel(mActionType);
			}
		});
		
		return dialog;
	}
	
	/**
	 * @param lsDeviceInfo2
	 */
	private void showScanResults(LsDeviceInfo device) 
	{

		if(device!=null)
		{       
			
			infoTextView.append("unpaired device information"+"\n");
			infoTextView.append("------------------------------------"+"\n");
			infoTextView.append("deviceName: "+device.getDeviceName()+"\n");
			infoTextView.append("broadcastID: "+device.getBroadcastID()+"\n");
			infoTextView.append("deviceType: "+device.getDeviceType()+"\n"); 
			infoTextView.append("modelNumber: "+device.getModelNumber()+"\n");  	      	  
			infoTextView.append("protocolType: "+device.getProtocolType()+"\n");
		}
		else
		{
			infoTextView.append("Failed to show scan results,is null"+"\n");
		}	 	       


	}

	private void showPairResults(LsDeviceInfo device) 
	{
		if(device!=null)
		{   
			infoTextView.append("paired device information"+"\n");
			infoTextView.append("------------------------------------"+"\n");
			infoTextView.append("deviceName: "+device.getDeviceName()+"\n");
			infoTextView.append("broadcastID: "+device.getBroadcastID()+"\n");
			infoTextView.append("deviceType: "+device.getDeviceType()+"\n"); 
			infoTextView.append("password: "+device.getPassword()+"\n"); 
			infoTextView.append("deviceID: "+device.getDeviceId()+"\n");
			infoTextView.append("deviceSN: "+device.getDeviceSn()+"\n");
			infoTextView.append("manufactureName: "+device.getManufactureName()+"\n");	        	
			infoTextView.append("modelNumber: "+device.getModelNumber()+"\n");  	      	  
			infoTextView.append("firmwareVersion: "+device.getFirmwareVersion()+"\n");
			infoTextView.append("hardwareVersion: "+device.getHardwareVersion()+"\n");   
			infoTextView.append("softwareVersion: "+device.getSoftwareVersion()+"\n");
			infoTextView.append("UserNumber: "+device.getDeviceUserNumber()+"\n");
			infoTextView.append("maxUserQuantity: "+device.getMaxUserQuantity()+"\n");
			infoTextView.append("protocolType: "+device.getProtocolType()+"\n");
		}
		else
		{
			infoTextView.append("Failed paired!Please try again"+"\n");
		}	 	       
	}
	

	
}
