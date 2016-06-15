/**
 * 
 */
package com.lifesense.ble.ui.view.alarmclock;

import java.util.ArrayList;

import com.lifesense.ui.bean.AlarmClockInfo;

/**
 * @author CaiChiXiang
 *
 */
public class AlarmClockOverview {

	private String time;
	private String days;
	private boolean enable;
	// ArrayList to store child objects
    private AlarmClockInfo alarmClockInfo;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getDays() {
		return days;
	}
	public void setDays(String days) {
		this.days = days;
	}
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public AlarmClockInfo getAlarmClockInfo() {
		return alarmClockInfo;
	}
	public void setAlarmClockInfo(AlarmClockInfo alarmClockInfo) {
		this.alarmClockInfo = alarmClockInfo;
	}
	@Override
	public String toString() {
		return "AlarmClockOverview [time=" + time + ", days=" + days
				+ ", enable=" + enable + ", alarmClockInfo=" + alarmClockInfo
				+ "]";
	}
    
	
	
    
	
}
