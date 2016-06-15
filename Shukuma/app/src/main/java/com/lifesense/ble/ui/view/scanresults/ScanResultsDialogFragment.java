/*******************************************************************************
 * Copyright (c) 2013 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package com.lifesense.ble.ui.view.scanresults;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.lifesense.ble.bean.LsDeviceInfo;
import com.lifesense.ble.ui.R;
import com.lifesense.ble.ui.view.scanresults.PullToRefreshListView.OnRefreshListener;

/**
 * ScannerFragment class scan required BLE devices and shows them in a list. This class scans and filter devices with standard BLE Service UUID and devices with custom BLE Service UUID It contains a
 * list and a button to scan/cancel. There is a interface {@link OnDeviceSelectedListener} which is implemented by activity in order to receive selected device. The scanning will continue for 5
 * seconds and then stop
 */
public class ScanResultsDialogFragment extends DialogFragment {
	private final static String TAG = "ScannerFragment";

	private OnDeviceSelectedListener mListener;
	private DeviceListAdapter mAdapter;
	private Button mScanButton;

	private boolean mIsScanning = false;


	/* package */static final int NO_RSSI = -1000;

	public ScanResultsDialogFragment(){};
	
	public ScanResultsDialogFragment(Context context)
	{
		mAdapter = new DeviceListAdapter(context);
	}

	/**
	 * This will make sure that {@link OnDeviceSelectedListener} interface is implemented by activity.
	 */
	/*
	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		try {
			this.mListener = (OnDeviceSelectedListener) activity;
		} catch (final ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnDeviceSelectedListener");
		}
	}
	*/

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	/**
	 * When dialog is created then set AlertDialog with list and button views.
	 */
	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) 
	{
		ContextThemeWrapper ctw = new ContextThemeWrapper(this.getActivity(), android.R.style.Theme_Holo_Light);
		final AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_device_selection, null);
		final ListView listview = (ListView) dialogView.findViewById(android.R.id.list);
		
		/*
		 * 使用下拉刷新的listView控件
		 * */
		/*
		final PullToRefreshListView listview = (PullToRefreshListView) dialogView.findViewById(android.R.id.list);
		// MANDATORY: Set the onRefreshListener on the list. You could also use
		// listView.setOnRefreshListener(this); and let this Activity
		// implement OnRefreshListener.
		listview.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// Your code to refresh the list contents goes here

				// for example:
				// If this is a webservice call, it might be asynchronous so
				// you would have to call listView.onRefreshComplete(); when
				// the webservice returns the data
 		 	adapter.loadData();

				// Make sure you call listView.onRefreshComplete()
				// when the loading is done. This can be done from here or any
				// other place, like on a broadcast receive from your loading
				// service or the onPostExecute of your AsyncTask.

				// For the sake of this sample, the code will pause here to
				// force a delay when invoking the refresh
				listview.postDelayed(new Runnable() {


					@Override
					public void run() {
						listview.onRefreshComplete();
					}
				}, 2000);
			}
		});

		adapter = new PullToRefreshListViewSampleAdapter() {};
		listView.setAdapter(adapter);
		*/
		/**/
		
		listview.setEmptyView(dialogView.findViewById(android.R.id.empty));
		listview.setAdapter(mAdapter);

		builder.setTitle(R.string.scanner_title);
		final AlertDialog dialog = builder.setView(dialogView).create();
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) 
			{
				dialog.dismiss();
				LsDeviceInfo lsDevice = (LsDeviceInfo) mAdapter.getItem(position);
				mListener.onDeviceSelected(lsDevice);
			}
		});

		mScanButton = (Button) dialogView.findViewById(R.id.action_cancel);
		mScanButton.setText(R.string.scanner_action_cancel);
		
		mScanButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				if (v.getId() == R.id.action_cancel) 
				{
					//stop ble search
					if(mListener!=null)
					{
						mListener.onStopSearch();
					}
					
				}
			}
		});
		
		return dialog;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		
		mListener.onDialogCanceled();
	}

	public void registerOnDeviceSelectedListener(OnDeviceSelectedListener onSelectedListener)
	{
		mListener=onSelectedListener;
	}
	
	public void addSearchResultItem(LsDeviceInfo lsDeviceInfo)
	{
		mAdapter.addOrUpdateDevice(lsDeviceInfo);
	}
	
	public void clearListView()
	{
		mAdapter.clearDevices();
	}
}
