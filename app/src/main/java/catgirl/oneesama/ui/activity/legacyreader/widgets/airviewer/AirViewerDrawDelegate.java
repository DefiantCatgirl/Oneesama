package catgirl.oneesama.ui.activity.legacyreader.widgets.airviewer;

import android.graphics.Point;

public interface AirViewerDrawDelegate {
	
	public void changePages(AirPage left, AirPage current, AirPage right);
	public void redrawPages();
	
	public Point getDimensions();
	
}
