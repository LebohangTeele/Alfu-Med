/**
 * 
 */
package com.lifesense.ble.ui.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.lifesense.ble.bean.LsDeviceInfo;
import com.lifesense.ui.bean.AlarmClockInfo;
import com.lifesense.ui.bean.BleDeviceUserInfo;
import com.lifesense.ui.bean.PairedDeviceInfo;
import com.lifesense.ui.bean.SettingInfo;

/**
 * @author CaiChiXiang
 *
 */
public class SettingInfoManager {

	public static final String KEY_SETTING_INFO=SettingInfo.class.getName();
	public static final String KEY_DEVICE_USER_INFO=BleDeviceUserInfo.class.getName();
	public static final String KEY_PEDOMETER_ALARM_CLOCK_INFO=AlarmClockInfo.class.getName();
	public static final String KEY_PAIRED_DEVICE_LIST=PairedDeviceInfo.class.getName();
	
	private static final String TAG=SettingInfoManager.class.getSimpleName();
	

	
	/**
	 * 保存设备的用户信息
	 * @param context
	 * @param deviceUserInfo
	 */
	public static void saveBleDeviceUserInfo(Context context,BleDeviceUserInfo deviceUserInfo)
	{
		if(context!=null && deviceUserInfo!=null)
		{
			//保存产品条形码
			SettingInfo settingInfo=getSettingInfo(context, KEY_SETTING_INFO);
			if(settingInfo!=null)
			{
				//直接覆盖之前已经存在的设备用户信息
				settingInfo.setDeviceUserInfo(deviceUserInfo);
			}
			else
			{
				settingInfo=new SettingInfo();
				settingInfo.setDeviceUserInfo(deviceUserInfo);
			}
			
			saveSettingInfo(context, KEY_SETTING_INFO, settingInfo);
		}
		
	}
	
	
	/**
	 * 删除指定的条形码
	 * @param barcode
	 * @param settingInfo
	 * @return
	 */
	public static boolean deleteProductBarcode(String barcode,SettingInfo settingInfo)
	{
		
		if(settingInfo!=null)
		{
			List<String> barcodes=settingInfo.getProductBarcodes();
			if(barcodes!=null && barcodes.size()>0)
			{
				return barcodes.remove(barcode);
			}
			else return false;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * 保存条形码至SharePreferences
	 * @param appContext
	 * @param key
	 * @param barcode
	 */
	public static void saveProductBarcode(Context appContext,String key,String barcode)
	{
		//保存产品条形码
		SettingInfo settingInfo=getSettingInfo(appContext, key);
		if(settingInfo!=null)
		{
			List<String> productBarcodes=new ArrayList<String>();
			if(settingInfo.getProductBarcodes()!=null)
			{
				productBarcodes=settingInfo.getProductBarcodes();
			}
			else productBarcodes.add(barcode);
			
			settingInfo.setProductBarcodes(productBarcodes);
		}
		else
		{
			settingInfo=new SettingInfo();
			List<String> productBarcodes=new ArrayList<String>();
			productBarcodes.add(barcode);
			settingInfo.setProductBarcodes(productBarcodes);
		}
		saveSettingInfo(appContext, key, settingInfo);
	}
	
	/**
	 * 从SharedPreferences中获取settingInfo对象信息
	 * @param appContext
	 * @param key
	 * @return
	 */
	public static SettingInfo getSettingInfo(Context appContext,String key)
	{
		String jsonValue=readFromSharedPreferences(appContext, key);
		if(jsonValue!=null)
		{
			Gson gson = new Gson(); 
			return gson.fromJson(jsonValue, SettingInfo.class);
		}
		else return null;
	}
	
	
	/**
	 * 保存SettingInfo对象至SharedPreferences
	 * @param appContext
	 * @param key
	 * @param settingInfo
	 */
	public static void saveSettingInfo(Context appContext,String key,SettingInfo settingInfo)
	{
		Gson gson = new Gson(); 
		String setInfo=gson.toJson(settingInfo);
		saveToSharedPreferences(appContext, key, setInfo);
	}
	
	/**
	 * 判断该设备是否已配对
	 * @param appContext
	 * @param broadcastID
	 * @return
	 */
	public static boolean isDevicePaired(Context appContext,String broadcastID)
	{
		if(appContext==null || broadcastID==null || broadcastID.length()==0)
		{
			return false;
		}
		String key=PairedDeviceInfo.class.getName();
		PairedDeviceInfo pairedDeviceInfo=readPairedDeviceInfoFromFile(appContext, key);
		if(pairedDeviceInfo!=null)
		{
			if(pairedDeviceInfo.getPairedDeviceMap()!=null 
					&& !pairedDeviceInfo.getPairedDeviceMap().isEmpty())
			{
				return pairedDeviceInfo.getPairedDeviceMap().containsKey(broadcastID);
			}
			else return false;
		}
		else return false;
	}
	
	/**
	 *  根据广播ID获取已配对的设备信息
	 * @param broadcastID
	 * @return
	 */
	public static LsDeviceInfo getPairedDeviceInfoByBroadcastID(Context appContext,String broadcastID)
	{
		if(appContext==null || broadcastID==null || broadcastID.length()==0)
		{
			return null;
		}
		String key=PairedDeviceInfo.class.getName();
		PairedDeviceInfo pairedDeviceInfo=readPairedDeviceInfoFromFile(appContext, key);
		if(pairedDeviceInfo!=null)
		{
			if(pairedDeviceInfo.getPairedDeviceMap()!=null 
					&& !pairedDeviceInfo.getPairedDeviceMap().isEmpty())
			{
				return pairedDeviceInfo.getPairedDeviceMap().get(broadcastID);
			}
			else return null;
		}
		else return null;
		
	}

	/**
	 * 从本地SharedPreferences文件中读取已配对的设备对象信息
	 * @return
	 */
	public static PairedDeviceInfo readPairedDeviceInfoFromFile(Context appContext,String key)
	{
		
		PairedDeviceInfo pairedDeice=null;
		//从文件中读取已保存的设备对象信息
		SharedPreferences readPrefs=appContext.getSharedPreferences(
				appContext.getApplicationInfo().name, Context.MODE_PRIVATE);
		if(readPrefs!=null)
		{
			//key=PairedDeviceInfo.class.getName()
			String jsonString=readPrefs.getString(key, null);
			Gson gson = new Gson(); 
			Log.e("read info=", jsonString+"");
			pairedDeice=gson.fromJson(jsonString, PairedDeviceInfo.class);
			return pairedDeice;
		}
		else return null;
		
	}
	
	public static boolean deletePairedDeviceInfo(Context appContext,String broadcastId)
	{
		boolean delete=false;
		if(broadcastId!=null && broadcastId.length()>0)
		{
			PairedDeviceInfo savePairedDeviceInfo=PairedDeviceInfo.readPairedDeviceInfoFromFile(appContext);
			if(savePairedDeviceInfo!=null)
			{
				HashMap<String,LsDeviceInfo> pairedDeviceHashMap=savePairedDeviceInfo.getPairedDeviceMap();
				
				if(pairedDeviceHashMap!=null && pairedDeviceHashMap.size()>0)
				{
					//本地存在已配对过的设备对象信息
					if(pairedDeviceHashMap.containsKey(broadcastId))
					{
						delete=true;
						//该设备已保存在本地文件，则先删除旧对象信息，后添加新的对象信息 
						pairedDeviceHashMap.remove(broadcastId);
						//放入已配对的设备信息对象属性
						savePairedDeviceInfo.setPairedDeviceMap(pairedDeviceHashMap);
						
						Gson gson = new Gson(); 
						String deviceInfo=gson.toJson(savePairedDeviceInfo);
						Log.e("更新设备信息",deviceInfo);
						String key=PairedDeviceInfo.class.getName();
						saveToSharedPreferences(appContext, key, deviceInfo);
					}
				}
			}
			
		}
		return delete;
	}
	
	/**
	 * 保存已成功配对的设备对象信息
	 * @param pairedDevice
	 */
	@SuppressLint("CommitPrefEdits")
	public static void savePairedDeviceInfoToFile(Context appContext,String key,LsDeviceInfo pairedDevice)
	{
		HashMap<String,LsDeviceInfo> pairedDeviceHashMap=null;
		if(pairedDevice!=null && pairedDevice.getDeviceId()!=null)
		{
			PairedDeviceInfo savePairedDeviceInfo=PairedDeviceInfo.readPairedDeviceInfoFromFile(appContext);
			
			if(savePairedDeviceInfo!=null)
			{
				pairedDeviceHashMap=savePairedDeviceInfo.getPairedDeviceMap();
				if(pairedDeviceHashMap!=null && pairedDeviceHashMap.size()>0)
				{
					String tempBroadcastID=null;
					tempBroadcastID=isPairedDeviceExist(pairedDeviceHashMap, pairedDevice.getDeviceId());
			
					//本地存在已配对过的设备对象信息
					if(tempBroadcastID!=null)
					{
						//该设备已保存在本地文件，则先删除旧对象信息，后添加新的对象信息 
						pairedDeviceHashMap.remove(tempBroadcastID);
						pairedDeviceHashMap.put(pairedDevice.getBroadcastID(), pairedDevice);
					}
					else
					{
						//该设备未保存在本地文件
						if(pairedDeviceHashMap!=null)
						{
							pairedDeviceHashMap.put(pairedDevice.getBroadcastID(), pairedDevice);
						}
					}
					//放入已配对的设备信息对象属性
					savePairedDeviceInfo.setPairedDeviceMap(pairedDeviceHashMap);
				
				}
				else
				{
					//初次保存已配对的设备对象信息
					//生成Broadcast ID与设备信息的Map对照表
					HashMap<String,com.lifesense.ble.bean.LsDeviceInfo> deviceInfoMap=new HashMap<String, com.lifesense.ble.bean.LsDeviceInfo>();
					
					//以Broadcast ID作为Key,已配对的设备信息对象作为Value存入Map对照表
					deviceInfoMap.put(pairedDevice.getBroadcastID(), pairedDevice);

					//放入已配对的设备信息对象属性
					savePairedDeviceInfo.setPairedDeviceMap(deviceInfoMap);
				}
			}
			else
			{
				savePairedDeviceInfo=new PairedDeviceInfo();
				
				//初次保存已配对的设备对象信息
				//生成Broadcast ID与设备信息的Map对照表
				HashMap<String,com.lifesense.ble.bean.LsDeviceInfo> deviceInfoMap=new HashMap<String, com.lifesense.ble.bean.LsDeviceInfo>();
				
				//以Broadcast ID作为Key,已配对的设备信息对象作为Value存入Map对照表
				deviceInfoMap.put(pairedDevice.getBroadcastID(), pairedDevice);

				//放入已配对的设备信息对象属性
				savePairedDeviceInfo.setPairedDeviceMap(deviceInfoMap);
			}
		
			Gson gson = new Gson(); 
			String deviceInfo=gson.toJson(savePairedDeviceInfo);
			Log.e("将内容写入文件",deviceInfo);
			//key=PairedDeviceInfo.class.getName();
			saveToSharedPreferences(appContext, key, deviceInfo);

		}
	}
	
	
	/**
	 * 根据key从SharedPreferences中读取相应的信息
	 * @param appContext
	 * @param key
	 * @return
	 */
	private static String readFromSharedPreferences(Context appContext,String key)
	{
		if(appContext!=null && key!=null && key.length()>0)
		{
			SharedPreferences readPrefs=appContext.getSharedPreferences(
					appContext.getApplicationInfo().name, Context.MODE_PRIVATE);
			if(readPrefs!=null)
			{
				//key=PairedDeviceInfo.class.getName()
				String jsonString=readPrefs.getString(key, null);
				Log.d(TAG, "read value from share preferences,key ="+key+";value="+jsonString);
				return jsonString;
			}
			else return null;
		}
		else
		{
			Log.e(TAG, "Failed to read value from share preferences,is null....");
			return null;
		}
		
	}
	
	
	/**
	 * 将内容保存至SharedPreferences
	 * @param appContext
	 * @param key
	 * @param value
	 */
	private static void saveToSharedPreferences(Context appContext,String key,String value)
	{
		if(appContext!=null && key!=null && key.length()>0 && value!=null)
		{
			SharedPreferences savePrefs=appContext.getSharedPreferences(
					appContext.getApplicationInfo().name, Context.MODE_PRIVATE);
			SharedPreferences.Editor ed=savePrefs.edit();
			ed.putString(key, value);
			ed.commit();
		}
		else
		{
			Log.e(TAG, "Failed to save content to share preferences,is null....");
		}
	}
	
	/**
	 * 判断该设备是否重复配对过
	 * @param hashMap
	 * @return
	 */
	public static String isPairedDeviceExist(Map<String,LsDeviceInfo> hashMap,String deviceId)
	{
		if(deviceId!=null && hashMap!=null && !hashMap.isEmpty())
		{
			Iterator<Entry<String, LsDeviceInfo>> it = hashMap.entrySet().iterator();
			while (it.hasNext()) 
			{
				Entry<String, LsDeviceInfo> entry = it.next();
				LsDeviceInfo lsDevice=entry.getValue();
				if(lsDevice!=null && deviceId.equals(lsDevice.getDeviceId()))
				{
					return lsDevice.getBroadcastID();
				}
			}
			return null;
		}
		else return null;

	}
	
}
