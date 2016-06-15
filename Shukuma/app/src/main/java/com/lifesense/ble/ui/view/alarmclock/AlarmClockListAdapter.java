/**
 * 
 */
package com.lifesense.ble.ui.view.alarmclock;

import java.util.List;

import com.lifesense.ble.bean.LsDeviceInfo;
import com.lifesense.ble.ui.R;
import com.lifesense.ui.bean.AlarmClockInfo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;


/**
 * @author CaiChiXiang
 *
 */
public class AlarmClockListAdapter  extends ArrayAdapter<AlarmClockOverview>{

	 private List<AlarmClockOverview> mData;
	 private int mLayoutViewResourceId;
	 private AlarmClockOverview currentSelectItem;
	 private Context appContext;

	public AlarmClockListAdapter(Context context, int layoutViewResourceId,
			List<AlarmClockOverview> data) 
	{
		super(context, layoutViewResourceId, data);
		mData = data;
		appContext=context;
		mLayoutViewResourceId = layoutViewResourceId;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{ 
		currentSelectItem = mData.get(position);
		final AlarmClockInfo alarmClock=currentSelectItem.getAlarmClockInfo();
		if(convertView == null) 
		{
			LayoutInflater inflater =  ((Activity) getContext()).getLayoutInflater();
			convertView = inflater.inflate(mLayoutViewResourceId, parent, false);
		}
		
		Switch enableAlarmClock=(Switch) convertView.findViewById(R.id.enable_alarm_clock_switch);
		TextView daysTextView=(TextView) convertView.findViewById(R.id.repeat_days_text_view);
		TextView timeTextView=(TextView) convertView.findViewById(R.id.alarm_clock_time_text_view);
		if(alarmClock!=null)
		{
			enableAlarmClock.setVisibility(View.VISIBLE);
			timeTextView.setText(alarmClock.getAlarmHour()+":"+alarmClock.getAlarmMinute());
			daysTextView.setText(getRepeatTextViewContent(alarmClock));
		}
		else
		{
			enableAlarmClock.setVisibility(View.GONE);
			//set default value
			timeTextView.setText("No alarm clock time");
			daysTextView.setText("");
		}
		

		return convertView;
	}

	/**
	 * @param alarmClock
	 */
	private String getRepeatTextViewContent(AlarmClockInfo alarmClock)
	{
		StringBuffer dayBuffer=new StringBuffer();
		if(alarmClock.isSundayChecked())
		{
			dayBuffer.append("Sun");
			dayBuffer.append(",");
		}
		if(alarmClock.isMondayChecked())
		{
			dayBuffer.append("Mon");
			dayBuffer.append(",");
		}
		
		if(alarmClock.isTuesdayChecked())
		{
			dayBuffer.append("Tue");
			dayBuffer.append(",");
		}
		if(alarmClock.isWednesdayChecked())
		{
			dayBuffer.append("Wed");
			dayBuffer.append(",");
		}
		if(alarmClock.isThursdayChecked())
		{
			dayBuffer.append("Thu");
			dayBuffer.append(",");
		}
		if(alarmClock.isFridayChecked())
		{
			dayBuffer.append("Fri");
			dayBuffer.append(",");
		}
		if(alarmClock.isSaturdayChecked())
		{
			dayBuffer.append("Sat");
		}
		
		if(dayBuffer.lastIndexOf(",")!=-1)
		{
			int index=dayBuffer.lastIndexOf(",");
			dayBuffer.replace(index, index, "");
		}
		return dayBuffer.toString();
	}
	
	
	  
	

	
}
