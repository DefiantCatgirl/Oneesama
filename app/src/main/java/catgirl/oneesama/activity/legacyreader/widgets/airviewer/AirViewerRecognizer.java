package catgirl.oneesama.activity.legacyreader.widgets.airviewer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * A view that will handle motion events and send it to the AirViewer. It handles separation of event sequences (no drag while zoom, no zoom while drag).
 * It also handles single tap and tap up in a way that will prevent a double tap from causing both unnecessarily.
 * 
 * Make sure it has the same dimensions as the drawer! I'll either combine the two or resolve the dependence myself later.
 *
 */
public class AirViewerRecognizer extends View {

	private AirViewer viewer;
	
	// Delay after which we should be fairly sure double tap is not going to happen, counter starts from tap down
	int doubleTapDelay = 300;
	
	boolean isZoomActive = false;
	boolean isDragActive = false;
	
	boolean waiting = false;
	boolean wasUp = false;
	boolean movedAway = false;
	boolean doubleTapHappened = false;
	int ux = 0, uy = 0;
	
	public boolean isActive = true;
	
	// Default View constructors
	public AirViewerRecognizer(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); init(); }	
	public AirViewerRecognizer(Context context, AttributeSet attrs) { super(context, attrs); init(); }	
	public AirViewerRecognizer(Context context) { super(context); init();}

	public void init()
	{
		if(this.isInEditMode())
			return;
		
	    mScaleDetector = new ScaleGestureDetector(this.getContext(), new ScaleListener());
        gestureDetector = new GestureDetector(this.getContext(), new MyGestureDetector());
	}
	
	public void setAirViewer(AirViewer viewer)
	{
		this.viewer = viewer;
	}
	
	float mLastTouchX;
	float mLastTouchY;
	
	boolean lastWasZoom = false;
	
	boolean retainUp = false;
	
	// Main function that handles touch
	@Override
	public boolean onTouchEvent(MotionEvent me)
	{
		if(viewer == null)
			return false;
		
		if(!isActive)
			return false;

		mScaleDetector.onTouchEvent(me);
		
		if(isZoomActive)
			return true;
		
		if(me.getPointerCount() > 1)
			return true;
		
		retainUp = false;
		
		if(me.getAction() == MotionEvent.ACTION_UP)
		{
			retainUp = true;
			
			isDragActive = false;
		}
		if(me.getAction() == MotionEvent.ACTION_DOWN)
		{
			lastWasZoom = false;
			viewer.onTapDown((int)me.getX(), (int)me.getY());
			mLastTouchX = me.getX();
			mLastTouchY = me.getY();
		}
		if(me.getAction() == MotionEvent.ACTION_MOVE) 
		{
	            
	        final float x = me.getX();
	        final float y = me.getY();
	            
	        // Calculate the distance moved
	        final float dx = x - mLastTouchX;
	        final float dy = y - mLastTouchY;

	        isDragActive = true;
			viewer.onDragTo(me.getX(), me.getY(), dx, dy);

	        // Remember this touch position for the next move event
	        mLastTouchX = x;
	        mLastTouchY = y;
		}
		
		gestureDetector.onTouchEvent(me);
		
		if(retainUp)
		{
			viewer.onTapUp((int)me.getX(), (int)me.getY());
		}
		

		return true;
	}
	
	private static final int SWIPE_MIN_DISTANCE = 40;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 300;
	private GestureDetector gestureDetector;
	
	private ScaleGestureDetector mScaleDetector;
	
    class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	viewer.onSwipe((int)e1.getX(), (int)e1.getY(), true);
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	viewer.onSwipe((int)e1.getX(), (int)e1.getY(), false);
                }
            return false;
        }
        
        @Override
        public boolean onDoubleTap(MotionEvent e)
        {
        	viewer.onDoubleTap((int)e.getX(), (int)e.getY());
        	doubleTapHappened = true;
        	return false;
        }
        
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
        	if(!lastWasZoom)
        		viewer.onSingleTap((int)e.getX(), (int)e.getY());
        	return false;
        }
        
        @Override
        public void onLongPress(MotionEvent e)
        {
        	//viewer.onLongTap((int)e.getX(), (int)e.getY());
        }
        
        @Override
        public boolean onScroll (MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
        	return true;
        }
    }


	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
	    @Override
	    public boolean onScaleBegin(ScaleGestureDetector detector)
	    {
	    	return true;
	    }
		
		@Override
	    public boolean onScale(ScaleGestureDetector detector) {
	    	viewer.onZoom(detector.getScaleFactor(), (int)detector.getFocusX(), (int)detector.getFocusY());
	    	isZoomActive = true;
	    	lastWasZoom = true;
	        return true;
	    }
	    
	    @Override
	    public void onScaleEnd(ScaleGestureDetector detector)
	    {
	    	isZoomActive = false;
	    }
	}
	
}
