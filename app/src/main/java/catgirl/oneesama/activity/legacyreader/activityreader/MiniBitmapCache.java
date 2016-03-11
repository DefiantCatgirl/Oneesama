package catgirl.oneesama.activity.legacyreader.activityreader;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class MiniBitmapCache {
	private static MiniBitmapCache _cache;
	
	public LruCache<Integer, Bitmap> mMemoryCache;
	
	public static MiniBitmapCache getInstance()
	{
		if(_cache == null)
			_cache = new MiniBitmapCache();
		return _cache;
	}
	
	private MiniBitmapCache()
	{
	    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 128);

	    // Use 1/16th of the available memory for this memory cache.
	    final int cacheSize = maxMemory / 16;

	    mMemoryCache = new LruCache<Integer, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(Integer key, Bitmap bitmap) {
	            // The cache size will be measured in kilobytes rather than
	            // number of items.
	            return (bitmap.getRowBytes() * bitmap.getHeight()) / 1024;
	        }
	    };
	}
	
	public void addBitmapToMemoryCache(Integer key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null) {
	        mMemoryCache.put(key, bitmap);
	    }
	}

	public Bitmap getBitmapFromMemCache(Integer key) {
	    return mMemoryCache.get(key);
	}
	
	
}
