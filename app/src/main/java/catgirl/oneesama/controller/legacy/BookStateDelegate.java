package catgirl.oneesama.controller.legacy;

import catgirl.oneesama.model.chapter.serializable.Page;

public interface BookStateDelegate {
	public void pageDownloaded(int id, boolean bookDownloaded, int pageId, boolean onlyProgress);
	public void completelyDownloaded(int id, boolean success);
}
