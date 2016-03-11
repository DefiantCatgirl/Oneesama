package catgirl.oneesama.data.controller.legacy;

public interface BookStateDelegate {
	public void pageDownloaded(int id, boolean bookDownloaded, int pageId, boolean onlyProgress);
	public void completelyDownloaded(int id, boolean success);
}
