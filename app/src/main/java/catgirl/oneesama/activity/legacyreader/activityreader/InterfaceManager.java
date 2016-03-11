package catgirl.oneesama.activity.legacyreader.activityreader;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

import catgirl.oneesama.R;
import catgirl.oneesama.data.controller.legacy.Book;
import catgirl.oneesama.activity.legacyreader.tools.ActivityUtils;

public class InterfaceManager {

	Book book;
	ReaderActivity activity;
	
	boolean interfaceAnimating = false;
	
	ViewGroup interfaceLayout;
	ViewGroup interfaceTopBar;

	float mdx = 0;
	float scale = 1;
	
	private TextView pageLabel;
	TextView bookTitle;
	
	public InterfaceManager self = this;
	
	public InterfaceManager(ReaderActivity activity, Book book) {
		this.book = book;
		this.activity = activity;
	}
	
	public void setupInterface()
	{
		pageLabel = (TextView) activity.findViewById(R.id.pageLabel);
		
		bookTitle = (TextView) activity.findViewById(R.id.bookTitle);
		bookTitle.setText((activity.updateBook != null ? "Updating: " : "") + book.data.getTitle());
		
		interfaceLayout = (ViewGroup) activity.findViewById(R.id.InterfaceLayout);
		interfaceLayout.clearAnimation();
		interfaceLayout.setVisibility(View.GONE);
		interfaceTopBar = (ViewGroup) activity.findViewById(R.id.InterfaceTopBar);
		interfaceTopBar.clearAnimation();
		interfaceTopBar.setVisibility(View.GONE);
		ActivityUtils.enableDisableViewGroup(interfaceLayout, false);

		activity.findViewById(R.id.BookTitleLayout).bringToFront();
		
		updateProgress();
		
		h.postDelayed(r, 10000);
	}
	
	public void showInterface()
	{
			interfaceLayout.clearAnimation();
			interfaceLayout.setVisibility(View.VISIBLE);
			interfaceTopBar.setVisibility(View.VISIBLE);
			ActivityUtils.enableDisableViewGroup(interfaceLayout, true);
			
			activity.findViewById(R.id.InterfaceTopBar).clearAnimation();
			activity.findViewById(R.id.InterfaceTopBar).setVisibility(View.VISIBLE);
			activity.findViewById(R.id.InterfaceBottomBar).clearAnimation();
			activity.findViewById(R.id.InterfaceBottomBar).setVisibility(View.VISIBLE);
			
			activity.findViewById(R.id.DownloadProgressLayout).clearAnimation();
    		if(book.completelyDownloaded && !(activity.updateBook != null && !activity.updateBook.completelyDownloaded))
    			activity.findViewById(R.id.DownloadProgressLayout).setVisibility(View.GONE);
    		else
	    		activity.findViewById(R.id.DownloadProgressLayout).setVisibility(View.VISIBLE);
		    
    		activity.findViewById(R.id.BookTitleLayout).bringToFront();
//    		MApplication.interfaceActive = true;
	}
	
	private void hideInterfaceAnimation()
	{
		Animation fadeOutAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f);
//		fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
	    fadeOutAnimation.setDuration(200);
	    fadeOutAnimation.setAnimationListener(new AnimationListener() {
	    	@Override
	    	public void onAnimationEnd(Animation animation) {
	    		activity.findViewById(R.id.InterfaceTopBar).clearAnimation();
	    		activity.findViewById(R.id.InterfaceTopBar).setVisibility(View.GONE);
	    		
	    		Animation slideBottom = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
	    		slideBottom.setDuration(200);
	    		slideBottom.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationEnd(Animation animation) {
						interfaceLayout.clearAnimation();
			    		interfaceLayout.setVisibility(View.GONE);
						interfaceTopBar.setVisibility(View.GONE);
			    		ActivityUtils.enableDisableViewGroup(interfaceLayout, false);
			    		interfaceAnimating = false;
					}
					@Override
					public void onAnimationRepeat(Animation animation) {}
					@Override
					public void onAnimationStart(Animation animation) {}});
	    		activity.findViewById(R.id.InterfaceBottomBar).startAnimation(slideBottom);
	    		
	    	}
	    	@Override
	    	public void onAnimationRepeat(Animation animation) {}
	    	@Override
	    	public void onAnimationStart(Animation animation) {}
	    });

	    activity.findViewById(R.id.InterfaceTopBar).startAnimation(fadeOutAnimation);
	}
	
	Handler h = new Handler();
	Runnable r = new Runnable(){

		@Override
		public void run() {
			self.reactInterface();
		}
		
	};
	
	public void reactInterface()
	{
		if(interfaceAnimating)
			return;
		
		h.removeCallbacks(r);
		
		interfaceAnimating = true;
		
//		MApplication.interfaceActive = !MApplication.interfaceActive;
		
		if(interfaceLayout.getVisibility() == View.VISIBLE)
		{
			if(book.completelyDownloaded && !(activity.updateBook != null && !activity.updateBook.completelyDownloaded))
				hideInterfaceAnimation();
			else
			{
				activity.findViewById(R.id.BookTitleLayout).bringToFront();
				interfaceAnimating = true;
				Animation dlout = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1.25f);
				dlout.setDuration(200);
				dlout.setAnimationListener(new AnimationListener() {
			    	@Override
			    	public void onAnimationEnd(Animation animation) {
			    		activity.findViewById(R.id.DownloadProgressLayout).clearAnimation();
			    		activity.findViewById(R.id.DownloadProgressLayout).setVisibility(View.GONE);
			    		hideInterfaceAnimation();
			    	}
			    	@Override
			    	public void onAnimationRepeat(Animation animation) {}
			    	@Override
			    	public void onAnimationStart(Animation animation) {}
			    });
				activity.findViewById(R.id.DownloadProgressLayout).startAnimation(dlout);
			}
		}
		else
		{
			interfaceLayout.clearAnimation();
			interfaceLayout.setVisibility(View.VISIBLE);
			interfaceTopBar.setVisibility(View.VISIBLE);
			activity.findViewById(R.id.InterfaceTopBar).clearAnimation();
			activity.findViewById(R.id.InterfaceTopBar).setVisibility(View.GONE);
			ActivityUtils.enableDisableViewGroup(interfaceLayout, true);
//			pageSeekBar.requestLayout();
			
    		if(book.completelyDownloaded && !(activity.updateBook != null && !activity.updateBook.completelyDownloaded))
    		{
    			activity.findViewById(R.id.DownloadProgressLayout).clearAnimation();
    			activity.findViewById(R.id.DownloadProgressLayout).setVisibility(View.GONE);
    		}
			
			Animation fadeInAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
//			fadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
			fadeInAnimation.setDuration(200);
			fadeInAnimation.setAnimationListener(new AnimationListener() {
		    	@Override
		    	public void onAnimationEnd(Animation animation) {
		    		activity.findViewById(R.id.InterfaceTopBar).clearAnimation();
		    		activity.findViewById(R.id.DownloadProgressLayout).clearAnimation();
		    		activity.findViewById(R.id.InterfaceTopBar).setVisibility(View.VISIBLE);
		    		activity.findViewById(R.id.DownloadProgressLayout).setVisibility(View.GONE);
//		    		interfaceAnimating = false;
		    		
		    		Animation slideBottom = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f);
		    		slideBottom.setDuration(200);
		    		slideBottom.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationEnd(Animation animation) {
				    		if(book.completelyDownloaded && !(activity.updateBook != null && !activity.updateBook.completelyDownloaded))
				    		{
				    			interfaceAnimating = false;
				    		}
				    		else
				    		{
				    			activity.findViewById(R.id.BookTitleLayout).bringToFront();
				    			activity.findViewById(R.id.DownloadProgressLayout).clearAnimation();
					    		activity.findViewById(R.id.DownloadProgressLayout).setVisibility(View.VISIBLE);
								Animation dlout = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1.25f, Animation.RELATIVE_TO_SELF, 0f);
								dlout.setDuration(200);
								dlout.setAnimationListener(new AnimationListener() {
							    	@Override
							    	public void onAnimationEnd(Animation animation) {
							    		interfaceAnimating = false;
							    	}
							    	@Override
							    	public void onAnimationRepeat(Animation animation) {}
							    	@Override
							    	public void onAnimationStart(Animation animation) {}
							    });
								activity.findViewById(R.id.DownloadProgressLayout).startAnimation(dlout);
				    		}
						}
						@Override
						public void onAnimationRepeat(Animation animation) {}
						@Override
						public void onAnimationStart(Animation animation) {}});
		    		activity.findViewById(R.id.InterfaceTopBar).startAnimation(slideBottom);
		    	}
		    	@Override
		    	public void onAnimationRepeat(Animation animation) {}
		    	@Override
		    	public void onAnimationStart(Animation animation) {}
		    });
	
			activity.findViewById(R.id.InterfaceBottomBar).startAnimation(fadeInAnimation);
		}

	}

	public void hideDownloadbar()
	{
		activity.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				if(activity.findViewById(R.id.DownloadProgressLayout).getVisibility() == View.VISIBLE && !interfaceAnimating)
				{
					activity.findViewById(R.id.BookTitleLayout).bringToFront();
					interfaceAnimating = true;
					Animation dlout = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1.25f);
					dlout.setDuration(200);
					dlout.setAnimationListener(new AnimationListener() {
				    	@Override
				    	public void onAnimationEnd(Animation animation) {
				    		activity.findViewById(R.id.DownloadProgressLayout).clearAnimation();
				    		activity.findViewById(R.id.DownloadProgressLayout).setVisibility(View.GONE);
				    		interfaceAnimating = false;
				    	}
				    	@Override
				    	public void onAnimationRepeat(Animation animation) {}
				    	@Override
				    	public void onAnimationStart(Animation animation) {}
				    });
					activity.findViewById(R.id.DownloadProgressLayout).startAnimation(dlout);
				}
			}
		});
	}
	
	public void updateProgress()
	{
		ProgressBar progress = (ProgressBar) activity.findViewById(R.id.downloadProgress);
		if(progress != null)
		{
			if(activity.updateBook != null)
			{
				progress.setMax(activity.updateBook.totalFiles);
				progress.setProgress(activity.updateBook.pagesDownloaded);
				Log.v("FixUpdate", "Progress update");
			}
			else
			{
				progress.setMax(book.totalFiles);
				progress.setProgress(book.pagesDownloaded);
				Log.v("FixUpdate", "Progress normal");
			}
		}
		Log.v("FixUpdate", "Progress bar: " + progress.getProgress() + "/" + progress.getMax());
	}
	
	public void updateLabel(int pageId)
	{
		if(pageLabel != null)
		{
			pageLabel.setText(activity.getResources().getString(R.string.PAGE_TITLE) + " " + book.getContentsPageName(pageId) + " [ " + (pageId + 1) + " / " + book.bookPages.size() + " ]");
		}
	}

	public void showDownloadBar() {
		if(activity.findViewById(R.id.DownloadProgressLayout).getVisibility() != View.VISIBLE && !interfaceAnimating)
		{
			activity.findViewById(R.id.BookTitleLayout).bringToFront();
			interfaceAnimating = true;
			Animation dlin = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1.25f, Animation.RELATIVE_TO_SELF, 0f);
			dlin.setDuration(200);
			dlin.setAnimationListener(new AnimationListener() {
		    	@Override
		    	public void onAnimationEnd(Animation animation) {
		    		activity.findViewById(R.id.DownloadProgressLayout).clearAnimation();
		    		interfaceAnimating = false;
		    	}
		    	@Override
		    	public void onAnimationRepeat(Animation animation) {}
		    	@Override
		    	public void onAnimationStart(Animation animation) {}
		    });

    		activity.findViewById(R.id.DownloadProgressLayout).setVisibility(View.VISIBLE);
			activity.findViewById(R.id.DownloadProgressLayout).startAnimation(dlin);
		}
	}

	public void startUpdate() {
		activity.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				if(interfaceLayout.getVisibility() != View.VISIBLE)
					reactInterface();
				h.removeCallbacks(r);
				
				bookTitle.setText("Updating: " + book.data.getTitle());
				showDownloadBar();
				updateProgress();
			}
		});
		
	}

	public void endUpdate() {
		activity.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				bookTitle.setText(book.data.getTitle());
			}
		});
	}

	public void updateName() {
		bookTitle.setText(book.data.getTitle());
		bookTitle.invalidate();
	}

	public void abortHideInterface() {
		h.removeCallbacks(r);
	}
}
