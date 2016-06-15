/**
 * 
 */
package com.lifesense.ui.bean;


/**
 * @author CaiChiXiang
 *
 */
public class AlarmClockInfo {

	private boolean mondayChecked=false;
	private boolean tuesdayChecked=false;
	private boolean wednesdayChecked=false;
	private boolean thursdayChecked=false;
	private boolean fridayChecked=false;
	private boolean saturdayChecked=false;
	private boolean sundayChecked=false;
	private boolean workDayChecked=false;
	private boolean allWeeksChecked=false;
	
	private int alarmHour=0;
	private int alarmMinute=0;
	private int alarmDays=0;
	
	private boolean enable;

	public boolean isMondayChecked() {
		return mondayChecked;
	}

	public void setMondayChecked(boolean mondayChecked) {
		this.mondayChecked = mondayChecked;
	}

	public boolean isTuesdayChecked() {
		return tuesdayChecked;
	}

	public void setTuesdayChecked(boolean tuesdayChecked) {
		this.tuesdayChecked = tuesdayChecked;
	}

	public boolean isWednesdayChecked() {
		return wednesdayChecked;
	}

	public void setWednesdayChecked(boolean wednesdayChecked) {
		this.wednesdayChecked = wednesdayChecked;
	}

	public boolean isThursdayChecked() {
		return thursdayChecked;
	}

	public void setThursdayChecked(boolean thursdayChecked) {
		this.thursdayChecked = thursdayChecked;
	}

	public boolean isFridayChecked() {
		return fridayChecked;
	}

	public void setFridayChecked(boolean fridayChecked) {
		this.fridayChecked = fridayChecked;
	}

	public boolean isSaturdayChecked() {
		return saturdayChecked;
	}

	public void setSaturdayChecked(boolean saturdayChecked) {
		this.saturdayChecked = saturdayChecked;
	}

	public boolean isSundayChecked() {
		return sundayChecked;
	}

	public void setSundayChecked(boolean sundayChecked) {
		this.sundayChecked = sundayChecked;
	}

	public boolean isWorkDayChecked() {
		return workDayChecked;
	}

	public void setWorkDayChecked(boolean workDayChecked) {
		this.workDayChecked = workDayChecked;
	}

	public boolean isAllWeeksChecked() {
		return allWeeksChecked;
	}

	public void setAllWeeksChecked(boolean allWeeksChecked) {
		this.allWeeksChecked = allWeeksChecked;
	}

	public int getAlarmHour() {
		return alarmHour;
	}

	public void setAlarmHour(int alarmHour) {
		this.alarmHour = alarmHour;
	}

	public int getAlarmMinute() {
		return alarmMinute;
	}

	public void setAlarmMinute(int alarmMinute) {
		this.alarmMinute = alarmMinute;
	}

	public int getAlarmDays() {
		return alarmDays;
	}

	public void setAlarmDays(int alarmDays) {
		this.alarmDays = alarmDays;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	@Override
	public String toString() {
		return "AlarmClockInfo [mondayChecked=" + mondayChecked
				+ ", tuesdayChecked=" + tuesdayChecked + ", wednesdayChecked="
				+ wednesdayChecked + ", thursdayChecked=" + thursdayChecked
				+ ", fridayChecked=" + fridayChecked + ", saturdayChecked="
				+ saturdayChecked + ", sundayChecked=" + sundayChecked
				+ ", workDayChecked=" + workDayChecked + ", allWeeksChecked="
				+ allWeeksChecked + ", alarmHour=" + alarmHour
				+ ", alarmMinute=" + alarmMinute + ", alarmDays=" + alarmDays
				+ ", enable=" + enable + "]";
	}
	
	
	
	
	
	
}
