package catgirl.oneesama2.legacy.legacyreader.widgets.airviewer;

/**
 * The purpose of this delegate is to let the receiver know when all of the canvas parameters (like height and width)
 * have been determined and the canvas is ready for use.
 *
 */
public interface AirCanvasDrawerDelegate {
	public void onCanvasInitialized();
}
