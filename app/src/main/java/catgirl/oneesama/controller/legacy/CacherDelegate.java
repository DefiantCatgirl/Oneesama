package catgirl.oneesama.controller.legacy;

public interface CacherDelegate {
	public void onCacheUpdated();
	public void onPageDimensionsChanged(int pageId);
}
