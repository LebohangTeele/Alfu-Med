/**
 * 
 */
package com.lifesense.ui.bean;

/**
 * @author CaiChiXiang
 *
 */
public class BleDeviceUserInfo {

	private String userName;
	private GenderType userGender;
	private String birthday;
	private int userAge;
	private float userHeight;
	private float userWeight;
	private int athleteLevel;
	private int weightTarget;
	private int movingTarget;
	private WeekStartType weekStart;
	
	private DistanceUnitType distanceUnit;
	private WeightUnitType weightUnit;
	private HourFormatType hourFormat;
	private String developerKey;
	
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public GenderType getUserGender() {
		return userGender;
	}
	public void setUserGender(GenderType userGender) {
		this.userGender = userGender;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public int getUserAge() {
		return userAge;
	}
	public void setUserAge(int userAge) {
		this.userAge = userAge;
	}
	public float getUserHeight() {
		return userHeight;
	}
	public void setUserHeight(float userHeight) {
		this.userHeight = userHeight;
	}
	public float getUserWeight() {
		return userWeight;
	}
	public void setUserWeight(float userWeight) {
		this.userWeight = userWeight;
	}
	public int getAthleteLevel() {
		return athleteLevel;
	}
	public void setAthleteLevel(int athleteLevel) {
		this.athleteLevel = athleteLevel;
	}
	public int getWeightTarget() {
		return weightTarget;
	}
	public void setWeightTarget(int weightTarget) {
		this.weightTarget = weightTarget;
	}
	public int getMovingTarget() {
		return movingTarget;
	}
	public void setMovingTarget(int movingTarget) {
		this.movingTarget = movingTarget;
	}
	public WeekStartType getWeekStart() {
		return weekStart;
	}
	public void setWeekStart(WeekStartType weekStart) {
		this.weekStart = weekStart;
	}
	public DistanceUnitType getDistanceUnit() {
		return distanceUnit;
	}
	public void setDistanceUnit(DistanceUnitType distanceUnit) {
		this.distanceUnit = distanceUnit;
	}
	public WeightUnitType getWeightUnit() {
		return weightUnit;
	}
	public void setWeightUnit(WeightUnitType weightUnit) {
		this.weightUnit = weightUnit;
	}
	public HourFormatType getHourFormat() {
		return hourFormat;
	}
	public void setHourFormat(HourFormatType hourFormat) {
		this.hourFormat = hourFormat;
	}
	public String getDeveloperKey() {
		return developerKey;
	}
	public void setDeveloperKey(String developerKey) {
		this.developerKey = developerKey;
	}
	@Override
	public String toString() {
		return "BleDeviceUserInfo [userName=" + userName + ", userGender="
				+ userGender + ", birthday=" + birthday + ", userAge="
				+ userAge + ", userHeight=" + userHeight + ", userWeight="
				+ userWeight + ", athleteLevel=" + athleteLevel
				+ ", weightTarget=" + weightTarget + ", movingTarget="
				+ movingTarget + ", weekStart=" + weekStart + ", distanceUnit="
				+ distanceUnit + ", weightUnit=" + weightUnit + ", hourFormat="
				+ hourFormat + ", developerKey=" + developerKey + "]";
	}
	
	
	
	
	
	
}
