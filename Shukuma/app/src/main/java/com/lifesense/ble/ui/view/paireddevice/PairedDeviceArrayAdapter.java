/**
 * 
 */
package com.lifesense.ble.ui.view.paireddevice;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lifesense.ble.DeviceConnectState;
import com.lifesense.ble.bean.BloodPressureData;
import com.lifesense.ble.bean.DeviceTypeConstants;
import com.lifesense.ble.bean.HeightData;
import com.lifesense.ble.bean.LsDeviceInfo;
import com.lifesense.ble.bean.PedometerData;
import com.lifesense.ble.bean.WeightData_A2;
import com.lifesense.ble.bean.WeightData_A3;
import com.lifesense.ble.ui.MainActivity;
import com.lifesense.ble.ui.R;
import com.lifesense.ble.ui.menu.PairedDeviceListFragment;



/**
 * @author CaiChiXiang
 *
 */
public class PairedDeviceArrayAdapter extends ArrayAdapter<PairedDeviceListItem>{

	 private List<PairedDeviceListItem> mData;
	 private int mLayoutViewResourceId;
	 private TextView recordNumberView ;
	 private PairedDeviceListItem currentSelectItem;
	 
	    public PairedDeviceArrayAdapter(Context context, int layoutViewResourceId,
	                              List<PairedDeviceListItem> data) {
	        super(context, layoutViewResourceId, data);
	        mData = data;
	        mLayoutViewResourceId = layoutViewResourceId;
	        
	    }

	    /**
	     * Populates the item in the listview cell with the appropriate data. This method
	     * sets the thumbnail image, the title and the extra text. This method also updates
	     * the layout parameters of the item's view so that the image and title are centered
	     * in the bounds of the collapsed view, and such that the extra text is not displayed
	     * in the collapsed state of the cell.
	     */
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {

	        currentSelectItem = mData.get(position);
	        final LsDeviceInfo pairedDevice=currentSelectItem.getDeviceInfo();
	        final int recordCount=currentSelectItem.getRecordCount();
	        final DeviceConnectState deviceConnectState=currentSelectItem.getConnectState();
	        
	        if(convertView == null) 
	        {
	            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
	            convertView = inflater.inflate(mLayoutViewResourceId, parent, false);
	        }

	        LinearLayout linearLayout = (LinearLayout)(convertView.findViewById(R.id.item_linear_layout));
	        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams
	                (AbsListView.LayoutParams.MATCH_PARENT,AbsListView.LayoutParams.WRAP_CONTENT);//
	        linearLayout.setLayoutParams(linearLayoutParams);
	        

	        ImageView imgView = (ImageView)convertView.findViewById(R.id.device_image_view);
	        TextView protocolTypeView = (TextView)convertView.findViewById(R.id.protocol_type_text_view);
	        TextView deviceNameView = (TextView)convertView.findViewById(R.id.device_name_text_view);
	        TextView userNumberView = (TextView)convertView.findViewById(R.id.user_number_text_view);
	        TextView connectStateView = (TextView)convertView.findViewById(R.id.connect_status_text_view);
	        recordNumberView = (TextView)convertView.findViewById(R.id.record_number_text_view);
	        
	        protocolTypeView.setText(pairedDevice.getProtocolType());
	        if("GENERIC_FAT".equals(pairedDevice.getProtocolType())
	        		||"A4".equals(pairedDevice.getProtocolType())
	        		||"KITCHEN_PROTOCOL".equals(pairedDevice.getProtocolType()))
	        {
	            deviceNameView.setText(pairedDevice.getDeviceName());
	        }
	        else
	        {
	            deviceNameView.setText(pairedDevice.getDeviceName()+":"+pairedDevice.getBroadcastID());
	        }
	
	        userNumberView.setText("UserNumber:"+pairedDevice.getDeviceUserNumber());
	       
//	        showTextView = (TextView)convertView.findViewById(R.id.show_record_text_view);
	        
	        
	       if(deviceConnectState!=null)
	       {
	    	   connectStateView.setText(deviceConnectState.toString().toLowerCase().replace("_", " "));
	       }
	       else
	       {
	    	   connectStateView.setText("Not in range");
	       }
			
	        if(recordCount==0)
	        {
	        	recordNumberView.setText("No Record");
	        }
	        else
	        {
	        	recordNumberView.setText(recordCount+" New Record");
	        }

	        String deviceType=pairedDevice.getDeviceType();
	        int imageResource=R.drawable.weight_scale;//default device image
	        
	        if(DeviceTypeConstants.FAT_SCALE.equals(deviceType))
	        {
	        	imageResource=R.drawable.fat_scale;
	        }
	        if(DeviceTypeConstants.PEDOMETER.equals(deviceType))
	        {
	        	imageResource=R.drawable.pedometer;
	        }
	        if(DeviceTypeConstants.HEIGHT_RULER.equals(deviceType))
	        {
	        	imageResource=R.drawable.height;
	        }
	        if(DeviceTypeConstants.SPHYGMOMAN_METER.equals(deviceType))
	        {
	        	imageResource=R.drawable.blood_pressure;
	        }
	        if(DeviceTypeConstants.KITCHEN_SCALE.equals(deviceType))
	        {
	        	imageResource=R.drawable.kitchen_scale;
	        }
	        if(DeviceTypeConstants.WEIGHT_SCALE.equals(deviceType))
	        {
	        	imageResource=R.drawable.weight_scale;
	        }
	        
	        imgView.setImageBitmap(getCroppedBitmap(BitmapFactory.decodeResource(getContext()
	                .getResources(),imageResource, null)));
	       
	        convertView.setLayoutParams(new ListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
	                AbsListView.LayoutParams.WRAP_CONTENT));

	        
	        ShowMeasuredRecordLayout expandingLayout = (ShowMeasuredRecordLayout)convertView.findViewById(R.id
	                .expanding_layout);
	        expandingLayout.setExpandedHeight(currentSelectItem.getExpandedHeight());
	        expandingLayout.setSizeChangedListener(currentSelectItem);

	        
	        if (!currentSelectItem.isExpanded()) 
	        {
	            expandingLayout.setVisibility(View.GONE);
	        } 
	        else 
	        {
	            expandingLayout.setVisibility(View.VISIBLE);
	        }

	        return convertView;
	    }

		/**
	     * Crops a circle out of the thumbnail photo.
	     */
	    public Bitmap getCroppedBitmap(Bitmap bitmap) {
	        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
	                Config.ARGB_8888);

	        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

	        Canvas canvas = new Canvas(output);

	        final Paint paint = new Paint();
	        paint.setAntiAlias(true);

	        int halfWidth = bitmap.getWidth()/2;
	        int halfHeight = bitmap.getHeight()/2;

	        canvas.drawCircle(halfWidth, halfHeight, Math.max(halfWidth, halfHeight), paint);

	        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

	        canvas.drawBitmap(bitmap, rect, rect, paint);

	        return output;
	    }
	    
	    

}
