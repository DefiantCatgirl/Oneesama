package catgirl.oneesama.data.controller.legacy.downloader;

public interface BookDownloaderDelegate {
	
	public void onDownloadComplete(int id, boolean fileExists);
	public void onDownloadFailed(int id, boolean internetError, boolean credentialsError);
	public void onDownloadProgress(int id, float progress);
	
}
