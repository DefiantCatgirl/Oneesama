package catgirl.oneesama2.legacy.legacyreader.widgets.airviewer;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;

/**
 * A class that contains both static page parameters and live page parameters - under normal circumstances
 * there should be two classes, but count me lazy this time. Possible todo.
 *
 */
public class AirPage {

    final public AirPage self = this;

	// Stable parameters
	
	static float maxZoomLevel = 10;
	
	public int pageId;
	
	// Base page dimensions (properties.js)
	
	// Container size
	public int defaultWidth;
	public int defaultHeight;
	
	// Page in container proportions
	public float proportion = 1f;
	
	// Page zoom parameters
	
	public boolean fitWidthPortrait;
	public boolean fitWidthLandscape;

//	public int pageWidth;
//	public int pageHeight;

	public boolean hasAlpha = false;

	public float alpha;
	
	// Stable parameters constructor - can't do without those
	
	public AirPage(int pageId, int defaultWidth, int defaultHeight, boolean fitWidthPortrait, boolean fitWidthLandscape, int bgcolor)
	{
		this.pageId = pageId; // for convenience purposes mostly
		this.defaultHeight = defaultHeight;
		this.defaultWidth = defaultWidth;
		this.fitWidthPortrait = fitWidthPortrait;
		this.fitWidthLandscape = fitWidthLandscape;

		this.bgcolor = bgcolor;

//		this.pageHeight = pageHeight;
//		this.pageWidth = pageWidth;
	}
	
	// Live parameters
	
	public boolean busy = false;
	public boolean shouldRecycle = false;
	
	// Page state
	public boolean isDownloading = false;
	public float progress = 0;
	
	// Drawables
	
	// Main page bitmap
	public Drawable pageBitmap;
	
	// Background color
	public int bgcolor = Color.rgb(200, 200, 200);
	
	public void setBitmap(Drawable bitmap)
	{
		pageBitmap = bitmap;
		
        if(delegate != null)
            delegate.onPageInvalidated(self, self.pageId);
	}
	
	private boolean bitmapLocked = false;
	
	public synchronized Drawable lockBitmap()
	{
		while(bitmapLocked);
		
		bitmapLocked = true;
		
		return pageBitmap;
	}
	
	public void unlockBitmap()
	{
		bitmapLocked = false;
	}

	public void invalidate()
	{
        if(delegate != null)
            delegate.onPageInvalidated(self, self.pageId);
	}
	
	// Easy modo - just Canvas coordinates, we don't need much else right now
	
	public float x = 0;
	public float y = 0;
	
	public float zoom;

    // Page delegate
    
	public AirPageDelegate delegate;
	
	/**
	 *  Live parameters initialization
	 */
	public void initLivePage(AirPageDelegate delegate)
	{
		this.delegate = delegate;
	}

    public float getWidth()
    {
        return defaultWidth * zoom;
    }

    public float getHeight()
    {
        return defaultHeight * zoom;
    }

	/**
	 * Get minimal zoom according to zoom parameters and viewport
	 * @param width - viewport width
	 * @param height - viewport height
	 * @return minimal zoom relative to default page size (zoom is always centered in the current AirViewer)
	 */
	public float getMinZoom(int width, int height, boolean isLandscape)
	{
		boolean fitWidth = false;
		if(isLandscape)
			fitWidth = fitWidthLandscape;
		else
			fitWidth = fitWidthPortrait;

		if(fitWidth)
			return ((float) width / (float) defaultWidth);
		else
			return Math.min((float) width / (float) defaultWidth, (float) height / (float) defaultHeight);
			
	}
	
	/**
	 * Get maximum zoom of a page. Maximum of 10 or minZoom * 2
	 * @param width - viewport width
	 * @param height - viewport height
	 * @return maximum zoom relative to default page size (zoom is always centered in the current AirViewer)
	 */
	public float getMaxZoom(int width, int height, boolean isLandscape)
	{
		return Math.max(getMinZoom(width, height, isLandscape) * 2f, maxZoomLevel);
	}
	
	/**
	 * Translate surface coordinates to page coordinates
	 * @param coordinates - surface coordinates
	 * @return page coordinates
	 */
	public PointF surfaceToPageCoordinates(PointF coordinates)
	{
		return new PointF((coordinates.x - x) / zoom, (coordinates.y - y) / zoom);
	}
	
	/**
	 * Translate page coordinates to surface coordinates
	 * @param coordinates - page coordinates
	 * @return surface coordinates
	 */
	public PointF pageToSurfaceCoordinates(PointF coordinates)
	{
		return new PointF(coordinates.x * zoom + x, coordinates.y * zoom + y);
	}

    /**
     * By app logic single tap only shows interface, the links react to tap down and tap up, so
     * this function only has to check whether there was a link, otherwise it will return false
     * so that the app can show the interface
     * @param x
     * @param y
     * @return whether there was a link at those coordinates
     */
	public boolean onSingleTap(float x, float y)
    {
        return false;
    }

	long tapdowntime = 0;
	boolean waiting = false;
	
	public boolean onTapDown(float x, float y)
    {
		return false;
    }

    public boolean onDragTo(float x, float y, float distx, float disty)
    {
        return false;
    }

    public boolean onTapUp(float x, float y)
    {
    	return false;
    }
}
