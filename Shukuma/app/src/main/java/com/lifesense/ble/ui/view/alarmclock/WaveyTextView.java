/**
 * 
 */
package com.lifesense.ble.ui.view.alarmclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author other
 *
 */
public class WaveyTextView extends TextView {

	private int leftOffset = 0;
	private enum TransitionState{TRANSITION_STARTING, TRANSITION_RUNNING, TRANSITION_NONE};
	private TransitionState transitionState;
	private double animDuration = 0;
	private double startTimeMillis;

	private Path wavePath = null;
	private final int pxWLength = 175;

	
	
	public WaveyTextView(final Context ctx) {
		super(ctx);
	}
	
	public WaveyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public WaveyTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	    

	public final void resetTextPosition() {
		leftOffset = 0;
		transitionState = TransitionState.TRANSITION_NONE;
		invalidate();
	}

	public final void animateToRight(final double animDuration) {
		this.animDuration = animDuration;
		transitionState = TransitionState.TRANSITION_STARTING;
		invalidate();
	}

	@Override
	public void onDraw(final Canvas canvas) {
		if(wavePath==null) {
			generateWavePath();
		}
		boolean done = true;
		switch(transitionState) {
		case TRANSITION_STARTING:
			done = false;
			transitionState = TransitionState.TRANSITION_RUNNING;
			startTimeMillis = SystemClock.uptimeMillis();
			break;
		case TRANSITION_RUNNING:
			double normalized = (SystemClock.uptimeMillis() - startTimeMillis)
			/ animDuration;
			done = normalized >= 1.0;
			normalized = Math.min(normalized, 1.0);
			leftOffset = (int) (getWidth() * normalized);
			break;
		default:
			break;
		}
		canvas.drawTextOnPath(getText().toString(), wavePath, leftOffset, (getHeight()-(getPaddingTop()+getPaddingBottom()))/4, getPaint());
		if(!done) {
			invalidate();
		}
	}

	private void generateWavePath() {
		wavePath = new Path();
		int lOffset = 0;
		int ct = 0;
		wavePath.moveTo(0, getHeight()/2);
		while(lOffset < getWidth()) {
			wavePath.quadTo(lOffset+pxWLength/4, getHeight() * (ct++ % 2), lOffset+pxWLength/2, getHeight()/2);
			lOffset += pxWLength/2;
		}
	}
	
}
