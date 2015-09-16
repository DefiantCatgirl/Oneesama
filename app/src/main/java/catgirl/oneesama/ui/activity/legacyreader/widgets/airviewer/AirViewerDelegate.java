package catgirl.oneesama.ui.activity.legacyreader.widgets.airviewer;

/**
 * A combined delegate and adapter class, because lazy.
 *
 */
public interface AirViewerDelegate {
	// Adapter methods
	
	public AirPage getPage(int pageId);
	public int getPageCount();
	
	// Delegate methods
	
	public void onPageChanged(int pageId);
	public void onPageChangeStarted(int fromPageId, int pageId);
	public void onReactInterface();
	
	public void onPagePositionChanged(float zoom, float x, float y, float wideratio);
	
	public void onShowThumbnails();
	
	public void onPageFrameChanged(int pageId, float zoom, float x1, float y1, float x2, float y2);
	
	// tentative: a method on zoom frame change, might be necessary for PDFs (ha ha pdf on android) and whatnot.
}
