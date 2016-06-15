/**
 * 
 */
package com.lifesense.ble.ui.view.paireddevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.Manifest.permission;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.widget.ScrollerCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TextView;

import com.lifesense.ble.bean.BloodPressureData;
import com.lifesense.ble.bean.DeviceTypeConstants;
import com.lifesense.ble.bean.HeightData;
import com.lifesense.ble.bean.LsDeviceInfo;
import com.lifesense.ble.bean.PedometerData;
import com.lifesense.ble.bean.WeightData_A2;
import com.lifesense.ble.bean.WeightData_A3;
import com.lifesense.ble.ui.R;
import com.lifesense.ble.ui.menu.PairedDeviceListFragment;

/**
 * @author CaiChiXiang
 *
 */
@SuppressLint("NewApi")
public class PairedDeviceListView extends ListView {


    private boolean mShouldRemoveObserver = false;

    private List<View> mViewsToDraw = new ArrayList<View>();

    private int[] mTranslate;
    
    private TextView showTextView;
    private List<BloodPressureData> mBloodPressureDataList;
    private List<WeightData_A3> mWeightDataList;
    private List<WeightData_A2> weightData_A2s;
    private List<PedometerData> pedometerDatas;
    private List<HeightData> heightDatas;

    public PairedDeviceListView(Context context) {
        super(context);
        init();
    }

    public PairedDeviceListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PairedDeviceListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOnItemClickListener(mItemClickListener);
    }
    

    public void updatePedometerRecord(PedometerData pdData)
    {
    	if(showTextView!=null)
    	{
    		System.err.println("is here==========================");
    		if(pedometerDatas==null)
    		{
    			pedometerDatas=new ArrayList<PedometerData>();
    			showTextView.setText("");
    		
    		}
    		pedometerDatas.add(pdData);
    		showPedometerRecord(pdData,pedometerDatas.indexOf(pdData));
    	}
    }
    
    
   
    
    /**
     * Listens for item clicks and expands or collapses the selected view depending on
     * its current state.
     */
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView
            .OnItemClickListener() {
        @Override
        public void onItemClick (AdapterView<?> parent, View view, int position, long id)
        
        {
            PairedDeviceListItem viewObject = (PairedDeviceListItem)getItemAtPosition(getPositionForView
                    (view));
            //
            final LsDeviceInfo pairedDevice=viewObject.getDeviceInfo();
            String deviceType=pairedDevice.getDeviceType();
            showTextView = (TextView) view.findViewById(R.id.show_record_text_view);
	      
            if (!viewObject.isExpanded()) 
            {
            	TextView recordTextView = (TextView) view.findViewById(R.id.record_number_text_view);
            	recordTextView.clearAnimation();
            	System.err.println("on item click ,show info...........");
                expandView(view);
                
//                if(PairedDeviceListFragment.hasMeasureData)
//    	        {
//    	        	showMeasureData(deviceType,viewObject);  
//    	        }
//    	        else
//    	        {
    	        	showTextView.setText("");
    	        	showPairResults(showTextView, pairedDevice);
//    	        }
                
            } else
            {
            	System.err.println("on item click ,close info...........");
            	
            	showTextView.setText("");
            	collapseView(view);
            }
        }
    };

    /**
     * Calculates the top and bottom bound changes of the selected item. These values are
     * also used to move the bounds of the items around the one that is actually being
     * expanded or collapsed.
     *
     * This method can be modified to achieve different user experiences depending
     * on how you want the cells to expand or collapse. In this specific demo, the cells
     * always try to expand downwards (leaving top bound untouched), and similarly,
     * collapse upwards (leaving top bound untouched). If the change in bounds
     * results in the complete disappearance of a cell, its lower bound is moved is
     * moved to the top of the screen so as not to hide any additional content that
     * the user has not interacted with yet. Furthermore, if the collapsed cell is
     * partially off screen when it is first clicked, it is translated such that its
     * full contents are visible. Lastly, this behaviour varies slightly near the bottom
     * of the listview in order to account for the fact that the bottom bounds of the actual
     * listview cannot be modified.
     */
    private int[] getTopAndBottomTranslations(int top, int bottom, int yDelta,
                                              boolean isExpanding) {
        int yTranslateTop = 0;
        int yTranslateBottom = yDelta;

        int height = bottom - top;

        if (isExpanding) {
            boolean isOverTop = top < 0;
            boolean isBelowBottom = (top + height + yDelta) > getHeight();
            if (isOverTop) {
                yTranslateTop = top;
                yTranslateBottom = yDelta - yTranslateTop;
            } else if (isBelowBottom){
                int deltaBelow = top + height + yDelta - getHeight();
                yTranslateTop = top - deltaBelow < 0 ? top : deltaBelow;
                yTranslateBottom = yDelta - yTranslateTop;
            }
        } else {
            int offset = computeVerticalScrollOffset();
            int range = computeVerticalScrollRange();
            int extent = computeVerticalScrollExtent();
            int leftoverExtent = range-offset - extent;

            boolean isCollapsingBelowBottom = (yTranslateBottom > leftoverExtent);
            boolean isCellCompletelyDisappearing = bottom - yTranslateBottom < 0;

            if (isCollapsingBelowBottom) {
                yTranslateTop = yTranslateBottom - leftoverExtent;
                yTranslateBottom = yDelta - yTranslateTop;
            } else if (isCellCompletelyDisappearing) {
                yTranslateBottom = bottom;
                yTranslateTop = yDelta - yTranslateBottom;
            }
        }

        return new int[] {yTranslateTop, yTranslateBottom};
    }

    /**
     * This method expands the view that was clicked and animates all the views
     * around it to make room for the expanding view. There are several steps required
     * to do this which are outlined below.
     *
     * 1. Store the current top and bottom bounds of each visible item in the listview.
     * 2. Update the layout parameters of the selected view. In the context of this
     *    method, the view should be originally collapsed and set to some custom height.
     *    The layout parameters are updated so as to wrap the content of the additional
     *    text that is to be displayed.
     *
     * After invoking a layout to take place, the listview will order all the items
     * such that there is space for each view. This layout will be independent of what
     * the bounds of the items were prior to the layout so two pre-draw passes will
     * be made. This is necessary because after the layout takes place, some views that
     * were visible before the layout may now be off bounds but a reference to these
     * views is required so the animation completes as intended.
     *
     * 3. The first predraw pass will set the bounds of all the visible items to
     *    their original location before the layout took place and then force another
     *    layout. Since the bounds of the cells cannot be set directly, the method
     *    setSelectionFromTop can be used to achieve a very similar effect.
     * 4. The expanding view's bounds are animated to what the final values should be
     *    from the original bounds.
     * 5. The bounds above the expanding view are animated upwards while the bounds
     *    below the expanding view are animated downwards.
     * 6. The extra text is faded in as its contents become visible throughout the
     *    animation process.
     *
     * It is important to note that the listview is disabled during the animation
     * because the scrolling behaviour is unpredictable if the bounds of the items
     * within the listview are not constant during the scroll.
     */

    private void expandView(final View view) {
        final PairedDeviceListItem viewObject = (PairedDeviceListItem)getItemAtPosition(getPositionForView
                (view));

        /* Store the original top and bottom bounds of all the cells.*/
        final int oldTop = view.getTop();
        final int oldBottom = view.getBottom();

        final HashMap<View, int[]> oldCoordinates = new HashMap<View, int[]>();

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            v.setHasTransientState(true);
            oldCoordinates.put(v, new int[] {v.getTop(), v.getBottom()});
        }

        /* Update the layout so the extra content becomes visible.*/
        final View expandingLayout = view.findViewById(R.id.expanding_layout);
        expandingLayout.setVisibility(View.VISIBLE);


        /* Add an onPreDraw Listener to the listview. onPreDraw will get invoked after onLayout
        * and onMeasure have run but before anything has been drawn. This
        * means that the final post layout properties for all the items have already been
        * determined, but still have not been rendered onto the screen.*/
        final ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                /* Determine if this is the first or second pass.*/
                if (!mShouldRemoveObserver) {
                    mShouldRemoveObserver = true;

                    /* Calculate what the parameters should be for setSelectionFromTop.
                    * The ListView must be offset in a way, such that after the animation
                    * takes place, all the cells that remain visible are rendered completely
                    * by the ListView.*/
                    int newTop = view.getTop();
                    int newBottom = view.getBottom();

                    int newHeight = newBottom - newTop;
                    int oldHeight = oldBottom - oldTop;
                    int delta = newHeight - oldHeight;

                    mTranslate = getTopAndBottomTranslations(oldTop, oldBottom, delta, true);

                    int currentTop = view.getTop();
                    int futureTop = oldTop - mTranslate[0];

                    int firstChildStartTop = getChildAt(0).getTop();
                    int firstVisiblePosition = getFirstVisiblePosition();
                    int deltaTop = currentTop - futureTop;

                    int i;
                    int childCount = getChildCount();
                    for (i = 0; i < childCount; i++) {
                        View v = getChildAt(i);
                        int height = v.getBottom() - Math.max(0, v.getTop());
                        if (deltaTop - height > 0) {
                            firstVisiblePosition++;
                            deltaTop -= height;
                        } else {
                            break;
                        }
                    }

                    if (i > 0) {
                        firstChildStartTop = 0;
                    }

                    setSelectionFromTop(firstVisiblePosition, firstChildStartTop - deltaTop);

                    /* Request another layout to update the layout parameters of the cells.*/
                    requestLayout();

                    /* Return false such that the ListView does not redraw its contents on
                     * this layout but only updates all the parameters associated with its
                     * children.*/
                    return false;
                }

                /* Remove the predraw listener so this method does not keep getting called. */
                mShouldRemoveObserver = false;
                observer.removeOnPreDrawListener(this);

                int yTranslateTop = mTranslate[0];
                int yTranslateBottom = mTranslate[1];

                ArrayList <Animator> animations = new ArrayList<Animator>();

                int index = indexOfChild(view);

                /* Loop through all the views that were on the screen before the cell was
                *  expanded. Some cells will still be children of the ListView while
                *  others will not. The cells that remain children of the ListView
                *  simply have their bounds animated appropriately. The cells that are no
                *  longer children of the ListView also have their bounds animated, but
                *  must also be added to a list of views which will be drawn in dispatchDraw.*/
                for (View v: oldCoordinates.keySet()) {
                    int[] old = oldCoordinates.get(v);
                    v.setTop(old[0]);
                    v.setBottom(old[1]);
                    if (v.getParent() == null) {
                        mViewsToDraw.add(v);
                        int delta = old[0] < oldTop ? -yTranslateTop : yTranslateBottom;
                        animations.add(getAnimation(v, delta, delta));
                    } else {
                        int i = indexOfChild(v);
                        if (v != view) {
                            int delta = i > index ? yTranslateBottom : -yTranslateTop;
                            animations.add(getAnimation(v, delta, delta));
                        }
                        v.setHasTransientState(false);
                    }
                }

                /* Adds animation for expanding the cell that was clicked. */
                animations.add(getAnimation(view, -yTranslateTop, yTranslateBottom));

                /* Adds an animation for fading in the extra content. */
                animations.add(ObjectAnimator.ofFloat(view.findViewById(R.id.expanding_layout),
                        View.ALPHA, 0, 1));

                /* Disabled the ListView for the duration of the animation.*/
                setEnabled(false);
                setClickable(false);

                /* Play all the animations created above together at the same time. */
                AnimatorSet s = new AnimatorSet();
                s.playTogether(animations);
                s.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        viewObject.setExpanded(true);
                        setEnabled(true);
                        setClickable(true);
                        if (mViewsToDraw.size() > 0) {
                            for (View v : mViewsToDraw) {
                                v.setHasTransientState(false);
                            }
                        }
                        mViewsToDraw.clear();
                    }
                });
                s.start();
                return true;
            }
        });
    }

    /**
     * By overriding dispatchDraw, we can draw the cells that disappear during the
     * expansion process. When the cell expands, some items below or above the expanding
     * cell may be moved off screen and are thus no longer children of the ListView's
     * layout. By storing a reference to these views prior to the layout, and
     * guaranteeing that these cells do not get recycled, the cells can be drawn
     * directly onto the canvas during the animation process. After the animation
     * completes, the references to the extra views can then be discarded.
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (mViewsToDraw.size() == 0) {
            return;
        }

        for (View v: mViewsToDraw) {
            canvas.translate(0, v.getTop());
            v.draw(canvas);
            canvas.translate(0, -v.getTop());
        }
    }

    /**
     * This method collapses the view that was clicked and animates all the views
     * around it to close around the collapsing view. There are several steps required
     * to do this which are outlined below.
     *
     * 1. Update the layout parameters of the view clicked so as to minimize its height
     *    to the original collapsed (default) state.
     * 2. After invoking a layout, the listview will shift all the cells so as to display
     *    them most efficiently. Therefore, during the first predraw pass, the listview
     *    must be offset by some amount such that given the custom bound change upon
     *    collapse, all the cells that need to be on the screen after the layout
     *    are rendered by the listview.
     * 3. On the second predraw pass, all the items are first returned to their original
     *    location (before the first layout).
     * 4. The collapsing view's bounds are animated to what the final values should be.
     * 5. The bounds above the collapsing view are animated downwards while the bounds
     *    below the collapsing view are animated upwards.
     * 6. The extra text is faded out as its contents become visible throughout the
     *    animation process.
     */

     private void collapseView(final View view) {
        final PairedDeviceListItem viewObject = (PairedDeviceListItem)getItemAtPosition
                (getPositionForView(view));

      
        /* Store the original top and bottom bounds of all the cells.*/
        final int oldTop = view.getTop();
        final int oldBottom = view.getBottom();

        final HashMap<View, int[]> oldCoordinates = new HashMap<View, int[]>();

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            v.setHasTransientState(true);
            oldCoordinates.put(v, new int [] {v.getTop(), v.getBottom()});
        }

        /* Update the layout so the extra content becomes invisible.*/
        view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                 viewObject.getCollapsedHeight()));

        
         /* Add an onPreDraw listener. */
        final ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {

                if (!mShouldRemoveObserver) {
                    /*Same as for expandingView, the parameters for setSelectionFromTop must
                    * be determined such that the necessary cells of the ListView are rendered
                    * and added to it.*/
                    mShouldRemoveObserver = true;

                    int newTop = view.getTop();
                    int newBottom = view.getBottom();

                    int newHeight = newBottom - newTop;
                    int oldHeight = oldBottom - oldTop;
                    int deltaHeight = oldHeight - newHeight;

                    mTranslate = getTopAndBottomTranslations(oldTop, oldBottom, deltaHeight, false);

                    int currentTop = view.getTop();
                    int futureTop = oldTop + mTranslate[0];

                    int firstChildStartTop = getChildAt(0).getTop();
                    int firstVisiblePosition = getFirstVisiblePosition();
                    int deltaTop = currentTop - futureTop;

                    int i;
                    int childCount = getChildCount();
                    for (i = 0; i < childCount; i++) {
                        View v = getChildAt(i);
                        int height = v.getBottom() - Math.max(0, v.getTop());
                        if (deltaTop - height > 0) {
                            firstVisiblePosition++;
                            deltaTop -= height;
                        } else {
                            break;
                        }
                    }

                    if (i > 0) {
                        firstChildStartTop = 0;
                    }

                    setSelectionFromTop(firstVisiblePosition, firstChildStartTop - deltaTop);

                    requestLayout();

                    return false;
                }

                mShouldRemoveObserver = false;
                observer.removeOnPreDrawListener(this);

                int yTranslateTop = mTranslate[0];
                int yTranslateBottom = mTranslate[1];

                int index = indexOfChild(view);
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View v = getChildAt(i);
                    int [] old = oldCoordinates.get(v);
                    if (old != null) {
                        /* If the cell was present in the ListView before the collapse and
                        * after the collapse then the bounds are reset to their old values.*/
                        v.setTop(old[0]);
                        v.setBottom(old[1]);
                        v.setHasTransientState(false);
                    } else {
                        /* If the cell is present in the ListView after the collapse but
                         * not before the collapse then the bounds are calculated using
                         * the bottom and top translation of the collapsing cell.*/
                        int delta = i > index ? yTranslateBottom : -yTranslateTop;
                        v.setTop(v.getTop() + delta);
                        v.setBottom(v.getBottom() + delta);
                    }
                }

                final View expandingLayout = view.findViewById (R.id.expanding_layout);

             
                
                /* Animates all the cells present on the screen after the collapse. */
              
                ArrayList <Animator> animations = new ArrayList<Animator>();
                for (int i = 0; i < childCount; i++) {
                    View v = getChildAt(i);
                    if (v != view) {
                        float diff = i > index ? -yTranslateBottom : yTranslateTop;
                        animations.add(getAnimation(v, diff, diff));
                    }
                }

                /* Adds animation for collapsing the cell that was clicked. */
                animations.add(getAnimation(view, yTranslateTop, -yTranslateBottom));

                /* Adds an animation for fading out the extra content. */
                animations.add(ObjectAnimator.ofFloat(expandingLayout, View.ALPHA, 1, 0));

                /* Disabled the ListView for the duration of the animation.*/
                setEnabled(false);
                setClickable(false);

                /* Play all the animations created above together at the same time. */
                AnimatorSet s = new AnimatorSet();
                s.playTogether(animations);
                s.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        expandingLayout.setVisibility(View.GONE);
                        
                        view.setLayoutParams(new AbsListView.LayoutParams(AbsListView
                                .LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
                        viewObject.setExpanded(false);
                        setEnabled(true);
                        setClickable(true);
                        /* Note that alpha must be set back to 1 in case this view is reused
                        * by a cell that was expanded, but not yet collapsed, so its state
                        * should persist in an expanded state with the extra content visible.*/
                        expandingLayout.setAlpha(1);
                    }
                });
                s.start();

                return true;
            }
        });
    }

    /**
     * This method takes some view and the values by which its top and bottom bounds
     * should be changed by. Given these params, an animation which will animate
     * these bound changes is created and returned.
     */
    private Animator getAnimation(final View view, float translateTop, float translateBottom) {

        int top = view.getTop();
        int bottom = view.getBottom();

        int endTop = (int)(top + translateTop);
        int endBottom = (int)(bottom + translateBottom);

        PropertyValuesHolder translationTop = PropertyValuesHolder.ofInt("top", top, endTop);
        PropertyValuesHolder translationBottom = PropertyValuesHolder.ofInt("bottom", bottom,
                endBottom);

        return ObjectAnimator.ofPropertyValuesHolder(view, translationTop, translationBottom);
    }


	private void showMeasureData(String deviceType, PairedDeviceListItem object)
	{
		 showTextView.setText("");
		if(deviceType!=null)
		{
			if(deviceType.equals(DeviceTypeConstants.SPHYGMOMAN_METER))
			{
				mBloodPressureDataList=PairedDeviceListFragment.bpDataList;
				showBloodPressureMeasureData(mBloodPressureDataList);
				}
			else if(deviceType.equals(DeviceTypeConstants.FAT_SCALE))
			{
				mWeightDataList=PairedDeviceListFragment.wDataList_A3;
				showWeightMeasureData(mWeightDataList);
				}
			else if(deviceType.equals(DeviceTypeConstants.WEIGHT_SCALE))
			{
				weightData_A2s=PairedDeviceListFragment.wDataList_A2;
				showWeightMeasureData_A2(weightData_A2s,object);
				}
			else if(deviceType.equals(DeviceTypeConstants.PEDOMETER))
			{
				pedometerDatas=PairedDeviceListFragment.pDataList;
				showPedometerMeasureData(pedometerDatas);
				
				}
			else if(deviceType.equals(DeviceTypeConstants.HEIGHT_RULER))
			{
				heightDatas=PairedDeviceListFragment.hDataList;
				showHeightMeasureData(heightDatas);
				}
		   else if(deviceType.equals(DeviceTypeConstants.SPHYGMOMAN_METER))
		   {
			   mBloodPressureDataList=PairedDeviceListFragment.bpDataList;
				showBloodPressureMeasureData(mBloodPressureDataList);
		   		}
			
		}
		else showTextView.setText("no measure data now!");
		
	}


	


	private void showHeightMeasureData(List<HeightData> hDatas) 
	{
		if(hDatas!=null)
		{
			showTextView.append("------The total number of records:"+hDatas.size()+"----"+"\n");
			for(HeightData hData:hDatas)
			{ 
				  showTextView.append("Record Number:"+hDatas.indexOf(hData)+"\n");
	        	  showTextView.append("Measuring time："+ hData.getDate()+"\n");
	        	  showTextView.append("DeviceSn："+ hData.getDeviceSn()+"\n");
	        	  showTextView.append("UserNumber："+ hData.getUserNo()+"\n");
	        	  showTextView.append("Height："+ hData.getHeight()+"\n");
	        	  showTextView.append("Unit ："+ hData.getUnit()+"\n");
	        	  showTextView.append("Battery："+ hData.getBattery()+"\n");
	        	  showTextView.append("----------------------------------"+"\n");
			}
			
		}
		else 
		{
			  showTextView.append("No measured Record"+"\n");
			}
		
	}


	private void showPedometerMeasureData(List<PedometerData> pDatas)
	{
		if(pDatas!=null)
		{
			showTextView.append("------The total number of records:"+pDatas.size()+"----"+"\n");
		
			for(PedometerData pData:pDatas)
			{ 
				showPedometerRecord(pData,pDatas.indexOf(pData));
			}
			
		}
		else 
		{
			  showTextView.append("No measured Record"+"\n");
			}
		
	}

	/**
	 * @param pData
	 */
	private void showPedometerRecord(PedometerData pData,int recordNumber) 
	{
	
		showTextView.append("Record Number:"+recordNumber+"\n");
		showTextView.append("Measuring time："+ pData.getDate()+"\n");
		showTextView.append("DeviceSN："+ pData.getDeviceSn()+"\n");
		showTextView.append("UserNumber："+ pData.getUserNo()+"\n");
		showTextView.append("WalkSteps："+ pData.getWalkSteps()+"\n");
		showTextView.append("RunSteps ："+ pData.getRunSteps()+"\n");
		showTextView.append("Calories："+ pData.getCalories()+"\n");
		showTextView.append("ExerciseTime："+ pData.getExerciseTime()+"\n");
		showTextView.append("Distance："+ pData.getDistance()+"\n");
		showTextView.append("Battery："+ pData.getBattery()+"\n");
		showTextView.append("SleepStatus："+ pData.getSleepStatus()+"\n");
		showTextView.append("IntensityLevel："+ pData.getIntensityLevel()+"\n");
		showTextView.append("----------------------------------"+"\n");
		
	}

	private void showWeightMeasureData_A2(List<WeightData_A2> wData_A2s, PairedDeviceListItem object)
	{
		if(wData_A2s!=null)
		{
			showTextView.append("------The total number of records:"+wData_A2s.size()+"----"+"\n");
			for(WeightData_A2 wData:wData_A2s)
			{ 
				  showTextView.append("Record Number:"+wData_A2s.indexOf(wData)+"\n");
				  showTextView.append("Measuring time："+ wData.getDate()+"\n");
		    	  showTextView.append("DeviceSN："+ wData.getDeviceSn()+"\n");
		    	  showTextView.append("UserNumber："+ wData.getUserNo()+"\n");
		    	  showTextView.append("Weight："+ wData.getWeight()+"\n");
		    	  showTextView.append("Fat rate："+ wData.getPbf()+"\n");
		    	  showTextView.append("Resistance_1："+ wData.getResistance_1()+"\n");
		    	  showTextView.append("Resistance_2："+ wData.getResistance_2()+"\n");
		    	  showTextView.append("Unit："+ wData.getDeviceSelectedUnit()+"\n");
		    	  showTextView.append("VoltageData："+ wData.getVoltageData()+"\n");
		    	  showTextView.append("Battery level:"+(int)wData.getBattery()+"\n");
		    	  if(wData.getResistance_2()>0)
		    	  {
		    		  showTextView.append("BodyFatRatio："+ wData.getBodyFatRatio()+"\n");
		    		  showTextView.append("BodyWaterRatio："+ wData.getBodyWaterRatio()+"\n");
		    		  showTextView.append("MuscleMassRatio："+ wData.getMuscleMassRatio()+"\n");
		    		  showTextView.append("BoneDensity："+ wData.getBoneDensity()+"\n");
		    		  showTextView.append("BasalMetabolism："+ wData.getBasalMetabolism()+"\n");
		    	  }
	        	  showTextView.append("----------------------------------"+"\n");
			}
			
		}
		else 
		{
			  showTextView.append("No measured Record"+"\n");
			}
		
	}


	private void showWeightMeasureData(List<WeightData_A3> wDataList) {
		
		if(wDataList!=null && wDataList.size()>0)
		{
			showTextView.append("------The total number of records:"+wDataList.size()+"----"+"\n");
			for(WeightData_A3 wData:wDataList)
			{
				 showTextView.append("Record Number:"+wDataList.indexOf(wData)+"\n");
				 showTextView.append("Measuring time："+ wData.getDate()+"\n");
				 showTextView.append("DeviceSn：" + wData.getDeviceSn() + "\n");
		    	  showTextView.append("mpedance："+ wData.getImpedance()+"\n");
		    	  showTextView.append("UserNumber："+ wData.getUserId()+"\n");
		    	  showTextView.append("isAppendMeasurement："+ wData.isAppendMeasurement()+"\n");
		    	  showTextView.append("Battery："+ wData.getBattery()+"\n");
		    	  showTextView.append("WeightStatus："+ wData.getWeightStatus()+"\n");
		    	  showTextView.append("ImpedanceStatus："+ wData.getImpedanceStatus()+"\n");
		    	  showTextView.append("AccuracyStatus："+ wData.getAccuracyStatus()+"\n");    	  
		    	  showTextView.append("Weight："+ wData.getWeight()+"\n");
		    	  showTextView.append("BodyFatRatio："+ wData.getBodyFatRatio()+"\n");
		    	  showTextView.append("BodyWaterRatio："+ wData.getBodyWaterRatio()+"\n");
		    	  showTextView.append("VisceralFatLevel："+ wData.getVisceralFatLevel()+"\n");
		    	  showTextView.append("MuscleMassRatio："+ wData.getMuscleMassRatio()+"\n");
		    	  showTextView.append("BoneDensity："+ wData.getBoneDensity()+"\n");
		    	  showTextView.append("BasalMetabolism："+ wData.getBasalMetabolism()+"\n");
		    	  showTextView.append("Unit："+ wData.getDeviceSelectedUnit()+"\n");
		    	  showTextView.append("----------------------------------"+"\n");
			}
		}
		else {showTextView.append("No measured records"+"\n");}
	}


	private void showBloodPressureMeasureData(List<BloodPressureData> bpDataList) 
	{
		if(bpDataList!=null && bpDataList.size()>0)
		{
			showTextView.append("------The total number of records:"+bpDataList.size()+"----"+"\n");
			for(BloodPressureData bData:bpDataList)
			{
				showTextView.append("Record Number:"+bpDataList.indexOf(bData)+"\n");
				showTextView.append("DeviceSn：" + bData.getDeviceSn() + "\n");
				showTextView.append("BroadcastID：" + bData.getBroadcastId() + "\n");
				showTextView.append("Measuring time：" + bData.getDate() + "\n");
				showTextView.append("userNumber：" + bData.getUserId() + "\n");
				showTextView.append("Systolic：" + bData.getSystolic() + "\n");
				showTextView.append("Diastolic ：" + bData.getDiastolic() + "\n");
				showTextView.append("PulseRate：" + bData.getPulseRate() + "\n");
				showTextView.append("MeanArterialPressure：" + bData.getMeanArterialPressure() + "\n");
				showTextView.append("Unit：" + bData.getDeviceSelectedUnit() + "\n");
				showTextView.append("Battery：" + bData.getBattery() + "\n");
				showTextView.append("----------------------------------"+"\n");
				}
			}
		else {
			showTextView.append("No measured record"+"\n");}
	}
	
    
    private void showPairResults(TextView infoTextView ,LsDeviceInfo device) 
    {
    	if(device!=null)
    	{   
    		infoTextView.append("------------------------------------"+"\n");
    		infoTextView.append("deviceName: "+device.getDeviceName()+"\n");
    		infoTextView.append("broadcastID: "+device.getBroadcastID()+"\n");
    		infoTextView.append("deviceType: "+device.getDeviceType()+"\n"); 
    		infoTextView.append("password: "+device.getPassword()+"\n"); 
    		infoTextView.append("deviceID: "+device.getDeviceId()+"\n");
    		infoTextView.append("deviceSN: "+device.getDeviceSn()+"\n");
    		infoTextView.append("manufactureName: "+device.getManufactureName()+"\n");	        	
    		infoTextView.append("modelNumber: "+device.getModelNumber()+"\n");  	      	  
    		infoTextView.append("firmwareVersion: "+device.getFirmwareVersion()+"\n");
    		infoTextView.append("hardwareVersion: "+device.getHardwareVersion()+"\n");   
    		infoTextView.append("softwareVersion: "+device.getSoftwareVersion()+"\n");
    		infoTextView.append("UserNumber: "+device.getDeviceUserNumber()+"\n");
    		infoTextView.append("maxUserQuantity: "+device.getMaxUserQuantity()+"\n");
    		infoTextView.append("protocolType: "+device.getProtocolType()+"\n");
    	}
    	else
    	{
    		infoTextView.append("Failed to get paired device info"+"\n");
    	}	 	       
    }

    
}
