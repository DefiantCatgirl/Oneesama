package catgirl.oneesama.controller.legacy;

public class CacheQueueItem {
	public int page;
	public CacheState state;
	
	String cachePath;
	
//	boolean isLeftPage = true;
	
	public boolean onlyStamps = false;
	
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof CacheQueueItem)) return false;
	    CacheQueueItem that = (CacheQueueItem) other;
	    
	    return (this.page == that.page && this.state == that.state);
	    
	}
}
