package catgirl.oneesama.controller.legacy.downloader;

import catgirl.oneesama.model.chapter.serializable.Page;

public interface BookDownloaderDelegate {
	
	public void onDownloadComplete(int id, boolean fileExists);
	public void onDownloadFailed(int id, boolean internetError, boolean credentialsError);
	public void onDownloadProgress(int id, float progress);
	
}
