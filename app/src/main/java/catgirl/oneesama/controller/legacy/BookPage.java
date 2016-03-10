package catgirl.oneesama.controller.legacy;

import catgirl.oneesama2.data.model.chapter.ui.UiPage;
import catgirl.oneesama2.legacy.legacyreader.widgets.airviewer.AirPage;

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
