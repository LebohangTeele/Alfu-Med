/**
 * 
 */
package com.lifesense.ble.ui.view.scanresults;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lifesense.ble.bean.DeviceTypeConstants;
import com.lifesense.ble.bean.LsDeviceInfo;
import com.lifesense.ble.ui.R;
import com.lifesense.ble.ui.tools.SettingInfoManager;
import com.lifesense.ui.bean.PairedDeviceInfo;

/**
 * @author CaiChiXiang
 *
 */
public class DeviceAdapter extends ArrayAdapter{

	 private ArrayList<LsDeviceInfo> modelsArrayList;
	  private Context context;
	 
	  @SuppressWarnings("unchecked")
	public DeviceAdapter(Context context, ArrayList<LsDeviceInfo> modelsArrayList) {
		
	         super(context, R.layout.scan_list_item, modelsArrayList);

	         this.context = context;
	         this.modelsArrayList = modelsArrayList;
	     }
	
	@Override
   public View getView(final int position, View convertView, ViewGroup parent) {

       // 1. Create inflater
       LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

       // 2. Get rowView from inflater
       View rowView=null;
       if(modelsArrayList.size()!=0)
       {
       	     rowView =inflater.inflate(R.layout.scan_list_item, parent, false);
             TextView nameView = (TextView) rowView.findViewById(R.id.name);
             TextView addressView=(TextView) rowView.findViewById(R.id.address);
             ImageView infoImageView= (ImageView) rowView.findViewById(R.id.info_image_view);
             TextView pairingTextView=(TextView) rowView.findViewById(R.id.pairing_text_view);
             ImageView deviceImage=(ImageView) rowView.findViewById(R.id.unpair_device_image_view);
             
             infoImageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					LsDeviceInfo deInfo=modelsArrayList.get(position);
				}
			});
             
             String sensorName=modelsArrayList.get(position).getDeviceName();
             String macAddress=modelsArrayList.get(position).getMacAddress();
             
             String deviceType=modelsArrayList.get(position).getDeviceType();
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
 	        
 	       deviceImage.setImageBitmap(getCroppedBitmap(BitmapFactory.decodeResource(getContext()
 	                .getResources(),imageResource, null)));

             nameView.setText("Device : "+sensorName);
             addressView.setText("Address : "+macAddress);
           
             String broadcastId=modelsArrayList.get(position).getBroadcastID();
             
             
             if(modelsArrayList.get(position).getPairStatus()==1)
             {
//            	 infoImageView.setBackgroundColor(Color.RED);
            	 infoImageView.setVisibility(View.GONE);
            	 pairingTextView.setBackgroundColor(Color.RED);
            	 pairingTextView.setVisibility(View.VISIBLE);
             }
             if(modelsArrayList.get(position).getPairStatus()==0 
            		 && SettingInfoManager.isDevicePaired(getContext(), broadcastId))
             {
            	 infoImageView.setBackgroundColor(Color.GREEN);
             }
             else if("BLOOD_PRESSURE_COMMAND_START_PROTOCOL".equals(macAddress) 
           		  && PairedDeviceInfo.getDeviceByBroadcastId(broadcastId, context)!=null)
             {
            	 infoImageView.setBackgroundColor(Color.GRAY);
             }
       }
      
       // 5. retrn rowView
       return rowView;
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
