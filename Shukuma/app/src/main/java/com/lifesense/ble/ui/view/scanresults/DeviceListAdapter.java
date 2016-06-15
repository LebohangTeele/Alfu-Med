/*******************************************************************************
 * Copyright (c) 2013 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA. Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. This heading must NOT be removed from the file.
 ******************************************************************************/
package com.lifesense.ble.ui.view.scanresults;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lifesense.ble.bean.LsDeviceInfo;
import com.lifesense.ble.ui.R;

/**
 * DeviceListAdapter class is list adapter for showing scanned Devices name, address and RSSI image based on RSSI values.
 */
public class DeviceListAdapter extends BaseAdapter {
	private static final int TYPE_TITLE = 0;
	private static final int TYPE_ITEM = 1;
	private static final int TYPE_EMPTY = 2;

	private final ArrayList<LsDeviceInfo> mListBondedValues = new ArrayList<LsDeviceInfo>();
	private final Context mContext;

	public DeviceListAdapter(Context context) 
	{
		mContext = context;
	}

	/*
	public void addBondedDevice(LsDeviceInfo device) 
	{
		mListBondedValues.add(device);
		notifyDataSetChanged();
	}
	*/
	/**
	 * Looks for the device with the same address as given one in the list of bonded devices. If the device has been found it updates its RSSI value.
	 * 
	 * @param address
	 *            the device address
	 * @param rssi
	 *            the RSSI of the scanned device
	 */
	public void updateRssiOfBondedDevice(String address, int rssi) 
	{
		if(mListBondedValues!=null && mListBondedValues.size()>0)
		{
			for(LsDeviceInfo lsDevice:mListBondedValues)
			{
				if(address.equals(lsDevice.getMacAddress()))
				{
					final int indexInBonded = mListBondedValues.indexOf(lsDevice);
					if (indexInBonded >= 0) 
					{
						LsDeviceInfo previousDevice = mListBondedValues.get(indexInBonded);
						previousDevice.setRssi(rssi);
						notifyDataSetChanged();
					}
				}
			}
		}
	}

	/**
	 * If such device exists on the bonded device list, this method does nothing. If not then the device is updated (rssi value) or added.
	 * 
	 * @param device
	 *            the device to be added or updated
	 */
	public void addOrUpdateDevice(LsDeviceInfo device) 
	{
		//pairing broadcast mode device
		LsDeviceInfo currentdevice=isExist(device.getMacAddress(), mListBondedValues);
		if(currentdevice!=null)
		{
			int indexInBonded = mListBondedValues.indexOf(currentdevice);
			
			if(indexInBonded!=-1)
			{
				System.err.println("更新扫描结果 ======================"+device.getMacAddress()+";index="+indexInBonded);
				LsDeviceInfo previousDevice = mListBondedValues.get(indexInBonded);
				previousDevice.setRssi(device.getRssi());
				notifyDataSetChanged();
				return;
			}

		}
		System.err.println("扫描结果 ======================"+device.toString());
		mListBondedValues.add(device);
		notifyDataSetChanged();
	}

	public void clearDevices() {
		mListBondedValues.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		final int bondedCount = mListBondedValues.size() + 1; // 1 for the title
		return bondedCount;
	}

	@Override
	public Object getItem(int position) {
		final int bondedCount = mListBondedValues.size() + 1; // 1 for the title

		if (position == 0)
			return R.string.scanner_subtitle_bonded;
		if (position < bondedCount)
			return mListBondedValues.get(position - 1);
		else
			return R.string.scanner_subtitle__not_bonded;
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return getItemViewType(position) == TYPE_ITEM;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0)
			return TYPE_TITLE;

		if (!mListBondedValues.isEmpty() && position == mListBondedValues.size() + 1)
			return TYPE_TITLE;

		return TYPE_ITEM;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View oldView, ViewGroup parent)
	{
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		final int type = getItemViewType(position);

		View view = oldView;
		ImageView infoView = null;
		switch (type) 
		{
		case TYPE_EMPTY:
			if (view == null) {
				view = inflater.inflate(R.layout.device_list_empty, parent, false);
			}
			break;
		case TYPE_TITLE:
			if (view == null) {
				view = inflater.inflate(R.layout.device_list_title, parent, false);
			}
//			final TextView title = (TextView) view;
//			title.setText((Integer) getItem(position));
			break;
		default:
			if (view == null) {
				view = inflater.inflate(R.layout.device_list_row, parent, false);
				
				final ViewHolder holder = new ViewHolder();
				holder.name = (TextView) view.findViewById(R.id.name);
				holder.address = (TextView) view.findViewById(R.id.address);
				infoView= (ImageView) view.findViewById(R.id.rssi);
				infoView.setTag(position);
				infoView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						System.err.println("is here now ==================="+v.getTag().toString());
						
					}
				});
				holder.rssi =infoView;
				
				view.setTag(holder);
			}

			final LsDeviceInfo device = (LsDeviceInfo) getItem(position);
			final ViewHolder holder = (ViewHolder) view.getTag();
			if(device.getPairStatus()==0 && infoView!=null)
			{
				infoView.setBackgroundColor(Color.RED);
			}
			
			final String name = device.getDeviceName();
			holder.name.setText(name != null ? "Broadcast: "+name : mContext.getString(R.string.not_available));
			holder.address.setText("Address: "+device.getMacAddress());
		
//			if (device.getRssi()!= ScannerFragment.NO_RSSI) 
//			{
				final int rssiPercent = (int) (100.0f * (127.0f + device.getRssi()) / (127.0f + 20.0f));
//				holder.rssi.setImageLevel(rssiPercent);
				holder.rssi.setVisibility(View.VISIBLE);
//			} else {
//				holder.rssi.setVisibility(View.GONE);
//			}
		
			break;
		}

		return view;
	}

	private class ViewHolder {
		private TextView name;
		private TextView address;
		private ImageView rssi;
	}
	
	private LsDeviceInfo isExist(String address,List<LsDeviceInfo> deviceList)
	{
		if(deviceList!=null && deviceList.size()>0)
		{
			for(LsDeviceInfo lsDevice:deviceList)
			{
				if(address.equals(lsDevice.getMacAddress()))
				{
					return lsDevice;
				}
			}
			return null;
		}
		else return null;
	}
}
