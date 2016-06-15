/**
 * 
 */
package com.lifesense.ble.ui.menu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.decode.CaptureActivityHandler;
import com.google.zxing.client.android.decode.InactivityTimer;
import com.google.zxing.client.android.decode.Intents;
import com.google.zxing.client.android.result.ResultHandler;
import com.google.zxing.client.android.result.ResultHandlerFactory;
import com.google.zxing.client.android.view.CustomViewfinderView;
import com.google.zxing.client.android.view.ViewfinderView;
import com.lifesense.ble.ui.MainActivity;
import com.lifesense.ble.ui.R;
import com.lifesense.ble.ui.ScreenFragmentMessage;
import com.lifesense.ble.ui.tools.SettingInfoManager;
import com.lifesense.ui.bean.SettingInfo;

/**
 * @author CaiChiXiang
 *
 */
@SuppressLint("NewApi")
public class ScanBarcodeFragment extends Fragment implements Callback{

	private static final String TAG = ScanBarcodeFragment.class.getSimpleName();
	private CaptureActivityHandler handler;
	private CustomViewfinderView viewfinderView;

	private boolean hasSurface;
	private Collection<BarcodeFormat> decodeFormats;
	private Map<DecodeHintType,?> decodeHints;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private Context appContext;
	private  View rootView;
	private  SurfaceView surfaceView;
	private CameraManager mCameraManager;
	private Result savedResultToShow;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		Log.e(TAG, "run onCreateView()==================");
		

		Window window = this.getActivity().getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		appContext=this.getActivity().getApplicationContext();

		rootView = inflater.inflate(R.layout.fragment_scan_barcode, container, false);
		//		 viewfinderView = (ViewfinderView) rootView.findViewById(R.id.viewfinder_view);

		viewfinderView = (CustomViewfinderView) rootView.findViewById(R.id.viewfinder_view);

		surfaceView = (SurfaceView)rootView. findViewById(R.id.preview_view);

		hasSurface = false;

		inactivityTimer = new InactivityTimer(this.getActivity());

		return rootView;
	}


	@Override
	public void onResume() 
	{
		super.onResume();
		
//		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		
		// CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
		// want to open the camera driver and measure the screen size if we're going to show the help on
		// first launch. That led to bugs where the scanning rectangle was the wrong size and partially
		// off screen.
		mCameraManager =new CameraManager(appContext);
		viewfinderView.setCameraManager(mCameraManager);
		handler = null;
		viewfinderView.setVisibility(View.VISIBLE);

		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) 
		{
			// The activity was paused but not stopped, so the surface still exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);
		}
		else 
		{
			// Install the callback and wait for surfaceCreated() to init the camera.
			surfaceHolder.addCallback(this);
		}

		inactivityTimer.onResume();
		decodeFormats = null;
		characterSet = null;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.e(TAG, "run onDestroyView()==================");
		inactivityTimer.shutdown();
	}

	

	@Override
	public void onPause() 
	{
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		
		mCameraManager.closeDriver();
		if (!hasSurface) 
		{
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}


	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) 
		{
			hasSurface = true;		
			initCamera(holder);
		}
	}


	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		Log.e(TAG, "surfaceDestroyed");
		hasSurface = false;
		mCameraManager.stopPreview();
	}

	private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
		// Bitmap isn't used yet -- will be used soon
		if (handler == null) {
			savedResultToShow = result;
		} else {
			if (result != null) {
				savedResultToShow = result;
			}
			if (savedResultToShow != null) 
			{
				Message message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow);
				handler.sendMessage(message);
			}
			savedResultToShow = null;
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) 
	{
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (mCameraManager.isOpen()) {
			Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			mCameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, mCameraManager);
			}
			decodeOrStoreSavedBitmap(null, null);
		} catch (IOException ioe) {
			Log.w(TAG, ioe);

		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			Log.w(TAG, "Unexpected error initializing camera", e);
		}
	}

	public CameraManager getCameraManager()
	{
		return mCameraManager;
	}

	public CustomViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		Log.e(TAG, "drawViewfinder");
		viewfinderView.drawViewfinder();

	}

	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder, int, int, int)
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	/**
	 * 接收由CaptureActivityHandler返回的扫描结果
	 *
	 * @param rawResult The contents of the barcode.
	 * @param scaleFactor amount by which thumbnail was scaled
	 * @param barcode   A greyscale bitmap of the camera data which was decoded.
	 */
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) 
	{
		inactivityTimer.onActivity();
		viewfinderView.setVisibility(View.GONE);

		ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this.getActivity(), rawResult);

		System.err.println("toString====="+rawResult.toString());

		System.err.println("getBarcodeFormat===="+rawResult.getBarcodeFormat().toString());
		System.err.println("getType====="+resultHandler.getType().toString());

		Toast.makeText(appContext, "Scan Results:"+rawResult.getText(), Toast.LENGTH_LONG).show();
		
		//保存产品条形码
		SettingInfoManager.saveProductBarcode(getActivity().getApplicationContext(), SettingInfo.class.getName(), rawResult.getText());
		
		MainActivity mainActivity=(MainActivity) getActivity();
		mainActivity.getScreenFragmentHandler();
		Message msg = mainActivity.getScreenFragmentHandler().obtainMessage(); 
		msg.arg1=ScreenFragmentMessage.MSG_ADD_PRODUCT_BARCODE;
		mainActivity.getScreenFragmentHandler().sendMessage(msg);
	}


}
