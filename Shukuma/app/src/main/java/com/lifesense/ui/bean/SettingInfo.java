/**
 * 
 */
package com.lifesense.ui.bean;

import java.util.List;
import java.util.Map;

/**
 * @author CaiChiXiang
 *
 */
public class SettingInfo {

		
	private List<String> productBarcodes;
	private AlarmClockInfo alarmClockInfo;
	private BleDeviceUserInfo deviceUserInfo;
	
	
	public List<String> getProductBarcodes() {
		return productBarcodes;
	}



	public void setProductBarcodes(List<String> productBarcodes) {
		this.productBarcodes = productBarcodes;
	}



	public AlarmClockInfo getAlarmClockInfo() {
		return alarmClockInfo;
	}



	public void setAlarmClockInfo(AlarmClockInfo alarmClockInfo) {
		this.alarmClockInfo = alarmClockInfo;
	}



	/**
	 * @return the deviceUserInfo
	 */
	public BleDeviceUserInfo getDeviceUserInfo() {
		return deviceUserInfo;
	}



	/**
	 * @param deviceUserInfo the deviceUserInfo to set
	 */
	public void setDeviceUserInfo(BleDeviceUserInfo deviceUserInfo) {
		this.deviceUserInfo = deviceUserInfo;
	}




	
	
}
