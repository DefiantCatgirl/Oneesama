package catgirl.oneesama.activity.legacyreader.widgets.airviewer;

public interface AirPageDelegate {
	/**
	 * Called when page should be redrawn - e.g. link activity, bitmap changed, etc. 
	 * @param page - page that called this
	 * @param pageId - page id, possibly useless
	 */
	public void onPageInvalidated(AirPage page, int pageId); // the latter mostly for convenience
}
