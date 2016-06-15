/**
 * 
 */
package com.lifesense.ble.ui.menu;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lifesense.ble.ui.R;
import com.lifesense.ble.ui.tools.SettingInfoManager;
import com.lifesense.ui.bean.BleDeviceUserInfo;
import com.lifesense.ui.bean.DistanceUnitType;
import com.lifesense.ui.bean.GenderType;
import com.lifesense.ui.bean.HourFormatType;
import com.lifesense.ui.bean.SettingInfo;
import com.lifesense.ui.bean.WeekStartType;
import com.lifesense.ui.bean.WeightUnitType;

/**
 * @author CaiChiXiang
 *
 */
public class DeviceUserFragment extends Fragment implements OnClickListener{

	private enum EditType
	{
		WEIGHT_TARGET,
		WEEK_TARGET_STEPS,
		USER_NAME,
		USER_HEIGHT,
		USER_WEIGHT,
	
	};
	
	private enum SelectorType
	{
		WEIGHT_UNIT,
		DISTANCE_UNIT,
		HOUR_FORMAT,
		WEEK_START,
		USER_GENDER,
		ATHLETE_LEVEL,
		
	};
	
	private String[] weightUnitArray=new String[]
			{
				"kg",
				"lb"
			};
	private String[] distanceUnitArray=new String[]
			{
				"Mile",
				"Kilometer"
			};
	private String[] hourFormatArray=new String[]
			{
				"12 Hour",
				"24 Hour"
			};
	private String[] weekStartArray=new String[]
			{
				"Monday",
				"Sunday"
			};
	
	private String[] genderArray=new String[]
			{
				"Female",
				"Male",
			};
	private String[] athleteLecvelArray=new String[]
			{
				"0","1","2","3","4","5"
			};
	
	private TextView weightUnitTextView;
	private TextView weightTargetTextView;
	private TextView distanceUnitTextView;
	private TextView hourFormatTextView;
	private TextView weekStartTextView;
	private TextView weekTargetStepsTextView;


	private View editUserInfoView;
	private  AlertDialog selectDialog;
	private MenuItem saveUserInfoMenuItem;
	
	
	private View userInfoSettingView;
	private View fatSettingView;
	private View pedometerSettingView;
	private View developerModeSettingView;
	
	private TextView userNameTextView;
	private TextView genderTextView;
	private TextView birthdayTextView;
	private TextView heightTextView;
	private TextView weightTextView;
	private TextView athletelevelTextView;
	private TextView developerModeTextView;
	
	private View rootView;
	private Context context;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		rootView = inflater.inflate(R.layout.fragment_user_details, container, false);
		weightUnitTextView=(TextView) rootView.findViewById(R.id.weight_unit_text_view);
		weightTargetTextView=(TextView) rootView.findViewById(R.id.weight_target_text_view);

		distanceUnitTextView=(TextView) rootView.findViewById(R.id.distance_unit_text_view);
		hourFormatTextView=(TextView) rootView.findViewById(R.id.hour_format_text_view);
		weekStartTextView=(TextView) rootView.findViewById(R.id.week_start_text_view);
		weekTargetStepsTextView=(TextView) rootView.findViewById(R.id.week_target_steps_text_view);

		editUserInfoView=(View) rootView.findViewById(R.id.edit_user_info_layout);
		userInfoSettingView=(View)rootView.findViewById(R.id.user_info_setting_layout);
		fatSettingView=(View)rootView.findViewById(R.id.fat_scale_setting_layout);
		pedometerSettingView=(View)rootView.findViewById(R.id.pedometer_setting_layout);
		developerModeSettingView=(View)rootView.findViewById(R.id.developer_mode_setting_layout);
		
		userNameTextView=(TextView) rootView.findViewById(R.id.user_name_text_view);
		genderTextView=(TextView) rootView.findViewById(R.id.user_gender_text_view);
		birthdayTextView=(TextView) rootView.findViewById(R.id.user_birthday_text_view);
		heightTextView=(TextView) rootView.findViewById(R.id.user_height_text_view);
		weightTextView=(TextView) rootView.findViewById(R.id.user_weight_text_view);
		athletelevelTextView=(TextView) rootView.findViewById(R.id.athlete_level_text_view);
		developerModeTextView=(TextView)rootView.findViewById(R.id.developer_mode_text_view);
		
		setHasOptionsMenu(true);
		return rootView;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}


	@Override
	public void onStart() 
	{
		
		rootView.findViewById(R.id.weight_unit_layout).setOnClickListener(this);
		rootView.findViewById(R.id.weight_target_layout).setOnClickListener(this);
		rootView.findViewById(R.id.distance_unit_layout).setOnClickListener(this);
		rootView.findViewById(R.id.hour_format_layout).setOnClickListener(this);
		rootView.findViewById(R.id.week_start_layout).setOnClickListener(this);
		rootView.findViewById(R.id.week_target_steps_layout).setOnClickListener(this);
	
		rootView.findViewById(R.id.user_name_layout).setOnClickListener(this);
		rootView.findViewById(R.id.user_gender_layout).setOnClickListener(this);
		rootView.findViewById(R.id.user_birthday_layout).setOnClickListener(this);
		rootView.findViewById(R.id.user_height_layout).setOnClickListener(this);
		rootView.findViewById(R.id.user_weight_layout).setOnClickListener(this);
		rootView.findViewById(R.id.athlete_level_layout).setOnClickListener(this);
		rootView.findViewById(R.id.edit_user_info_layout).setOnClickListener(this);
		rootView.findViewById(R.id.developer_mode_layout).setOnClickListener(this);
		
		context=getActivity().getApplicationContext();
		SettingInfo settingInfo=SettingInfoManager.getSettingInfo(context, SettingInfoManager.KEY_SETTING_INFO);
		if(settingInfo!=null && settingInfo.getDeviceUserInfo()!=null)
		{
			BleDeviceUserInfo deviceUserInfo=settingInfo.getDeviceUserInfo();
			restoreDeviceUserInfoSetting(deviceUserInfo);
		}
		super.onStart();
	}

	@Override
	public void onStop() 
	{
		saveDeviceUserInfoSetting();
		
		super.onStop();
	}





	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) 
	{
		inflater.inflate(R.menu.menu_fragment_user_info, menu);
		saveUserInfoMenuItem=menu.findItem(R.id.action_save_user_info_item);
		saveUserInfoMenuItem.setVisible(false);
		super.onCreateOptionsMenu(menu, inflater);
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
			case R.id.action_save_user_info_item:
			{
				saveUserInfoMenuItem.setVisible(false);
				userInfoSettingView.setVisibility(View.GONE);
				editUserInfoView.setVisibility(View.VISIBLE);
				fatSettingView.setVisibility(View.VISIBLE);
				pedometerSettingView.setVisibility(View.VISIBLE);
				developerModeSettingView.setVisibility(View.VISIBLE);
				
				String msg="Done! Successfully to  save user info";
				BleDeviceUserInfo userInfo=saveDeviceUserInfoSetting();
				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
				restoreDeviceUserInfoSetting(userInfo);
			}
			
		}
			
		return super.onOptionsItemSelected(item);
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) 
	{
		int viewId=v.getId();
		String title="";
		switch (viewId) 
		{
			case R.id.edit_user_info_layout:
			{
				saveUserInfoMenuItem.setVisible(true);
				userInfoSettingView.setVisibility(View.VISIBLE);
				editUserInfoView.setVisibility(View.GONE);
				fatSettingView.setVisibility(View.GONE);
				pedometerSettingView.setVisibility(View.GONE);
				developerModeSettingView.setVisibility(View.GONE);
			}break;
	
			case R.id.weight_unit_layout:
			{
				title="Select Weight Unit";
				showSelectorValue(title, weightUnitArray, SelectorType.WEIGHT_UNIT);
			}break;
			case R.id.weight_target_layout:
			{
				title="Set Weight Goals";
				showInputDialog(title,EditType.WEIGHT_TARGET,InputType.TYPE_CLASS_NUMBER);
			}break;
			case R.id.distance_unit_layout:
			{
				title="Select Distance Unit";
				showSelectorValue(title, distanceUnitArray,SelectorType.DISTANCE_UNIT);
			}break;
			case R.id.hour_format_layout:
			{
				title="Select Hour Format";
				showSelectorValue(title, hourFormatArray,SelectorType.HOUR_FORMAT);
			}break;
			case R.id.week_start_layout:
			{
				title="Select Week Start";
				showSelectorValue(title, weekStartArray,SelectorType.WEEK_START);
			}break;
			case R.id.week_target_steps_layout:
			{
				title="Set week target steps";
				showInputDialog(title,EditType.WEEK_TARGET_STEPS,InputType.TYPE_CLASS_NUMBER);
			}break;
			case R.id.user_name_layout:
			{
				title="Set your name";
				showInputDialog(title,EditType.USER_NAME,InputType.TYPE_CLASS_TEXT);
				
			}break;
			case R.id.user_gender_layout:
			{
				title="Select Gender";
				showSelectorValue(title, genderArray,SelectorType.USER_GENDER);
			}break;
			case R.id.user_birthday_layout:
			{
				showDatePickerDialog();
			}break;
			case R.id.user_height_layout:
			{
				title="Set height";
				showInputDialog(title,EditType.USER_HEIGHT,InputType.TYPE_NUMBER_FLAG_DECIMAL);
			}break;
			case R.id.user_weight_layout:
			{
				title="Set Weight";
				showInputDialog(title,EditType.USER_WEIGHT,InputType.TYPE_NUMBER_FLAG_DECIMAL);
			}break;
			case R.id.athlete_level_layout:
			{
				title="Select Athlete level";
				showSelectorValue(title, athleteLecvelArray,SelectorType.ATHLETE_LEVEL);
			}break;
			case R.id.developer_mode_layout:
			{
				String developerKey=developerModeTextView.getText().toString().trim();
				if(developerKey.equals("true"))
				{
					developerKey="false";
				}
				else
				{
					developerKey="true";
				}
				developerModeTextView.setText(developerKey);
			}break;
			default:
				break;
		}

	}

	private void restoreDeviceUserInfoSetting(BleDeviceUserInfo deviceUserInfo)
	{
		//set the title
		TextView nameTitle=(TextView)editUserInfoView.findViewById(R.id.user_name_title_text_view);
		TextView genderTitle=(TextView)editUserInfoView.findViewById(R.id.user_gender_title_text_view);
		
		nameTitle.setText(deviceUserInfo.getUserName().toString().trim());
		userNameTextView.setText(deviceUserInfo.getUserName());
		
		if(deviceUserInfo.getUserGender()==GenderType.FEMALE)
		{
			System.out.println("failed to set user gender....,is female"+deviceUserInfo.getUserGender());
			genderTextView.setText(genderArray[0]);
			genderTitle.setText(genderArray[0]);
		}
		else 
		{
			System.out.println("failed to set user gender....,is male"+deviceUserInfo.getUserGender());
			genderTextView.setText(genderArray[1]);
			genderTitle.setText(genderArray[1]);
		}
	
		
		
		birthdayTextView.setText(deviceUserInfo.getBirthday());
		heightTextView.setText(deviceUserInfo.getUserHeight()+"");
		weightTextView.setText(deviceUserInfo.getUserWeight()+"");
		athletelevelTextView.setText(deviceUserInfo.getAthleteLevel()+"");
		if(deviceUserInfo.getWeightUnit()==WeightUnitType.KG)
		{
			weightUnitTextView.setText(weightUnitArray[0]);
		}
		else weightUnitTextView.setText(weightUnitArray[1]);
		
		weightTargetTextView.setText(deviceUserInfo.getWeightTarget()+"");
		
		if(deviceUserInfo.getDistanceUnit()==DistanceUnitType.KILOMETER)
		{
			distanceUnitTextView.setText(distanceUnitArray[1]);
		}
		else distanceUnitTextView.setText(distanceUnitArray[0]);
		
		
		if(deviceUserInfo.getHourFormat()==HourFormatType.HOUR_12)
		{
			hourFormatTextView.setText(hourFormatArray[0]);
		}
		else hourFormatTextView.setText(hourFormatArray[1]);
		
		if(deviceUserInfo.getWeekStart()==WeekStartType.MONDAY)
		{
			weekStartTextView.setText(weekStartArray[0]);
		}
		else weekStartTextView.setText(weekStartArray[1]);
		
		weekTargetStepsTextView.setText(deviceUserInfo.getMovingTarget()+"");
	
		developerModeTextView.setText(deviceUserInfo.getDeveloperKey());
	}
	
	
	/**
	 * 
	 */
	private BleDeviceUserInfo saveDeviceUserInfoSetting() 
	{
		BleDeviceUserInfo deviceUserInfo=new BleDeviceUserInfo();
		deviceUserInfo.setUserName(userNameTextView.getText().toString().trim());
		if(genderTextView.getText().equals(genderArray[0]))
		{
			deviceUserInfo.setUserGender(GenderType.FEMALE);
		}
		else
		{
			deviceUserInfo.setUserGender(GenderType.MALE);
		}
		
		
		String birthday=birthdayTextView.getText().toString().trim();
		deviceUserInfo.setBirthday(birthday);
		//set user age
		int yearIndex=birthday.indexOf("-");
		String yearStr=birthday.substring(0, yearIndex);
		int yearIntValue=Integer.parseInt(yearStr);
		Calendar calendar = Calendar.getInstance();
		int currentYear=calendar.get(Calendar.YEAR);
		deviceUserInfo.setUserAge(currentYear-yearIntValue);
		
		deviceUserInfo.setUserHeight(Float.parseFloat(heightTextView.getText().toString().trim()));
		
		deviceUserInfo.setUserWeight(Float.parseFloat(weightTextView.getText().toString().trim()));
		
		deviceUserInfo.setAthleteLevel(Integer.parseInt(athletelevelTextView.getText().toString().trim()));
	
		WeightUnitType weightUnit=WeightUnitType.KG;
		if(weightUnitTextView.getText().toString().equals(weightUnitArray[1]))
		{
			weightUnit=WeightUnitType.LB;
		}
		deviceUserInfo.setWeightUnit(weightUnit);
		
		deviceUserInfo.setWeightTarget(Integer.parseInt(weightTargetTextView.getText().toString().trim()));
		
		DistanceUnitType distanceUnit=DistanceUnitType.KILOMETER;
		if(distanceUnitTextView.getText().toString().trim().equals(distanceUnitArray[0]))
		{
			distanceUnit=DistanceUnitType.MILE;
		}
		deviceUserInfo.setDistanceUnit(distanceUnit);
		
		HourFormatType hourFormat=HourFormatType.HOUR_24;
		if(hourFormatTextView.getText().toString().trim().equals(hourFormatArray[0]))
		{
			hourFormat=HourFormatType.HOUR_12;
		}
		deviceUserInfo.setHourFormat(hourFormat);
		
		
		WeekStartType weekStart=WeekStartType.MONDAY;
		if(weekStartTextView.getText().toString().equals(weekStartArray[1]))
		{
			weekStart=WeekStartType.SUNDAY;
		}
		deviceUserInfo.setWeekStart(weekStart);
		
		deviceUserInfo.setMovingTarget(Integer.parseInt(weekTargetStepsTextView.getText().toString().trim()));
	
		deviceUserInfo.setDeveloperKey(developerModeTextView.getText().toString().trim());
		
		//save setting info to file
		SettingInfoManager.saveBleDeviceUserInfo(context, deviceUserInfo);
		
		return deviceUserInfo;
	}
	
	private void showSelectorValue(String title,CharSequence[] items,final SelectorType selectorType) 
	{
		ContextThemeWrapper ctw = new ContextThemeWrapper(this.getActivity(), android.R.style.Theme_Holo_Light);
		// Creating and Building the Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		builder.setTitle(title);
		builder.setSingleChoiceItems(items, -1,new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				//不通过解除绑定，直接更换设备的用户
				selectDialog.dismiss();
//				int index=which+1;
				setSelectorValue(selectorType,which);
			}
		});
		selectDialog = builder.create();
		selectDialog.show();
	}
	

	/**
	 * @param selectorType
	 * @param index
	 */
	protected void setSelectorValue(SelectorType selectorType, int index) 
	{
		if(selectorType==SelectorType.WEIGHT_UNIT)
		{
			weightUnitTextView.setText(weightUnitArray[index]);
		}
		if(selectorType==SelectorType.WEEK_START)
		{
			weekStartTextView.setText(weekStartArray[index]);
		}
		if(selectorType==SelectorType.DISTANCE_UNIT)
		{
			distanceUnitTextView.setText(distanceUnitArray[index]);
		}
		if(selectorType==SelectorType.HOUR_FORMAT)
		{
			hourFormatTextView.setText(hourFormatArray[index]);
		}
		if(selectorType==SelectorType.USER_GENDER)
		{
			genderTextView.setText(genderArray[index]);
		}
		if(selectorType==SelectorType.ATHLETE_LEVEL)
		{
			athletelevelTextView.setText(athleteLecvelArray[index]);
		}
		
	}


	/**
	 * 显示日期选择器
	 */
	private void showDatePickerDialog()
	{
		ContextThemeWrapper ctw = new ContextThemeWrapper(this.getActivity(), android.R.style.Theme_Holo_Light);
		Calendar calendar = Calendar.getInstance();
	 	DatePickerDialog mDialog = new DatePickerDialog(ctw, new OnDateSetListener() 
	 	{

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) 
			{	  
				monthOfYear=monthOfYear+1;
				// Month is 0 based so ,add 1
				
				StringBuffer birthday=new StringBuffer();
				birthday.append(year);
				birthday.append("-");
				birthday.append(monthOfYear);
				birthday.append("-");
				birthday.append(dayOfMonth);

				Calendar calendar = Calendar.getInstance();
				int currentYear=calendar.get(Calendar.YEAR);
				
//				newUserInfo.setUserAge(currentYear-year);
				
				birthdayTextView.setText(birthday);
			}
		}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
	 	
	 	//设置日期的选择范围为当前时间
	 	mDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
	 	
	 	mDialog.show();
	}

	
	private void showInputDialog(String title,final EditType editType,int inputType)
	{
		ContextThemeWrapper ctw = new ContextThemeWrapper(this.getActivity(), android.R.style.Theme_Holo_Light);
		AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		builder.setTitle(title);
		// Set up the input
		final EditText input = new EditText(getActivity());
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType(inputType);
		builder.setView(input);
		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				String inputValue=input.getText().toString();
				if(editType==EditType.WEIGHT_TARGET)
				{
					weightTargetTextView.setText(inputValue+"");
				}
				if(editType==EditType.WEEK_TARGET_STEPS)
				{
					weekTargetStepsTextView.setText(inputValue);
				}
				if(editType==EditType.USER_NAME)
				{
					userNameTextView.setText(inputValue);
				}
				if(editType==EditType.USER_HEIGHT)
				{
					float maxHeight=(float)2.5;
					if(Float.parseFloat(inputValue)>=2.5)
					{
						heightTextView.setText(maxHeight+"");
					}
					else heightTextView.setText(inputValue+"");
					
				}
				if(editType==EditType.USER_WEIGHT)
				{
					float maxWeight=(float)300;
					if(Float.parseFloat(inputValue)>=300)
					{
						weightTextView.setText(maxWeight+"");
					}
					else weightTextView.setText(inputValue+"");
					
					
				}
				
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();

			}
		});
		builder.create();
		builder.show();
	}

}
