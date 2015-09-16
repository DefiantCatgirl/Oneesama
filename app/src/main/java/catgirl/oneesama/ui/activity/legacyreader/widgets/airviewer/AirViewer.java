package catgirl.oneesama.ui.activity.legacyreader.widgets.airviewer;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import catgirl.oneesama.ui.activity.legacyreader.tools.EndAnimatorListener;

/**
 * A class that controls the positions of AirPages. Relies on a drawer delegate to actually draw the pages, itself only changing coordinates.
 * E.g. the pages might be drawn as widgets, painted on a Canvas, used as a basis for an OpenGL renderer, etc. The latter would require smarter caching, though.
 * Gets pages from another delegate, most likely defined in an activity.
 *
 */
public class AirViewer implements AirPageDelegate {
	
	public AirViewer self = this;

	// The viewer draws three pages. Technically it only needs two, but the drawer can just omit drawing the one that's off-screen.
	// It's more convenient to work with three.
	
	public AirPage currentPage;
	public AirPage leftPage;
	public AirPage rightPage;
	
	public int currentPageId = 0;
	
	// Whether it's in landscape mode at the moment
	public boolean isLandscape;
	
	// Some publications require retaining zoom when switching pages
	public boolean retainZoom;
	
	// Arabic publications and such, right-to-left mode, might be helpful for manga?..
	public boolean rtl;

	// Delegates for getting pages/sending events and drawing the pages respectively.
	
	public AirViewerDelegate delegate;
	public AirViewerDrawDelegate drawDelegate;
	
	// Can be turned to false in case of interface overlay like bookmarks or web video.
	
	public boolean acceptsInput = true;
	
	// Canvas parameters
	
	public int width;
	public int height;
	
	public int pageGap = 50;
	
	public Context context;

	public boolean tabletMode;

	// Methods
	
	/**
	 * Your run of the mill constructor
	 * @param delegate - delegate to get pages and notify changes
	 * @param drawDelegate - delegate to draw pages
	 */
	public AirViewer(Context context, AirViewerDelegate delegate, AirViewerDrawDelegate drawDelegate, boolean retainZoom, boolean rtl, boolean tabletMode)
	{
		this.delegate = delegate;
		this.drawDelegate = drawDelegate;

		this.tabletMode = tabletMode;
		
		this.retainZoom = retainZoom;
		this.rtl = rtl;
	}
	
	// -------------------------------------------------- //
	// Initialization and page positions                  //
	// -------------------------------------------------- //
	
	boolean started = false;
	
	/**
	 * Call this method when everything is ready, especially the draw delegate. Can also be used to reset the viewer, I guess.
	 * Can be used when device is rotated - should retain zoom and coordinates
	 * @param startPageId - page to start
	 */
	public void start(final int startPageId, final boolean reZoom, final float ssx, final float ssy, final float sszoom, final boolean isLandscape, final boolean forceUnzoom)
	{
		if(animationActive)
		{
			Handler h = new Handler();
			h.postDelayed(new Runnable(){

				@Override
				public void run() {
					start(startPageId, reZoom, ssx, ssy, sszoom, isLandscape, forceUnzoom);
				}}, 100);
			return;
		}

		currentPageId = startPageId;
		
		this.isLandscape = isLandscape;
		
		Point p = drawDelegate.getDimensions();
		
		width = p.x;
		height = p.y;
		
		float sx = 0, sy = 0, szoom = 1;
		
		if(started)
		{
			sx = currentPage.x;
			sy = currentPage.y;
			szoom = currentPage.zoom;
		}
		
		// reset pages
		
		currentPage = getPage(currentPageId);
		if(currentPage != null)
			currentPage.delegate = this;
		if(rtl)
		{
			leftPage = getPage(currentPageId + 1);
			rightPage = getPage(currentPageId - 1);
		}
		else
		{
			leftPage = getPage(currentPageId - 1);
			rightPage = getPage(currentPageId + 1);
		}
		
		resetPage(currentPage);
		
		if(forceUnzoom)
		{
			currentPage.zoom = currentPage.getMinZoom(width, height, isLandscape);
			fixToBounds();
		}
		else if(reZoom)
		{
			currentPage.zoom = sszoom;
			if(currentPage.zoom < currentPage.getMinZoom(width, height, isLandscape))
				currentPage.zoom = currentPage.getMinZoom(width, height, isLandscape);
			if(currentPage.zoom > currentPage.getMaxZoom(width, height, isLandscape))
				currentPage.zoom = currentPage.getMaxZoom(width, height, isLandscape);
			currentPage.x = ssx;
			currentPage.y = ssy;
			
			fixToBounds();
		}
		else if(started)
		{
			currentPage.zoom = szoom;
			currentPage.x = sx;
			currentPage.y = sy;
			
			if(currentPage.zoom < currentPage.getMinZoom(width, height, isLandscape))
				currentPage.zoom = currentPage.getMinZoom(width, height, isLandscape);
			if(currentPage.zoom > currentPage.getMaxZoom(width, height, isLandscape))
				currentPage.zoom = currentPage.getMaxZoom(width, height, isLandscape);
			
			fixToBounds();
		}

		resetPage(leftPage);
		resetPage(rightPage);

		repositionPages();
		
		updatePositionDelegate();
		
		if(drawDelegate != null)
			drawDelegate.changePages(leftPage, currentPage, rightPage);
		
		if(delegate != null)
			delegate.onPageChanged(currentPageId);
		
		started = true;
	}
	
	private AirPage getPage(int pageId)
	{
		if(delegate != null)
		{
			AirPage p = delegate.getPage(pageId);
			if(p != null)
				p.delegate = this;
			return p;
		}
		else return null;
	}
	
	/**
	 * Call to reset the page to the currentPage position at minimal zoom, use repositionPages afterwards
	 * if you're using it for the left or the right page
	 * @param page
	 */
	public void resetPage(AirPage page)
	{
		if(page == null)
			return;
		
		page.initLivePage(this);
		
		page.zoom = page.getMinZoom(width, height, isLandscape);
		
		if(retainZoom)
		{
			page.zoom = currentPage.zoom;
			
			if(page.zoom < page.getMinZoom(width, height, isLandscape))
				page.zoom = page.getMinZoom(width, height, isLandscape);
			if(page.zoom > page.getMaxZoom(width, height, isLandscape))
				page.zoom = page.getMaxZoom(width, height, isLandscape);
		}
		
		page.y = 0;
		page.x = getPageStretchback(page, false);
		page.alpha = 1;
		
		fixY(page);
	}
	
	/**
	 * Repositions the left and right pages to be to the left and right of the current page accordingly.
	 * Use after drag events, zoom does not require this.
	 */
	public void repositionPages()
	{
		if(leftPage != null)
		{
			if(currentPage.getWidth() > width / 1.05f && leftPage.getWidth() >= width / 1.05f)
				leftPage.x = currentPage.x - leftPage.getWidth() - pageGap; 
			else
				leftPage.x = (currentPage.x - ((width - currentPage.getWidth()) / 2f)) - ((width / 2 - leftPage.getWidth() / 2)) - leftPage.getWidth();
			
			fixY(leftPage);
		}
		
		if(rightPage != null)
		{
			if(currentPage.getWidth() >= width / 1.05f && rightPage.getWidth() >= width / 1.05f)
				rightPage.x = currentPage.x + currentPage.getWidth() + pageGap; 
			else
				rightPage.x = currentPage.x + currentPage.getWidth() + (width - currentPage.getWidth()) / 2f + (width / 2 - rightPage.getWidth() / 2);
			
			fixY(rightPage);
		}
	}
	
	public void fixY(AirPage page)
	{
		if(page.getHeight() < height)
			page.y = (height - page.getHeight()) / 2f;
		else
		{
			if(page.y > 0)
				page.y = 0;
			
			if(page.y < height - page.getHeight())
				page.y = height - page.getHeight();
		}
	}
	
	/**
	 * Fix the page x coordinate according to its current width - center or bring to an edge.
	 * Pick the edge manually since it can be also used to init a page. 
	 * @param page - page to fix
	 * @param snapRight - whether you should snap to the left or to the right edge
	 * @return
	 */
	public float getPageStretchback(AirPage page, boolean snapRight)
	{
		return getPageStretchBackNormal(page, snapRight);
	}
	
	public float getPageStretchBackNormal(AirPage page, boolean snapRight)
	{
		if(page.getWidth() >= width && !snapRight)
			return 0;
		else if(page.getWidth() >= width && snapRight)
			return width - page.getWidth();
		else
			return (width - page.getWidth()) / 2;
	}
	
	/**
	 * Auto fix x
	 * @param page
	 * @return
	 */
	public float getPageStretchback(AirPage page)
	{
		if(page.x + page.getWidth() <= width)
			return getPageStretchback(page, true);
		else
			return getPageStretchback(page, false);
	}
	
	
	// -------------------------------------------------- //
	// Input event processing                             //
	// -------------------------------------------------- //
	
	boolean zoomActive = false;
	boolean dragActive = false;
	boolean inertiaActive = false;
	boolean animationActive = false;
	
	public float lastDx = 0, lastDy = 0;
	public float lastVx = 0, lastVy = 0;
	public float lastTime = 0;
	
	boolean shouldCancelInertia = false;
	
	boolean lockHorizontalDrag = false;
	boolean lockVerticalDrag = false;
	boolean noLock = true;
	float startDragX = 0;
	float startDragY = 0;
	
	public synchronized void onTapDown(int x, int y)
	{
		if(animationActive || zoomActive || dragActive)
			return;
		
		if(inertiaActive)
			cancelInertia();
		
		// Check for links
		currentPage.onTapDown(x, y);
		
		lastTime = System.nanoTime() / 1000;
		
    	if(self.drawDelegate != null)
    		self.drawDelegate.redrawPages();
	}
	
	public synchronized void onTapUp(int x, int y)
	{
		lockHorizontalDrag = false;
		lockVerticalDrag = false;
		noLock = false;

		
		if(animationActive)
			return;
		
		if(dragActive)
		{
			updatePositionDelegate();
		}
		
		dragActive = false;
		
		if(zoomActive)
		{
			if(zoomDownUnlocked && currentPage.zoom < currentPage.getMinZoom(width, height, isLandscape))
			{	
				lastFactor = currentPage.zoom;
				
				animationActive = true;
				ValueAnimator v = ObjectAnimator.ofFloat(this, "val", 0, 1);
				v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
				{
					boolean tstarted = false;
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
			        	self.doAbsZoom(((1 - animation.getAnimatedFraction()) * (lastFactor - lastFactor / 10f)) + (lastFactor / 10f), self.lastZoomX, self.lastZoomY);
			        	
			        	currentPage.hasAlpha = true;
			        	currentPage.alpha = 1 - animation.getAnimatedFraction();
			        	
			        	if(animation.getCurrentPlayTime() > 250 && !tstarted)
			        	{
			        		tstarted = true;
							if(delegate != null)
								delegate.onShowThumbnails();
			        	}
			        
			        	if(self.drawDelegate != null)
			        		self.drawDelegate.redrawPages();
					}
				});
				v.addListener(new EndAnimatorListener(){
					@Override
					public void onAnimationEnd(Animator animation) {
			        	if(self.drawDelegate != null)
			        		self.drawDelegate.redrawPages();	        	
		            	
			        	Handler handler = new Handler();
			        	handler.postDelayed(new Runnable() {

			        	    public void run() {
					        	animationActive = false;     
				            	
				        		zoomDownUnlocked = false;
				        		
				        		currentPage.hasAlpha = false;
				        		currentPage.alpha = 1f;

				            	currentPage.zoom = currentPage.getMinZoom(width, height, isLandscape);
				            	self.fixToBounds();
				            	
				            	repositionPages();
				            	
				            	checkSchedules();
				            	
				            	updatePositionDelegate();
				            	
				            	if(self.drawDelegate != null)
				            		self.drawDelegate.redrawPages();
			        	    }

			        	}, 500);
					}
				});
				
				v.setDuration(600);
			    
			    v.start();
			}
			updatePositionDelegate();
		}
		else
		{
			// Check for links
			if(!currentPage.onTapUp(x, y))
			{
			// Switch pages if required
				float s = getPageStretchback(currentPage, true);
				if(currentPage.x < s - 0.3 * width)
				{
					goRight();
				}
				else
				{
					s = getPageStretchback(currentPage, false);
					if(currentPage.x > s + 0.3 * width)
					{
						goLeft();
					}
				}
			}
		}
		
		zoomActive = false;
		dragActive = false;
		
		// Snap back
		if(currentPage.zoom >= currentPage.getMinZoom(width, height, isLandscape))
			if(currentPage.x > 0 || currentPage.x + currentPage.getWidth() < width)
				stretchBack();
		
    	if(self.drawDelegate != null)
    		self.drawDelegate.redrawPages();
	}
	
	public synchronized void onDragTo(float x, float y, float distx, float disty)
	{
		if(animationActive || zoomActive)
			return;
		 
		if(!dragActive)
		{
			startDragX = x;
			startDragY = y;
		}
		else if(!noLock && !lockHorizontalDrag && !lockVerticalDrag)
		{
			if(Math.sqrt((x - startDragX) * (x - startDragX) + (y - startDragY) * (y - startDragY)) > this.dpToPx(48))
				noLock = true;
			else if(Math.sqrt((x - startDragX) * (x - startDragX) + (y - startDragY) * (y - startDragY)) > this.dpToPx(12))
			{
				if(Math.abs(y - startDragY) < this.dpToPx(7))
					lockHorizontalDrag = true;
				else if(Math.abs(x - startDragX) < this.dpToPx(7))
					lockVerticalDrag = true;
			}
		}
		
		dragActive = true;
		
		cancelInertia();
		
		if(lockHorizontalDrag)
			disty = 0;
		else if(lockVerticalDrag)
			distx = 0;
		
		// Update link highlights
		currentPage.onDragTo(x, y, distx, disty);
		
		// Drag the page
		currentPage.x += distx;
		currentPage.y += disty;
		
		// Fix the page coordinate
		if(currentPage.getHeight() < height)
			currentPage.y = (height - currentPage.getHeight()) / 2f;
		else
		{
			if(currentPage.y > 0)
				currentPage.y = 0;
			else if(currentPage.y < height - currentPage.getHeight())
				currentPage.y = height - currentPage.getHeight();
		}
		
		if(leftPage == null)
		{
			float s = getPageStretchback(currentPage, false);
			if(currentPage.x > s)
				currentPage.x = s;
		}
		if(rightPage == null)
		{
			float s = getPageStretchback(currentPage, true);
			if(currentPage.x < s)
				currentPage.x = s;
		}
		
		// Reposition neighbours
		repositionPages();
		
		lastDx = distx;
		lastDy = disty;
		
    	if(self.drawDelegate != null)
    		self.drawDelegate.redrawPages();
	}
	
	boolean zoomDownUnlocked = false;
	int lastZoomX, lastZoomY;
	float lastFactor;
	
	public synchronized void onZoom(float factor, int x, int y)
	{
		if(animationActive)
			return;
		
		if(!zoomActive)
		{
			if(currentPage.zoom < currentPage.getMinZoom(width, height, isLandscape) * 1.05f)
				zoomDownUnlocked = true;
			else
				zoomDownUnlocked = false;
		}
		
		zoomActive = true;
		
		lastZoomX = x;
		lastZoomY = y;
		
		cancelInertia();

		doZoom(factor, x, y);
		
		if(!zoomDownUnlocked)
		{
			resetPage(leftPage);
			resetPage(rightPage);
			repositionPages();
		}
		
//		updatePositionDelegate();
		
    	if(self.drawDelegate != null)
    		self.drawDelegate.redrawPages();
	}
	
	public void doAbsZoom(float targetZoom, int x, int y)
	{
		float minZoom = currentPage.getMinZoom(width, height, isLandscape);
		float maxZoom = currentPage.getMaxZoom(width, height, isLandscape);
		
		if(targetZoom < currentPage.getMinZoom(width, height, isLandscape))
		{	
			if(!zoomDownUnlocked)
				targetZoom = minZoom;
		}
		else if(targetZoom > currentPage.getMaxZoom(width, height, isLandscape))
			targetZoom = maxZoom;
		
		float f_factor = targetZoom / currentPage.zoom;
		
		currentPage.x = x - (x - currentPage.x) * f_factor;
		currentPage.y = y - (y - currentPage.y) * f_factor;
		
		currentPage.zoom = targetZoom;
		
		if(!zoomDownUnlocked || currentPage.zoom > currentPage.getMinZoom(width, height, isLandscape))
			fixToBounds();
	}
	
	public void doZoom(float factor, int x, int y)
	{
		// Calculate zoom
		float targetZoom = currentPage.zoom * factor;
		doAbsZoom(targetZoom, x, y);
	}
	
	public void fixToBounds()
	{
		// Fix to page bounds
		if(currentPage.getWidth() < width)
			currentPage.x = (width - currentPage.getWidth()) / 2f;
		else if(currentPage.x > 0)
			currentPage.x = 0;
		else if(currentPage.x + currentPage.getWidth() < width)
			currentPage.x = width - currentPage.getWidth();
		
		if(currentPage.getHeight() < height)
			currentPage.y = (height - currentPage.getHeight()) / 2f;
		else if(currentPage.y > 0)
			currentPage.y = 0;
		else if(currentPage.y + currentPage.getHeight() < height)
			currentPage.y = height - currentPage.getHeight();
	}
	
	public synchronized void onSingleTap(int x, int y)
	{
		// Check if a link was in the way, if it was - go away
		if(currentPage.onSingleTap(x, y))
			return;
		
		boolean s = false;
		
		// If it wasn't, check if the page width is less than screen width and the tap was at the edges
		if(currentPage.getWidth() <= width + 5)
		// If it was, switch pages
		{
			if(x <= 0.1 * width)
			{
				if(goLeft())
					s = true;
			}
			else if (x >= 0.9 * width)
			{
				if(goRight())
					s = true;
			}
		}
		
		// If it wasn't the case, show or hide interface
		if(!s && delegate != null)
			delegate.onReactInterface();
	}

	public static int dpToPx(int dp)
	{
		return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
	}
	
	public synchronized void onDoubleTap(int x, int y)
	{
		if(dragActive || animationActive)
			return;
		
		float tx = 0;
		float ty = 0;
		float targetZoom = 1;
		float factor = 1;
		
		final float startZoom = currentPage.zoom;
		final float startX = currentPage.x;
		final float startY = currentPage.y;

		targetZoom = currentPage.getMinZoom(width, height, isLandscape);
		if(currentPage.zoom < 1.05 * targetZoom)
			targetZoom = 2 * targetZoom;

		if(targetZoom > currentPage.getMaxZoom(width, height, isLandscape))
			targetZoom = currentPage.getMaxZoom(width, height, isLandscape);

		factor = targetZoom / currentPage.zoom;

		tx = x - (x - currentPage.x) * factor;
		ty = y - (y - currentPage.y) * factor;
		
		// Fix to page bounds
		float fixWidth = currentPage.getWidth() * factor;
		float fixHeight = currentPage.getHeight() * factor;
		
		if(fixWidth < width)
			tx = (width - fixWidth) / 2f;
		else if(tx > 0)
			tx = 0;
		else if(tx + fixWidth < width)
			tx = width - fixWidth;
		
		if(fixHeight < height)
			ty = (height - fixHeight) / 2f;
		else if(ty > 0)
			ty = 0;
		else if(ty + fixHeight < height)
			ty = height - fixHeight;
		
		final float endZoom = targetZoom;
		final float endX = tx;
		final float endY = ty;
			
		animationActive = true;
		
		ValueAnimator v = ObjectAnimator.ofFloat(this, "val", 0, 1);
		v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float interpolatedTime = animation.getAnimatedFraction();
	        	currentPage.zoom = startZoom + (endZoom - startZoom) * interpolatedTime;
	        	currentPage.x = startX + (endX - startX) * interpolatedTime;
	        	currentPage.y = startY + (endY - startY) * interpolatedTime;
	        	
	        	if(self.drawDelegate != null)
	        		self.drawDelegate.redrawPages();
			}
		});
		v.addListener(new EndAnimatorListener(){
			@Override
			public void onAnimationEnd(Animator animation) {
	        	resetPage(leftPage);
	        	resetPage(rightPage);
	        	repositionPages();
            	
            	if(self.drawDelegate != null)
	        		self.drawDelegate.redrawPages();	        	
            	animationActive = false;           
            	
            	checkSchedules();
            	
            	updatePositionDelegate();
			}
		});
		
		v.setDuration(300);
	    v.setInterpolator(new AccelerateDecelerateInterpolator());
	    
	    v.start();

//	    updatePositionDelegate();
        
	}
	
	public synchronized void onSwipe(int x, int y, boolean rightSwipe)
	{
		// Check if page was at the corresponding edge
		// If it was, switch pages
		
		// +/- 5 more for avoiding any bugs than anything else
		
		if(zoomActive || animationActive)
			return;
		
		else if(rightSwipe && currentPage.x + currentPage.getWidth() <= width + 5)
		{
			goRight();
		}
		else if(!rightSwipe && currentPage.x >= -5)
		{
			goLeft();
		}
		
    	if(self.drawDelegate != null)
    		self.drawDelegate.redrawPages();
	}
	
	// -------------------------------------------------- //
	// Additional motion methods                          //
	// -------------------------------------------------- //
	
	boolean toNewPage = false;
	
	public void stretchBack()
	{
		final float endX = getPageStretchback(currentPage);		
		
		animationActive = true;
		
		stretchTo(endX);
	}
	
	public void stretchTo(final float endX)
	{
		final float startX = currentPage.x;
		ValueAnimator v = ObjectAnimator.ofFloat(this, "val", 0, 1);
		v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
	        	currentPage.x = startX + (endX - startX) * animation.getAnimatedFraction();

				repositionPages();
	        	
	        	if(self.drawDelegate != null)
	        		self.drawDelegate.redrawPages();
			}
		});
		v.addListener(new EndAnimatorListener(){
			@Override
			public void onAnimationEnd(Animator animation) {
	        	if(self.drawDelegate != null)
	        		self.drawDelegate.redrawPages();	        	
            	animationActive = false;
            	
            	leftPage = getPage(rtl ? currentPageId + 1 : currentPageId - 1);
            	rightPage = getPage(rtl ? currentPageId - 1 : currentPageId + 1);
            	resetPage(leftPage);
            	resetPage(rightPage);
            	repositionPages();

            	if(delegate != null && toNewPage)
            		delegate.onPageChanged(currentPageId);
            	
            	toNewPage = false;
            	
            	if(drawDelegate != null)
        			drawDelegate.changePages(leftPage, currentPage, rightPage);
            	
            	checkSchedules();
            	
            	dragActive = false;
            	
            	updatePositionDelegate();
			}
		});
		
		v.setDuration(300);
	    if(Math.abs(endX - startX) < 3)
	    	v.setDuration(1);
	    v.setInterpolator(new DecelerateInterpolator());
	    
	    v.start();
	}
	
	int scheduledPage = -1;
	
	public synchronized void schedulePageChange(int pageId)
	{
		scheduledPage = pageId;
		
		if(!animationActive)
			checkSchedules();
	}
	
	public synchronized void checkSchedules()
	{
		if(scheduledPage >= 0)
		{
			cancelInertia();
			
			switchPages(scheduledPage);
			
			scheduledPage = -1;
		}
	}
	
	public boolean goLeft()
	{
		if(leftPage == null)
			return false;
		
		if(rtl)
			return switchPages(currentPageId + 1);
		else
			return switchPages(currentPageId - 1);
	}
	
	public boolean goRight()
	{
		if(rightPage == null)
			return false;
		
		if(rtl)
			return switchPages(currentPageId - 1);
		else
			return switchPages(currentPageId + 1);
	}
	
	public boolean switchPages(int pageId)
	{
		if(pageId == currentPageId)
			return false;
		
		AirPage page = getPage(pageId);
		
		if(page == null)
			return false;
		
		if((rtl && pageId > currentPageId) || (!rtl && pageId < currentPageId))
		{
			leftPage = page;
			resetPage(leftPage);

			repositionPages();
			
			rightPage = currentPage;
			currentPage = leftPage;

			leftPage = null;
		}
		else
		{
			rightPage = page;
			resetPage(rightPage);

			repositionPages();
			
			leftPage = currentPage;
			currentPage = rightPage;
			
			rightPage = null;
		}
		
		if(drawDelegate != null)
			drawDelegate.changePages(leftPage, currentPage, rightPage);
		
		if(delegate != null)
			delegate.onPageChangeStarted(currentPageId, pageId);
		
		currentPageId = pageId;
		
		repositionPages();
		
		toNewPage = true;
		
		stretchBack();
		
		return true;
	}
	
	public void cancelInertia()
	{
		if(inertiaActive)
			shouldCancelInertia = true;
	}

	@Override
	public void onPageInvalidated(AirPage page, int pageId) {
		if(drawDelegate != null)
			drawDelegate.redrawPages();
	}

	public void invalidate()
	{
		if(drawDelegate != null)
			drawDelegate.redrawPages();
	}
	
	public void updatePositionDelegate()
	{
		Log.v("Derp", "Update thing");
    	if(delegate != null)
			delegate.onPagePositionChanged(currentPage.zoom <= currentPage.getMinZoom(width, height, isLandscape) * 1.05f ? 0 : currentPage.zoom, currentPage.x, currentPage.y, -1);
	}

	public void onPageDimensionsChanged(int pageId) {
		
		if(pageId == currentPageId)
		{
			float minZoom = currentPage.getMinZoom(width, height, isLandscape);
			doAbsZoom(minZoom, currentPage.defaultWidth / 2, currentPage.defaultHeight / 2);
				
			fixToBounds();
		}
		
		if(pageId >= currentPageId - 1 || pageId <= currentPageId + 1)
		{
			resetPage(leftPage);
			resetPage(rightPage);
			repositionPages();
		
			updatePositionDelegate();
		
			if(self.drawDelegate != null)
				self.drawDelegate.redrawPages();
		}
	}

	float val;

	public void setVal(float val) {
		this.val = val;
	}

	public float getVal() {
		return val;
	}
}
