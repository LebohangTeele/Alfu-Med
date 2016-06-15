package com.lifesense.ble.ui;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.lifesense.ble.LsBleManager;
import com.lifesense.ble.bean.LsDeviceInfo;
import com.lifesense.ble.ui.menu.AlarmClockFragment;
import com.lifesense.ble.ui.menu.DeviceUserFragment;
import com.lifesense.ble.ui.menu.PairedDeviceListFragment;
import com.lifesense.ble.ui.menu.ScanBarcodeFragment;
import com.lifesense.ble.ui.menu.ScanFilterFragment;
import com.lifesense.ble.ui.menu.SearchDeviceFragment;
import com.lifesense.ble.ui.menu.adapter.NavDrawerListAdapter;
import com.lifesense.ble.ui.menu.model.NavDrawerItem;
import com.lifesense.ble.ui.view.ConnectDeviceFragment;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

	private static final int MSG_FRAGMENT_POSITION_USER_INFO = 0;
	private static final int MSG_FRAGMENT_POSITION_SCAN_FILTER = 1;
	private static final int MSG_FRAGMENT_POSITION_ALARM_CLOCK = 2;
	private static final int MSG_FRAGMENT_POSITION_SCAN_BARCODE = 3;
	private static final int MSG_FRAGMENT_POSITION_SEARCH_DEVICE = 4;
	private static final int MSG_FRAGMENT_POSITION_PAIRED_DEVICE_LIST = 5;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;

	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	private Handler screenFragmentHandler;
	private LsBleManager mLsBleManager;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

		setContentView(R.layout.activity_main);

		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array

		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));

		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));

		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));


		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));


		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));

		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);


		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(5, true);
		}

		//	
		mLsBleManager = LsBleManager.newInstance();
		mLsBleManager.initialize(getApplicationContext());

		screenFragmentHandler = new ScreenFragmentHandler(getMainLooper());
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {


		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

		return super.onPrepareOptionsMenu(menu);
	}


	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);

	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	private void displayView(Fragment targetFragment) {
		if (targetFragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction ft = fragmentManager.beginTransaction();
			ft.replace(R.id.frame_container, targetFragment).commit();
		}

	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 */
	private void displayView(int position, boolean firstLoad) {


		Fragment fragment = null;
		switch (position) {
			case MSG_FRAGMENT_POSITION_USER_INFO:
//			fragment = new UserInfoFragment();
				fragment = new DeviceUserFragment();
				break;
			case MSG_FRAGMENT_POSITION_SCAN_FILTER:
				fragment = new ScanFilterFragment();

				break;
			case MSG_FRAGMENT_POSITION_ALARM_CLOCK:
				fragment = new AlarmClockFragment();
				break;
			case MSG_FRAGMENT_POSITION_SCAN_BARCODE:
				fragment = new ScanBarcodeFragment();
				break;
			case MSG_FRAGMENT_POSITION_SEARCH_DEVICE:
				fragment = new SearchDeviceFragment();
				break;
			case MSG_FRAGMENT_POSITION_PAIRED_DEVICE_LIST:
				fragment = new PairedDeviceListFragment();
				break;

			default:
				break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction ft = fragmentManager.beginTransaction();

			if (!firstLoad) {
				ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
			}


			ft.replace(R.id.frame_container, fragment).commit();

			updateDrawerLayout(position);


		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	/**
	 * @param position
	 */
	private void updateDrawerLayout(int position) {
		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		mDrawerList.setSelection(position);
		setTitle(navMenuTitles[position]);

		mDrawerLayout.closeDrawer(mDrawerList);

	}

	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://com.lifesense.ble.ui/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://com.lifesense.ble.ui/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
	}

	/**
	 * Slide menu item click listener
	 */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {
			System.err.println("选择菜单=============");
			// display view for selected nav drawer item
			displayView(position, false);
		}
	}

	public Handler getScreenFragmentHandler() {
		return screenFragmentHandler;
	}

	public class ScreenFragmentHandler extends Handler {
		public ScreenFragmentHandler(Looper myLooper) {
			super(myLooper);
		}

		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
				case ScreenFragmentMessage.MSG_SCREEN_FRAGMENT_CONNECT_DEVICE: {
					LsDeviceInfo deviceInfo = (LsDeviceInfo) msg.obj;

					Fragment connectFragment = new ConnectDeviceFragment();

					final Bundle args = new Bundle();
					args.putParcelable("LS_DEVICE_INFO", deviceInfo);
					connectFragment.setArguments(args);
					displayView(connectFragment);
				}
				break;
				case ScreenFragmentMessage.MSG_ADD_PRODUCT_BARCODE: {
					Fragment searchFragment = new SearchDeviceFragment();
					displayView(searchFragment);
					updateDrawerLayout(MSG_FRAGMENT_POSITION_SEARCH_DEVICE);
				}
				break;
				case ScreenFragmentMessage.MSG_SHOW_PAIRED_DEVICE_LIST: {
					Fragment deviceListFragment = new PairedDeviceListFragment();
					displayView(deviceListFragment);
					updateDrawerLayout(MSG_FRAGMENT_POSITION_PAIRED_DEVICE_LIST);
				}
				break;
				case ScreenFragmentMessage.MSG_ADD_DEVICE: {
					Fragment addDeviceFragment = new SearchDeviceFragment();
					displayView(addDeviceFragment);
					updateDrawerLayout(MSG_FRAGMENT_POSITION_SEARCH_DEVICE);
				}
				break;
			}
		}
	}

}
