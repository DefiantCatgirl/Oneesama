package catgirl.oneesama.controller.legacy;

import catgirl.oneesama.model.chapter.ui.UiPage;
import catgirl.oneesama.ui.activity.legacyreader.widgets.airviewer.AirPage;

/**
 * A "real" page - has its metadata and loads all of the required assets. Implied to be read-only for the model, but hey, that would require like ten more classes to properly guarantee.
 * @author Defiant Catgirl
 *
 */
public class BookPage {
	public UiPage data;
	public AirPage page;

	public CacheState cacheState = CacheState.NONE;
	public boolean isDownloading = false;
	
}
