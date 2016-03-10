package catgirl.oneesama.controller.legacy;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import catgirl.oneesama2.application.Application;
import catgirl.oneesama.controller.FileManager;
import catgirl.oneesama.controller.legacy.downloader.BookDownloaderDelegate;
import catgirl.oneesama.controller.legacy.downloader.Downloader;
import catgirl.oneesama2.data.model.chapter.ui.UiChapter;
import catgirl.oneesama2.data.model.chapter.ui.UiPage;
import catgirl.oneesama2.legacy.legacyreader.tools.ActivityUtils;
import catgirl.oneesama2.legacy.legacyreader.tools.ThreadPoolAsyncTask;
import catgirl.oneesama2.legacy.legacyreader.widgets.airviewer.AirPage;

public class Book implements BookDownloaderDelegate {

	public UiChapter data;

	public ArrayList<BookPage> bookPages;

	private ArrayList<CacheQueueItem> cacheQueue;
	private boolean cacherActive = false;

	public Book self = this;

	public WeakReference<CacherDelegate> delegate;
	public List<WeakReference<BookStateDelegate>> bsDelegates = new ArrayList<WeakReference<BookStateDelegate>>();

	public BitmapFactory.Options currentOptions;

	public boolean shouldDownload = false;
	public boolean isDownloading = false;

	public boolean completelyDownloaded = false;
	public int pagesDownloaded = 0;

	public Downloader downloader;

	int currentPage = 0;

	public boolean isUpdating = false;
	public boolean isUpdater = false;

	public Date modifiedSince;

	int virtualPageCount = 0;

	Set<Integer> downloadedFiles = new HashSet<>();
	Set<Integer> pagesBeingDownloaded = new HashSet<>();
	int MAXCONNECTIONS = 4;
	Map<Integer, Integer> downloadFailures = new HashMap<Integer, Integer>();
	Set<String> downloadedStamps = new HashSet<String>();

	public int totalFiles = 0;

	int MAXFAILURES = 3;

	public boolean canReload = false;

	public long mySize = 0;

	public boolean searchDownloaded = false;

	public Map<Integer, Integer> tocToPages = new HashMap<Integer, Integer>();

	public Book(UiChapter chapter, CacherDelegate delegate, BookStateDelegate bsDelegate, boolean isUpdater, Date modifiedSince)
	{
		this.data = chapter;

		addBookStateDelegate(bsDelegate);

		this.isUpdater = isUpdater;

		bookPages = new ArrayList<BookPage>();

		for(int i = 0; i < data.getPages().size(); i++)
		{
			UiPage d = data.getPages().get(i);

			AirPage p = new AirPage(i, 300, 400, false, false, Color.WHITE);
			p.bgcolor = Color.WHITE;
			p.isDownloading = true;

			BookPage pg = new BookPage();
			pg.data = d;
			pg.page = p;

			bookPages.add(pg);

			// This mystery piece of code tries to set proper page sizes for the photo album
			// Basically it should match the background image dimensions, not "book size" parameter
			// If you don't have the file yet - use the delegate when downloading
			fixPageForAlbum(i);
		}

		cacheQueue = new ArrayList<CacheQueueItem>();

		downloader = new Downloader(self, data.getId());
		downloader.continueOnFailure = false;

		this.modifiedSince = modifiedSince;

		this.delegate = new WeakReference<CacherDelegate>(delegate);

		// Calculate the amount of downloaded pages
		if(!isUpdater)
		{
			for(int i = 0; i < data.getPages().size(); i++)
			{
				if(FileManager.fileExists(data.getId(), data.getPages().get(i)))
				{
					pagesDownloaded++;
					bookPages.get(i).page.isDownloading = false;
				}
			}
		}

		if(getNextDownload() < 0)
			completelyDownloaded = true;

		totalFiles = bookPages.size();
	}

	private boolean fixPageForAlbum(int i) {
			AirPage page = bookPages.get(i).page;

			if(!FileManager.fileExists(data.getId(), data.getPages().get(i)))
				return false;

			InputStream istr = FileManager.getInputStream(data.getId(), data.getPages().get(i));

			if(istr == null)
				return false;

			self.currentOptions=new BitmapFactory.Options();
			self.currentOptions.inJustDecodeBounds = true;

			BitmapFactory.decodeStream(istr, null, self.currentOptions);

			int newWidth = self.currentOptions.outWidth;
			int newHeight = self.currentOptions.outHeight;

			float xFactor = 1;
			float yFactor = 1;

			float xStretch = (float) newWidth / (float) page.defaultWidth;
			float yStretch = (float) newHeight / (float) page.defaultHeight;

			if(xStretch > yStretch)
				yFactor = (float) (page.defaultHeight * xStretch) / (float) newHeight;
			else
				xFactor = (float) (page.defaultWidth * yStretch) / (float) newWidth;

			page.defaultWidth = self.currentOptions.outWidth;
			page.defaultHeight = self.currentOptions.outHeight;

			Log.v("Album", "" + page.defaultWidth + " " + page.defaultHeight);

			return true;
	}

	public void setDelegate(CacherDelegate delegate)
	{
		this.delegate = new WeakReference<CacherDelegate>(delegate);
	}

	public synchronized void addBookStateDelegate(BookStateDelegate delegate)
	{
		List<WeakReference<BookStateDelegate>> toDelete = new ArrayList<WeakReference<BookStateDelegate>>();
		for(WeakReference<BookStateDelegate> d : bsDelegates)
		{
			if(d.get() == null)
				toDelete.add(d);
		}
		for(WeakReference<BookStateDelegate> d : toDelete)
		{
			bsDelegates.remove(d);
		}
		boolean exists = false;
		for(WeakReference<BookStateDelegate> d : bsDelegates)
		{
			if(d.get() == delegate)
			{
				exists = true;
				break;
			}
		}
		if(!exists)
			bsDelegates.add(new WeakReference<BookStateDelegate>(delegate));
	}

	public synchronized void delegateOnPageDownloaded(int id, boolean bookDownloaded, int pageId, boolean onlyProgress)
	{
		for(WeakReference<BookStateDelegate> d : bsDelegates)
		{
			if(d.get() != null)
				d.get().pageDownloaded(id, bookDownloaded, pageId, onlyProgress);
		}
	}

	public synchronized void delegateOnCompletelyDownloaded(int id)
	{
		for(WeakReference<BookStateDelegate> d : bsDelegates)
		{
			if(d.get() != null)
				d.get().completelyDownloaded(id, completelyDownloaded);
		}
	}

	public void startDownload()
	{
		if(isDownloading)
			return;

		downloader.resume();

		isDownloading = true;

		canReload = false;
		downloadFailures = new HashMap<>();
		shouldDownload = true;

		if(isUpdater)
		{
			downloader.checkModify = true;
			downloader.modifyDate = modifiedSince;
		}

		for(int i = 0; i < MAXCONNECTIONS; i++)
		{
			int j = getNextDownload();

			if(j >= 0)
			{
				pagesBeingDownloaded.add(j);
				downloader.download(j, data.getPages().get(j));
			}
		}
	}

	public boolean shouldDownload(int pageToCheck) {
		return (!downloadedFiles.contains(pageToCheck) && (!downloadFailures.containsKey(pageToCheck) || downloadFailures.get(pageToCheck) < MAXFAILURES) && !FileManager.fileExists(data.getId(), data.getPages().get(pageToCheck)) && !pagesBeingDownloaded.contains(pageToCheck));
	}

	public int getNextDownload()
	{
		if(shouldDownload(currentPage))
			return currentPage;

		if(currentPage < bookPages.size() - 1)
		{
			if(shouldDownload(currentPage + 1))
				return currentPage + 1;
		}

		for(int i = 0; i < bookPages.size(); i++)
		{
			if(shouldDownload(i))
				return i;
		}

		return -1;
	}


	public synchronized void clearCache()
	{
		cacherActive = false;

		cacheQueue.clear();
		if(currentOptions != null)
			currentOptions.requestCancelDecode();
		for(BookPage p : bookPages)
		{
			if(p.page.pageBitmap != null)// && !p.page.pageBitmap.isRecycled())
			{
				p.page.lockBitmap();
//				p.page.pageBitmap.recycle();
				p.page.pageBitmap = null;
				p.page.unlockBitmap();
			}

			p.cacheState = CacheState.NONE;
		}
	}

	public void clearCachedPage(int vpage)
	{
		BookPage p = bookPages.get(vpage);
		AirPage page = null;

		page = p.page;
		p.cacheState = CacheState.NONE;

		if(page != null && page.pageBitmap != null)// && !page.pageBitmap.isRecycled())
		{
			page.lockBitmap();

			if(page.busy)
				page.shouldRecycle = true;
			else
			{
//				page.pageBitmap.recycle();
				page.pageBitmap = null;
			}

			page.unlockBitmap();
		}
	}

	public void cachePage(int vpage, CacheState state)
	{
		CacheQueueItem item = new CacheQueueItem();
		item.page = vpage;
		item.state = state;

		if(item.page >= 0 && item.page < bookPages.size())
		{
			cacheQueue.add(item);
		}
	}

	public void cacheAround(int page, int retain)
	{
		Log.v("RDR", "Caching around: " + Integer.toString(page));

		this.currentPage = Math.max(page, 0);

		CacheQueueItem current = cacheQueue.size() > 0 ? cacheQueue.get(0) : null;

		cacheQueue.clear();

		if(page < 0)
			page = 0;
		else if (page >= bookPages.size())
			page = bookPages.size() - 1;

		for(int i = 0; i <= Math.min(bookPages.size(), page - 1); i++)
		{
//			if(i == retain)
//				continue;

			clearCachedPage(i);
		}

		for(int i = Math.max(0, page + 1); i < bookPages.size(); i++)
		{
			if(i == retain)
				continue;

			clearCachedPage(i);
		}

		// Manually because the order of thumbnails needs some research on low-memory devices.

		for(int i = page; i <= page + 1; i++)
			cachePage(i, CacheState.LOW);

		for(int i = page - 1; i <= page - 1; i++)
			cachePage(i, CacheState.LOW);

//		int i = currentPage;
		for(int i = page; i <= page + 1; i++)
			cachePage(i, CacheState.HIGH);

//		for(int i = page - 0; i <= page; i++)
//			cachePage(i, CacheState.HIGH);

		if(!cacherActive && !cacheQueue.isEmpty())
		{
			cacherActive = true;
			startCacher(cacheQueue.get(0));
		}
		else if(cacherActive)
		{
			if(currentOptions != null && current != null && !cacheQueue.contains(current))
				currentOptions.requestCancelDecode();
		}
	}

	private ThreadPoolAsyncTask<CacheQueueItem, Void, Void> task;

	public void startCacher(CacheQueueItem item)
	{
		cacherActive = true;

		task = new ThreadPoolAsyncTask<CacheQueueItem, Void, Void>(){

			Drawable bitmap;
			CacheQueueItem item;

			@SuppressLint("NewApi")
			@Override
			protected Void doInBackground(CacheQueueItem... params) {

				Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

				this.item = params[0];
				if(this.item == null)
					return null;

				InputStream istr = null;

				if(!item.onlyStamps)
				{
					BookPage bookPage = bookPages.get(item.page);

					if(item.state == CacheState.HIGH && bookPage.cacheState == CacheState.HIGH)
						return null;

					if(item.state == CacheState.LOW && bookPage.cacheState != CacheState.NONE)
						return null;

					if(!FileManager.fileExists(data.getId(), data.getPages().get(item.page)))
						return null;

					istr = FileManager.getInputStream(data.getId(), data.getPages().get(item.page));

					if(istr == null)
					{
						Log.v("Cache", "Failed: " + FileManager.getPageFile(data.getId(), data.getPages().get(item.page)).toString());
						return null;
					}

					ActivityUtils.ByteArray a = ActivityUtils.byteArrayFromStream(istr);

					if(item.state == CacheState.LOW)
					{
						self.currentOptions=new BitmapFactory.Options();
						self.currentOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
						self.currentOptions.inJustDecodeBounds = true;

						BitmapFactory.decodeByteArray(a.array, 0, a.count, self.currentOptions);

						self.currentOptions.inSampleSize = ActivityUtils.calculateInSampleSizeMin(self.currentOptions, ActivityUtils.MAX_SIZE / 4, ActivityUtils.MAX_SIZE / 4);
						self.currentOptions.inJustDecodeBounds = false;

						try {
							bitmap = new BitmapDrawable(Application.getContextOfApplication().getResources(), BitmapFactory.decodeByteArray(a.array, 0, a.count, self.currentOptions));
						} catch (OutOfMemoryError e) {
							return null;
						}

						return null;
					}
					else
					{
						self.currentOptions=new BitmapFactory.Options();

						self.currentOptions.inSampleSize = 1;

						self.currentOptions=new BitmapFactory.Options();
						self.currentOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
						self.currentOptions.inJustDecodeBounds = false;


						try {
							if(android.os.Build.VERSION.SDK_INT >= 10)
								bitmap = ActivityUtils.createLargeDrawable(a);
							else
								bitmap = new BitmapDrawable(Application.getContextOfApplication().getResources(), BitmapFactory.decodeByteArray(a.array, 0, a.count, self.currentOptions));
						} catch (OutOfMemoryError E) {
							self.currentOptions.inSampleSize = 2;
							try {
								bitmap = new BitmapDrawable(Application.getContextOfApplication().getResources(), BitmapFactory.decodeByteArray(a.array, 0, a.count, self.currentOptions));
							} catch (OutOfMemoryError E1) {
								bitmap = null;
							}
							self.currentOptions.inSampleSize = 1;
						} catch (StreamCorruptedException e) {
							try
							{
								Bitmap b = BitmapFactory.decodeByteArray(a.array, 0, a.count, self.currentOptions);
								bitmap = ActivityUtils.createLargeDrawable(b);
							} catch (Exception e1) {
								bitmap = null;
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {

				self.pageDidFinishLoading(item, bitmap);
			}

			@Override
			protected void onPreExecute() {
			}


		};

//		task.execute(item);
		task.asyncExecute(item);
	}

	public void noMemory()
	{
		cacherActive = false;
	}


	// TODO: make sure to mutex the code, to hell with it right now, pain to debug
	public synchronized void pageDidFinishLoading(CacheQueueItem item, Drawable bitmap)
	{
		if(bitmap != null)
		{
			if(cacheQueue.contains(item))
			{
				BookPage bookPage = bookPages.get(item.page);
				AirPage p = bookPages.get(item.page).page;

				if(p.pageBitmap != null)
				{
					if(p.pageBitmap != null)// && !p.pageBitmap.isRecycled())
					{
						p.lockBitmap();
						if(p.busy)
						{
							p.shouldRecycle = true;
						}
						else
						{
							p.pageBitmap = null;
						}
						p.unlockBitmap();
					}
				}
				p.pageBitmap = bitmap;
				bookPage.cacheState = item.state;

				if(delegate.get() != null)
					delegate.get().onCacheUpdated();
			}
			else
			{
//					bitmap.recycle();
			}
		}

		if(cacheQueue.contains(item))
			cacheQueue.remove(item);

		if(!cacheQueue.isEmpty())
		{
//			task.cancel(true);
			startCacher(cacheQueue.get(0));
		}
		else
			cacherActive = false;

	}

	ArrayList<Integer> fakes = new ArrayList<>();

	@Override
	public synchronized void onDownloadComplete(int id, boolean fileExists) {
		if(fileExists)
		{
			fakes.add(id);
		}

		downloadedFiles.add(id);

		pagesBeingDownloaded.remove(id);
		downloadFailures.remove(id);

		int r = getNextDownload();

		if(r >= 0)
		{
			if(isDownloading)
			{
				pagesBeingDownloaded.add(r);
				downloader.download(r, data.getPages().get(r));
			}
		}
		else if(downloadFailures.isEmpty() && pagesBeingDownloaded.isEmpty())
		{
			completelyDownloaded = true;
			isDownloading = false;
			delegateOnCompletelyDownloaded(data.getId());
		}
		else if(pagesBeingDownloaded.isEmpty())
		{
			canReload = true;
			isDownloading = false;
			delegateOnCompletelyDownloaded(data.getId());
		}

		int n = id;

		pagesDownloaded++;

		bookPages.get(id).page.isDownloading = false;
		fixPageForAlbum(id);

		delegateOnPageDownloaded(data.getId(), completelyDownloaded, id, false);

		if(n >= currentPage - 1 && n <= currentPage + 1)
		{
			cacheDownloaded(n);
		}

		if(!cacherActive && !cacheQueue.isEmpty())
		{
			cacherActive = true;
			startCacher(cacheQueue.get(0));
		}

		if(delegate.get() != null)
			delegate.get().onPageDimensionsChanged(id);
	}

	public void cacheDownloaded(int vpage)
	{
		CacheQueueItem q = new CacheQueueItem();
		q.page = vpage;
		q.state = CacheState.HIGH;

		cacheQueue.add(q);
	}

	@Override
	public synchronized void onDownloadFailed(int id, boolean i, boolean p) {
//		pages.get(rd.pageId).isDownloading = false;
		if(!downloadFailures.containsKey(id))
			downloadFailures.put(id, 1);
		else
			downloadFailures.put(id, downloadFailures.get(id) + 1);
		pagesBeingDownloaded.remove(id);

		if(!downloadFailures.containsKey(id) || downloadFailures.get(id) < MAXFAILURES)
		{
			if(isDownloading)
			{
				pagesBeingDownloaded.add(id);
				downloader.download(id, data.getPages().get(id));
			}
		}
		else
		{
			int r = getNextDownload();

			if(r >= 0)
			{
				if(isDownloading)
				{
					pagesBeingDownloaded.add(r);
					downloader.download(r, data.getPages().get(r));
				}
			}
			else if(pagesBeingDownloaded.isEmpty())
			{
				canReload = true;
				isDownloading = false;
				delegateOnCompletelyDownloaded(data.getId());
			}
		}
	}

	@Override
	public void onDownloadProgress(int rd, float progress) {

			BookPage p = bookPages.get(rd);

			p.page.progress = progress;

			if(delegate.get() != null)
				delegate.get().onCacheUpdated();
	}

	public String getContentsPageName (int pageId) {
		String name = data.getPages().get(pageId).getName();
		if(name != null && !name.isEmpty())
			return name;
		else
			return String.valueOf(pageId);
	}

	public String getContentsPageCount() {
		return getContentsPageName(data.getPages().size() - 1);
	}

	public void cancelDownload() {
		downloader.cancel();
		isDownloading = false;
	}
}
