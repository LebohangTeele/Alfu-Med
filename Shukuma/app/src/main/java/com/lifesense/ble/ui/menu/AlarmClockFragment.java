/**
 * 
 */
package com.lifesense.ble.ui.menu;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

import com.lifesense.ble.bean.PedometerAlarmClock;
import com.lifesense.ble.ui.R;
import com.lifesense.ble.ui.tools.SettingInfoManager;
import com.lifesense.ui.bean.AlarmClockInfo;
import com.lifesense.ui.bean.SettingInfo;

/**
 * @author CaiChiXiang
 *
 */
public class AlarmClockFragment extends Fragment implements OnClickListener{

	private enum UpdateTextViewType
		{
			APPEND,
			REMOVE,
		};
	private static final String[] daysArray=new String[]
			{
				"Sun","Mon","Tue","Wed","Thu","Fri","Sat"
			};
	private View rootView;
	private CheckedTextView mMondayCheckBox;
	private CheckedTextView mTuesdayCheckBox;
	private CheckedTextView mWednesdayCheckBox;
	private CheckedTextView mThursdayCheckBox;
	private CheckedTextView mFridayCheckBox;
	private CheckedTextView mSaturdayCheckBox;
	private CheckedTextView mSundayCheckBox;


	private List<Integer>alarmWeeks;
	private TimePicker alarmClockTimePicker;
	private int alarmHour=0;
	private int alarmMinute=0;
	private int alarmDays=0;

	private SettingInfo mSettingInfo;
	private TextView alarmClockTitleTextView;
	private TextView daysTextView;
	private MenuItem saveAlarmClockMenuItem;
	private Switch enableAlarmClock;
	private View alarmClockSettingView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{

		//		//	    	 getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		rootView=inflater.inflate(R.layout.fragment_alarm_clock, container, false);

		mMondayCheckBox=(CheckedTextView)rootView.findViewById(R.id.mondayCheckBox);
		mTuesdayCheckBox=(CheckedTextView) rootView.findViewById(R.id.tuesdayCheckBox);
		mWednesdayCheckBox=(CheckedTextView) rootView.findViewById(R.id.wednesdayCheckBox);
		mThursdayCheckBox=(CheckedTextView) rootView.findViewById(R.id.thursdayCheckBox);
		mFridayCheckBox=(CheckedTextView) rootView.findViewById(R.id.fridayCheckBox);
		mSaturdayCheckBox=(CheckedTextView) rootView.findViewById(R.id.saturdayCheckBox);
		mSundayCheckBox=(CheckedTextView) rootView.findViewById(R.id.sundayCheckBox);
		
		alarmClockTitleTextView=(TextView) rootView.findViewById(R.id.alarm_clock_time_text_view);
		alarmClockTimePicker=(TimePicker) rootView.findViewById(R.id.alarm_clock_time_picker);
		daysTextView=(TextView) rootView.findViewById(R.id.repeat_days_text_view);
		
		enableAlarmClock=(Switch) rootView.findViewById(R.id.enable_alarm_clock_switch);
		alarmClockSettingView=rootView.findViewById(R.id.alarm_clock_setting_view_layout);
		setHasOptionsMenu(true);

		return rootView;
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) 
	{
		inflater.inflate(R.menu.menu_fragment_alarm_clock, menu);
		saveAlarmClockMenuItem=menu.findItem(R.id.action_save_alarm_clock_item);
		saveAlarmClockMenuItem.setVisible(false);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
		case R.id.action_save_alarm_clock_item:
		{
			enableAlarmClock.setChecked(false);
			saveAlarmClockMenuItem.setVisible(false);
		
			
			
			AlarmClockInfo newAlarmClockInfo=new AlarmClockInfo();
			alarmWeeks.clear();
			
			if(mMondayCheckBox.isChecked())
			{			
				alarmWeeks.add(PedometerAlarmClock.MONDAY);
				newAlarmClockInfo.setMondayChecked(true);
			}
			if(mTuesdayCheckBox.isChecked())
			{			
				alarmWeeks.add(PedometerAlarmClock.TUESDAY);
				newAlarmClockInfo.setTuesdayChecked(true);
			}
			if(mWednesdayCheckBox.isChecked())
			{		
				alarmWeeks.add(PedometerAlarmClock.WEDNESDAY);
				newAlarmClockInfo.setWednesdayChecked(true);
			}
			if(mThursdayCheckBox.isChecked())
			{	
				alarmWeeks.add(PedometerAlarmClock.THURSDAY);
				newAlarmClockInfo.setThursdayChecked(true);
			}
			if(mFridayCheckBox.isChecked())
			{
				alarmWeeks.add(PedometerAlarmClock.FRIDAY);
				newAlarmClockInfo.setFridayChecked(true);
			}
			if(mSaturdayCheckBox.isChecked())
			{
				alarmWeeks.add(PedometerAlarmClock.SATURDAY);
				newAlarmClockInfo.setSaturdayChecked(true);
			}
			if(mSundayCheckBox.isChecked())
			{
				alarmWeeks.add(PedometerAlarmClock.SUNDAY);
				newAlarmClockInfo.setSundayChecked(true);
			}

			int dayCount=getAlarmClockDayCount(alarmWeeks);
			saveAlarmClockSetting(alarmHour,alarmMinute,dayCount,newAlarmClockInfo);
			return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}


	

	@Override
	public void onStart() 
	{
		super.onStart();
		alarmWeeks=new ArrayList<Integer>();
		//restore alarm clock setting info
		restoreAlarmClockSetting();
			

		Calendar mcurrentTime = Calendar.getInstance();
		int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
		int minute = mcurrentTime.get(Calendar.MINUTE);
		alarmClockTimePicker.setCurrentHour(hour);
		alarmClockTimePicker.setCurrentMinute(minute);
		alarmClockTimePicker.setIs24HourView(true);
		alarmClockTimePicker.setOnTimeChangedListener(new OnTimeChangedListener() {

			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) 
			{
				alarmHour=hourOfDay;
				alarmMinute=minute;
				alarmClockTitleTextView.setText(hourOfDay+":"+minute);
			}
		});
		
		
		
		enableAlarmClock.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				View alarmClockSettingView=rootView.findViewById(R.id.alarm_clock_setting_view_layout);
			
				if(isChecked)
				{
					//show the alarm clock setting view
					alarmClockSettingView.setVisibility(View.VISIBLE);
					saveAlarmClockMenuItem.setVisible(true);
					ScaleAnimation scaleAnimation = new ScaleAnimation(0.2f, 1f, 0.2f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
					        ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
					scaleAnimation.setDuration(450);
					alarmClockSettingView.startAnimation(scaleAnimation);
				
				}
				else
				{
					saveAlarmClockMenuItem.setVisible(false);
					alarmClockSettingView.clearAnimation();
					//display the alarm clock setting view
					alarmClockSettingView.setVisibility(View.GONE);
				}
				
			}
		});
		
		/*
		if(alarmDays>0 && alarmHour>0 && alarmMinute>0)
		{
			timePickerEditText.setText(alarmHour+":"+alarmMinute);
		}
		//hide softKeyboard input
		timePickerEditText.setInputType(InputType.TYPE_NULL);
		timePickerEditText.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Calendar mcurrentTime = Calendar.getInstance();
				int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
				int minute = mcurrentTime.get(Calendar.MINUTE);
				TimePickerDialog mTimePicker;
				mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() 
				{
					@Override
					public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) 
					{
						timePickerEditText.setText( selectedHour + ":" + selectedMinute);
						alarmHour=selectedHour;
						alarmMinute=selectedMinute;
					}
				}, hour, minute, true);//Yes 24 hour time
				mTimePicker.setTitle("Select Time");
				mTimePicker.show();

			}
		});
		 */
		mMondayCheckBox.setOnClickListener(this);
		mSundayCheckBox.setOnClickListener(this);
		mTuesdayCheckBox.setOnClickListener(this);
		mThursdayCheckBox.setOnClickListener(this);
		mWednesdayCheckBox.setOnClickListener(this);
		mFridayCheckBox.setOnClickListener(this);
		mSaturdayCheckBox.setOnClickListener(this);
	}




	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) 
	{
		int viewId=v.getId();
		boolean isChecked=false;
		String dayTitle="";
		if(viewId==R.id.sundayCheckBox)
		{
			dayTitle=daysArray[0];
			isChecked = ((CheckedTextView)v.findViewById(viewId)).isChecked();
			if(isChecked)
			{
				updateTextViewValue(daysTextView, dayTitle, UpdateTextViewType.REMOVE);
				mSundayCheckBox.setChecked(false);
			}
			else
			{
				updateTextViewValue(daysTextView,dayTitle , UpdateTextViewType.APPEND);
				mSundayCheckBox.setChecked(true);	
			}
		}

		else if(viewId==R.id.mondayCheckBox)
		{
			dayTitle=daysArray[1];
			isChecked = ((CheckedTextView)v.findViewById(viewId)).isChecked();
			if(isChecked)
			{
				updateTextViewValue(daysTextView, dayTitle, UpdateTextViewType.REMOVE);
				mMondayCheckBox.setChecked(false);
			}
			else
			{
				updateTextViewValue(daysTextView,dayTitle , UpdateTextViewType.APPEND);
				mMondayCheckBox.setChecked(true);	
			}
		}

		else if(viewId==R.id.tuesdayCheckBox)
		{
			dayTitle=daysArray[2];
			isChecked = ((CheckedTextView)v.findViewById(viewId)).isChecked();
			if(isChecked)
			{
				updateTextViewValue(daysTextView, dayTitle, UpdateTextViewType.REMOVE);
				mTuesdayCheckBox.setChecked(false);
			}
			else
			{
				updateTextViewValue(daysTextView,dayTitle , UpdateTextViewType.APPEND);
				mTuesdayCheckBox.setChecked(true);	
			}
		}
		else if(viewId==R.id.wednesdayCheckBox)
		{
			dayTitle=daysArray[3];
			isChecked=((CheckedTextView)v.findViewById(viewId)).isChecked();
			if(isChecked)
			{
				updateTextViewValue(daysTextView, dayTitle, UpdateTextViewType.REMOVE);
				mWednesdayCheckBox.setChecked(false);
			}
			else
			{
				updateTextViewValue(daysTextView,dayTitle , UpdateTextViewType.APPEND);
				mWednesdayCheckBox.setChecked(true);	
			}
		}
		else if(viewId==R.id.thursdayCheckBox)
		{
			dayTitle=daysArray[4];
			isChecked = ((CheckedTextView)v.findViewById(viewId)).isChecked();
			if(isChecked)
			{
				updateTextViewValue(daysTextView, dayTitle, UpdateTextViewType.REMOVE);
				mThursdayCheckBox.setChecked(false);
			}
			else
			{
				updateTextViewValue(daysTextView,dayTitle , UpdateTextViewType.APPEND);
				mThursdayCheckBox.setChecked(true);	
			}

		}
		else if(viewId==R.id.fridayCheckBox)
		{
			dayTitle=daysArray[5];
			isChecked=((CheckedTextView)v.findViewById(viewId)).isChecked();
			if(isChecked)
			{
				updateTextViewValue(daysTextView, dayTitle, UpdateTextViewType.REMOVE);
				mFridayCheckBox.setChecked(false);
			}
			else
			{
				updateTextViewValue(daysTextView,dayTitle , UpdateTextViewType.APPEND);
				mFridayCheckBox.setChecked(true);	
			}
		}
		else if(viewId==R.id.saturdayCheckBox)
		{
			dayTitle=daysArray[6];
			isChecked=((CheckedTextView)v.findViewById(viewId)).isChecked();
			if(isChecked)
			{
				updateTextViewValue(daysTextView, dayTitle, UpdateTextViewType.REMOVE);
				mSaturdayCheckBox.setChecked(false);
			}
			else
			{
				updateTextViewValue(daysTextView,dayTitle , UpdateTextViewType.APPEND);
				mSaturdayCheckBox.setChecked(true);	
			}
		}
	}
	private void restoreAlarmClockDaySetting(AlarmClockInfo alarmClockInfo)
	{
		mMondayCheckBox.setChecked(alarmClockInfo.isMondayChecked());
		mTuesdayCheckBox.setChecked(alarmClockInfo.isTuesdayChecked());
		mWednesdayCheckBox.setChecked(alarmClockInfo.isWednesdayChecked());
		mThursdayCheckBox.setChecked(alarmClockInfo.isThursdayChecked());
		mFridayCheckBox.setChecked(alarmClockInfo.isFridayChecked());

		mSaturdayCheckBox.setChecked(alarmClockInfo.isSaturdayChecked());
		mSundayCheckBox.setChecked(alarmClockInfo.isSundayChecked());

		mMondayCheckBox.setEnabled(true);
		mTuesdayCheckBox.setEnabled(true);
		mWednesdayCheckBox.setEnabled(true);
		mThursdayCheckBox.setEnabled(true);
		mFridayCheckBox.setEnabled(true);
		mSaturdayCheckBox.setEnabled(true);
		mSundayCheckBox.setEnabled(true);
		
	    String dayTitle="";
		
		if(alarmClockInfo.isSundayChecked())
		{
			dayTitle=daysArray[0];
			updateTextViewValue(daysTextView,dayTitle , UpdateTextViewType.APPEND);
		}
		
		if(alarmClockInfo.isMondayChecked())
		{
			dayTitle=daysArray[1];
			updateTextViewValue(daysTextView,dayTitle , UpdateTextViewType.APPEND);
		}
		
		if(alarmClockInfo.isTuesdayChecked())
		{
			dayTitle=daysArray[2];
			updateTextViewValue(daysTextView,dayTitle , UpdateTextViewType.APPEND);
		}
		
		if(alarmClockInfo.isWednesdayChecked())
		{
			dayTitle=daysArray[3];
			updateTextViewValue(daysTextView,dayTitle , UpdateTextViewType.APPEND);
		}
		if(alarmClockInfo.isThursdayChecked())
		{
			dayTitle=daysArray[4];
			updateTextViewValue(daysTextView,dayTitle , UpdateTextViewType.APPEND);
		}
		
		if(alarmClockInfo.isFridayChecked())
		{
			dayTitle=daysArray[5];
			updateTextViewValue(daysTextView,dayTitle , UpdateTextViewType.APPEND);
		}
		
		if(alarmClockInfo.isSaturdayChecked())
		{
			dayTitle=daysArray[6];
			updateTextViewValue(daysTextView,dayTitle , UpdateTextViewType.APPEND);
		}
		

	}

	/**
	 * 
	 */
	private void restoreAlarmClockSetting() 
	{

		mSettingInfo=SettingInfoManager.getSettingInfo(getActivity().getApplicationContext(), SettingInfo.class.getName());
		if(mSettingInfo!=null)
		{
			AlarmClockInfo alarmClockInfo=mSettingInfo.getAlarmClockInfo();
			if(alarmClockInfo!=null)
			{
				alarmHour=alarmClockInfo.getAlarmHour();
				alarmMinute=alarmClockInfo.getAlarmMinute();
				alarmDays=alarmClockInfo.getAlarmDays();
				alarmClockTitleTextView.setText(alarmHour+":"+alarmMinute);
				daysTextView.setText("");
				restoreAlarmClockDaySetting(alarmClockInfo);
			}
			else
			{
				alarmClockInfo=new AlarmClockInfo();
				//set default value
				alarmClockTitleTextView.setText("Undefined");
				daysTextView.setText("");
			}
		}
		else
		{
			//set default value
			alarmClockTitleTextView.setText("Undefined");
			daysTextView.setText("");
			mSettingInfo=new SettingInfo();
		}
	}


	private  void hideSoftKeyboard(Activity context) 
	{
		InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputManager != null)
		{
			inputManager.hideSoftInputFromWindow(context.getWindow().getDecorView().getApplicationWindowToken(), 0);
		}
		context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}

	/**
	 * @param newAlarmClockInfo 
	 * @param alarmHour2
	 * @param alarmMinute2
	 * @param day1
	 */
	private void saveAlarmClockSetting(int hour, int minute,
			int day, AlarmClockInfo newAlarmClockInfo) 
	{
		if(hour==0 || minute==0)
		{
			showPromptDialog("Warning", "please set the time");
		}
		if(day==0)
		{
			showPromptDialog("Warning", "please select the day");
		}
		else
		{
			Toast.makeText(getActivity(), "Done! Alarm Clock("+hour+":"+minute+")", Toast.LENGTH_SHORT).show();
		}

		//save alarm clock to SharedPreferences
		newAlarmClockInfo.setAlarmDays(day);
		newAlarmClockInfo.setAlarmHour(hour);
		newAlarmClockInfo.setAlarmMinute(minute);
		mSettingInfo.setAlarmClockInfo(newAlarmClockInfo);

		SettingInfoManager.saveSettingInfo(getActivity().getApplicationContext(), SettingInfo.class.getName(), mSettingInfo);


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

			}

		})
		.setMessage(message);
		promptDialog.create().show();
	}
	
	private String getDaysTitleString(String oldStr)
	{
		if(oldStr!=null)
		{
			oldStr=oldStr.replace(",,", ",");
			if(oldStr.startsWith(","))
			{
				oldStr=oldStr.substring(1);
			}
			if(oldStr.endsWith(","))
			{
				oldStr=oldStr.substring(0, oldStr.length()-1);
			}
			return oldStr;
		}
		else return "";
		
	}

	private void updateTextViewValue(TextView textView,String str,UpdateTextViewType updateType)
	{
		AlphaAnimation animation=null;
		if(updateType==UpdateTextViewType.APPEND)
		{
			// fade in animation
			animation = new AlphaAnimation( 1.0f , 0.0f );
			String oldText=textView.getText().toString();
			if(oldText.endsWith(",")||oldText.length()==0)
			{
				textView.append(str);
				textView.append(",");
			}
			else
			{
				textView.append(",");
				textView.append(str);
			}
	
			
		}
		else if(updateType==UpdateTextViewType.REMOVE)
		{
			//fade out animation
			animation= new AlphaAnimation(0.0f , 1.0f ); 
			String sourceText=textView.getText().toString();
			sourceText = sourceText.replaceAll(str, ""); // remove "X:"
			String value=getDaysTitleString(sourceText);
			textView.setText(value);
		}
		
		
		if(animation!=null)
		{
			textView.startAnimation(animation);
		}

	}
	
	/**
	 * @param alarmWeeks2
	 * @return
	 */
	private int getAlarmClockDayCount(List<Integer> alarmWeeks2)
	{
		int day1=0;
		if(alarmWeeks2!=null && alarmWeeks2.size()>0)
		{
			for(Integer day:alarmWeeks2)
			{
				day1=day1+day;
			}
		}
		return day1;
	}

	
	/**
	 * 数字倒数动画
	 * @param tv
	 * @param count
	 */
	private void countDown(final TextView tv, final int  count) 
	{
		if (count == 0) 
		{ 
			tv.setText(""); //Note: the TextView will be visible again here.
			return;
		} 
		tv.setText(String.valueOf(count));
		AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
		animation.setDuration(1000);
		animation.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation anim) {
				countDown(tv, count - 1);
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
		});
		tv.startAnimation(animation);
	}
	 
	
}
