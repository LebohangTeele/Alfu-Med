/**
 * 
 */
package com.lifesense.ble.ui.view.scanresults;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lifesense.ble.LsBleManager;
import com.lifesense.ble.bean.LsDeviceInfo;
import com.lifesense.ble.ui.R;

/**
 * @author CaiChiXiang
 *
 */
public class ScanResultsFragment extends Fragment {

	private Context appContext;
	private View rootView;
	private ListView scanResultsListView;
	private ArrayList<LsDeviceInfo> mBleDeviceList;
	private LsBleManager mLsBleManager;
	private DeviceListAdapter mListAdapter;
	
	
	public ScanResultsFragment(){}
	
	public ScanResultsFragment(Context context)
	{
		appContext=context;
		mListAdapter = new DeviceListAdapter(appContext);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
	
		rootView=inflater.inflate(R.layout.fragment_scan_results, container, false);
		scanResultsListView = (ListView) rootView.findViewById(android.R.id.list);
		
		return rootView;
		
	}

	@Override
	public void onStart() 
	{
		mLsBleManager=LsBleManager.newInstance();
		initListView();
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	
	public void addSearchResultItem(LsDeviceInfo lsDeviceInfo)
	{
		mListAdapter.addOrUpdateDevice(lsDeviceInfo);
	}
	
	private void initListView() 
	{
		
		scanResultsListView.setAdapter(mListAdapter);
		scanResultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) 
			{
				
			}
		});
	}
	

}
