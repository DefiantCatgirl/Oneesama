package catgirl.oneesama.ui.activity.legacyreader.activityreader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.Bind;
import catgirl.oneesama.Application;
import catgirl.oneesama.R;
import catgirl.oneesama.controller.ChaptersController;
import catgirl.oneesama.controller.legacy.Book;
import catgirl.oneesama.controller.legacy.BookStateDelegate;
import catgirl.oneesama.controller.legacy.CacherDelegate;
import catgirl.oneesama.ui.activity.legacyreader.widgets.airviewer.AirCanvasDrawerDelegate;
import catgirl.oneesama.ui.activity.legacyreader.widgets.airviewer.AirPage;
import catgirl.oneesama.ui.activity.legacyreader.widgets.airviewer.AirViewer;
import catgirl.oneesama.ui.activity.legacyreader.widgets.airviewer.AirViewerDelegate;
import catgirl.oneesama.ui.activity.legacyreader.widgets.airviewer.AirViewerRecognizer;
import catgirl.oneesama.ui.activity.legacyreader.widgets.airviewer.AirWidgetDrawer;
import catgirl.oneesama.ui.activity.legacythumbnails.ReaderThumbnailsActivity;

public class ReaderActivity extends BaseActivity implements AirViewerDelegate, AirCanvasDrawerDelegate, CacherDelegate, BookStateDelegate
{
	public static final String PUBLICATION_ID = "publicationId";
    public static final String CURRENT_PAGE = "currentPage";
    private static final int THUMBNAILS_ACTIVITY = 101;

    @Bind(R.id.BookTitleLayout) Toolbar toolbar;
    @Bind(R.id.ReaderView) ViewGroup readerView;

    @Bind(R.id.airrender) AirWidgetDrawer airDrawer;
    @Bind(R.id.airtouch) AirViewerRecognizer airRecognizer;

    @Bind(R.id.ThumbnailsButton) ImageButton thumbButton;
    private static float cwideratio;
    private static float cx;
    private static float cy;
    private static float czoom;

	private AirViewer airView;
	
	private boolean isLandscape;
	
	private final ReaderActivity self = this;

	// Manages download progress bar and interface animation
	private InterfaceManager interfaceManager;

	public Book book;
	public static Book updateBook;
	public static int currentPage = 0;
	public static boolean retainZoom = true;
    
    public boolean isAnimating = false;
    
    boolean started = false;

    @SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v("PixelFormat", "format = " + getWindow().getAttributes().format + ", 565 = " + PixelFormat.RGB_565); 
		
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.format = PixelFormat.RGBA_8888;
		getWindow().setAttributes(lp);
		
		Log.v("PixelFormat", "format = " + getWindow().getAttributes().format + ", 565 = " + PixelFormat.RGB_565); 
		
		// Set working book controller

		if(savedInstanceState == null)
			init(getIntent().getExtras().getInt(PUBLICATION_ID, 0), -1);
	}

	public void init(int bookId, int savedCurrentPage) {
		book = ChaptersController.getInstance().getChapterController(bookId);

		if(book == null)
		{
			goBack();
			return;
		}

		if(savedCurrentPage < 0)
			currentPage = Application.getContextOfApplication().getSharedPreferences("savedpages", Context.MODE_PRIVATE).getInt(String.valueOf(book.data.getId()), 0);
		else
			currentPage = savedCurrentPage;

		if(currentPage >= book.data.getPages().size())
			currentPage = book.data.getPages().size() - 1;

		book.startDownload();
		book.setDelegate(this);
		book.addBookStateDelegate(this);
		if(updateBook != null)
			updateBook.addBookStateDelegate(this);

		book.cacheAround(currentPage, -1);

		setContentView(R.layout.activity_reader);
		ButterKnife.bind(this);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		toolbar.setNavigationOnClickListener(view -> onBackPressed());

		// Default params
		MiniBitmapCache.getInstance().mMemoryCache.evictAll();

		// Initialize widgets and resources

		interfaceManager = new InterfaceManager(this, book);

		airDrawer.useBackground = false;

		airView = new AirViewer(this, self, airDrawer, false, true, false);

		isLandscape = (readerView.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

		justStarted = true;

		airDrawer.setDelegate(this);

		retainZoom = true;

		// Setup interface

		interfaceManager.setupInterface();
		interfaceManager.updateLabel(Math.max(currentPage, 0));
		interfaceManager.showInterface();

		if(book.canReload)
		{
			//TODO Reload
			//View v = findViewById(R.id.Reader_ReloadButton);
			//v.setVisibility(View.VISIBLE);
		}

		thumbButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onThumbnailsPressed(v);
			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt(ReaderActivity.PUBLICATION_ID, book.data.getId());
		savedInstanceState.putInt(CURRENT_PAGE, currentPage);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		init(savedInstanceState.getInt(PUBLICATION_ID, 0), savedInstanceState.getInt(CURRENT_PAGE, -1));
	}

    public void onThumbnailsPressed(View view) {
		Intent intent = new Intent(this, ReaderThumbnailsActivity.class);
		intent.putExtra(PUBLICATION_ID, book.data.getId());
        intent.putExtra(CURRENT_PAGE, currentPage);
        startActivityForResult(intent, ReaderActivity.THUMBNAILS_ACTIVITY);
    }

	boolean justStarted = false;
	
	@Override
	public void onResume()
	{
		super.onResume();

		if(book == null)
		{
			goBack();
			return;
		}

		book.cacheAround(currentPage, -1);

		if(!justStarted)
		{	
			return;
		}
		
		justStarted = false;	
	}
	
    boolean quitting = false;
    
	@Override
	public void onBackPressed() {
		cleanup();
        super.onBackPressed();
	}

	void cleanup()
	{
		quitting = true;

		MiniBitmapCache.getInstance().mMemoryCache.evictAll();

        book.clearCache();
	}
	
	String parameter;

	void goBack()
	{
		cleanup();
		finish();
	}

	void restart()
	{
		book.clearCache();
		Intent setIntent = new Intent(self, ReaderActivity.class);
		setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(setIntent);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		finish();
		book.clearCache();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
	
    // Region: Book controller delegates //

	@Override
	public void onCacheUpdated() {
		if(airView != null && !quitting)
			airView.invalidate();
	}
	
	// Region: Book downloader delegates //
	
	@Override
	public void pageDownloaded(int id, final boolean bookDownloaded, final int pageId, boolean onlyProgress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(interfaceManager != null)
                {
                    interfaceManager.updateProgress();
                    if(bookDownloaded)
                    {
                        interfaceManager.hideDownloadbar();
                    }
                }
            }
        });
	}

	@Override
	public void completelyDownloaded(int id, boolean success) {}

	// Region: Air drawer delegates //

	@Override
	public void onCanvasInitialized() {
		if(!started)
		{
			airView.start(currentPage, false, 0, 0, 1, airDrawer.getDimensions().x > airDrawer.getDimensions().y, false);
			started = true;
		}
		else
			airView.start(currentPage, true, cx, cy, czoom, airDrawer.getDimensions().x > airDrawer.getDimensions().y, false);
		
		airRecognizer.setAirViewer(airView);
//		airView.invalidate();
	}

	// Region: Air viewer delegates //
	
	@Override
	public AirPage getPage(int pageId) {
		if(pageId >= 0 && pageId < book.bookPages.size())
			return book.bookPages.get(pageId).page;
		else
			return null;
	}

	@Override
	public int getPageCount() {
		return book.bookPages.size();
	}

	@Override
	public void onPageChanged(final int pageId) {
		book.cacheAround(pageId, -1);
		
		currentPage = pageId;

		Application.getContextOfApplication().getSharedPreferences("savedpages", Context.MODE_PRIVATE).edit().putInt(String.valueOf(book.data.getId()), pageId).commit();

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				int realPage = Math.max(0, currentPage);

				if (pageId == book.bookPages.size() - 1 && interfaceManager.interfaceLayout.getVisibility() != View.VISIBLE)
					interfaceManager.reactInterface();

				interfaceManager.updateLabel(realPage);

				airDrawer.invalidate();
			}
		});
	}

	@Override
	public void onPageChangeStarted(int fromPageId, int pageId) {
		if(Math.abs(fromPageId - pageId) > 1)
			book.cacheAround(pageId, fromPageId);
	}

	@Override
	public void onReactInterface() {
		interfaceManager.reactInterface();
	}

	public void showInterface() {
		if(interfaceManager.interfaceLayout.getVisibility() != View.VISIBLE)
			interfaceManager.reactInterface();
		interfaceManager.abortHideInterface();
	}

	// Make sure to retain parameters during screen rotation 
	@Override
	public void onPagePositionChanged(float zoom, float x, float y, float wideratio) {
		czoom = zoom;
		cx = x;
		cy = y;
		cwideratio = wideratio;
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case THUMBNAILS_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    Bundle res = data.getExtras();
                    int newId = res.getInt(CURRENT_PAGE);
                    switchPage(newId);
                }
                break;
        }
    }
	
	public void animateToPage(int page)
	{
		airView.schedulePageChange(page);
	}
	
	public void switchPage(int page)
	{
		airView.start(page, false, 0, 0, 1, airDrawer.getDimensions().x > airDrawer.getDimensions().y, false);
	}

	@Override
	public void onShowThumbnails() {
		this.onThumbnailsPressed(null);
	}

	@Override
	public void onPageDimensionsChanged(int pageId) {
		if(airView != null && airView.started)
			airView.onPageDimensionsChanged(pageId);
	}

	@Override
	public void onPageFrameChanged(int pageId, float zoom, float x1, float y1, float x2,
								   float y2) {

	}
}
