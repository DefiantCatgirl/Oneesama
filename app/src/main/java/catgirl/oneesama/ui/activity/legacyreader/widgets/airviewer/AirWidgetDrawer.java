package catgirl.oneesama.ui.activity.legacyreader.widgets.airviewer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import catgirl.oneesama.R;
import catgirl.oneesama.ui.activity.legacyreader.tools.ActivityUtils;


public class AirWidgetDrawer extends FrameLayout implements AirViewerDrawDelegate {

	public final AirWidgetDrawer self = this;
	
	boolean justStarted = true;
	
	int width = -1, height = -1;
	AirCanvasDrawerDelegate delegate;
	
	public AirPage left;

	public AirPage current;

	public AirPage right;
	
	public boolean useBackground = true;
	
	// Images
	ImageView li, ci, ri;
	LinearLayout dli, dci, dri;
	ImageView bli, bci, bri;
	
	Context context;
	
	Drawable none;

	ImageView[] views	= new ImageView[3];
	LinearLayout[] dls = new LinearLayout[3];
	ImageView[] bgs		= new ImageView[3];

	Handler handler = new Handler();
	
	public AirWidgetDrawer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	public AirWidgetDrawer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public AirWidgetDrawer(Context context) {
		super(context );
		init(context);
	}
	
	public void init(Context context)
	{
		if(this.isInEditMode())
			return;
		
		self.context = context;
		Bitmap t =  Bitmap.createBitmap(1, 1, Config.RGB_565);
		t.setPixel(0, 0, Color.rgb(200, 200, 200));
		none = new BitmapDrawable(getContext().getResources(), t);
	}
	
	public void setDelegate(AirCanvasDrawerDelegate delegate)
	{
		self.delegate = delegate;
		if(width > 0 && height > 0)
			delegate.onCanvasInitialized();
	}
	
    @SuppressLint("NewApi")
	@Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
            super.onSizeChanged(xNew, yNew, xOld, yOld);
            
    		if(this.isInEditMode())
    			return;
            
            if(xNew <= 0 || yNew <= 0)
            {
            	Log.e("Air", "Size change failed");
            	return;
            }
            
            this.removeAllViews();
            
            width = xNew;
            height = yNew;
            
            LayoutParams p = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            
            bli = new ImageView(context);
            bli.setScaleType(ScaleType.MATRIX);
            bli.setLayoutParams(p);
            
            bci = new ImageView(context);
            bci.setScaleType(ScaleType.MATRIX);
            bci.setLayoutParams(p);
            
            bri = new ImageView(context);
            bri.setScaleType(ScaleType.MATRIX);
            bri.setLayoutParams(p);
            
            self.addView(bli);
            self.addView(bri);
            self.addView(bci);
            
            li = new ImageView(context);
            li.setScaleType(ScaleType.MATRIX);
            li.setLayoutParams(p);
            
            ci = new ImageView(context);
            ci.setScaleType(ScaleType.MATRIX);
            ci.setLayoutParams(p);
            
            ri = new ImageView(context);
            ri.setScaleType(ScaleType.MATRIX);
            ri.setLayoutParams(p);
            
            self.addView(li);
            self.addView(ri);
            self.addView(ci);

            dli = newDownloadingLayout();
            dci = newDownloadingLayout();
			dri = newDownloadingLayout();

		self.addView(dli);
            self.addView(dri);
            self.addView(dci);
            
    		views[0]   = ci; 		views[1]  = li; 		views[2]  = ri;
    		dls[0]     = dci;		dls[1]    = dli;		dls[2]    = dri;
    		bgs[0]     = bci;		bgs[1]    = bli;		bgs[2]    = bri;
    		
            if(delegate != null)
            	delegate.onCanvasInitialized();
            
            self.post(new Runnable(){

				@Override
				public void run() {
					self.requestLayout();
				}
            	
            });
    }

	public TextView newDownloadingTextview() {
		TextView downloading = new TextView(context);
		downloading.setText(R.string.DOWNLOADING_TITLE);
		downloading.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		downloading.setShadowLayer(1, 0, 1, Color.BLACK);
		downloading.setTextColor(Color.WHITE);
		downloading.setPadding(0, ActivityUtils.dpToPx(5), 0, 0);
		return downloading;
	}

	public LinearLayout newDownloadingLayout() {
		LayoutParams w = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		TextView downloading = newDownloadingTextview();
		ProgressWheel cloud = new ProgressWheel(context);
		cloud.spin();
		LinearLayout dri = new LinearLayout(context);
		dri.setOrientation(LinearLayout.VERTICAL);
		dri.setGravity(Gravity.CENTER);
		dri.setLayoutParams(w);
		dri.setVisibility(View.GONE);
		dri.addView(cloud);
		dri.addView(downloading);
		return dri;
	}

	@Override
	public synchronized void changePages(final AirPage left, final AirPage current, final AirPage right) {
		// TODO Auto-generated method stub
		if (current == null)
			return;
		
		handler.post(new Runnable() {

			@Override
			public void run() {

				if(self.left != null)
				self.left.lockBitmap();
			if(self.right != null)
				self.right.lockBitmap();
			if(self.current != null)
				self.current.lockBitmap();

			if(left != null && left != self.left && left != self.right && left != self.current)
				left.lockBitmap();
			if(right != null && right != self.left && right != self.right && right != self.current)
				right.lockBitmap();
			if(current != null && current != self.left && current != self.right && current != self.current)
				current.lockBitmap();

			AirPage[] olds			= new AirPage[3];
			AirPage[] news 			= new AirPage[3];

			olds[0] = self.current;	olds[1] = self.left; 	olds[2] = self.right;
			news[0] = current; 		news[1] = left; 		news[2] = right;

			for(int i = 0; i < olds.length; i++)
			{
				AirPage op = olds[i];
				AirPage np = news[i];
				ImageView vi = views[i];
				ImageView bg = bgs[i];

				if(np == null)
				{
					bg.setImageBitmap(null);
					bg.clearAnimation();
					bg.setVisibility(View.GONE);
				}

				if(np != null)
				{
					// Set background color
					Bitmap bgb = Bitmap.createBitmap(1, 1, Config.RGB_565);
					bgb.setPixel(0, 0, np.bgcolor);
					bg.setImageBitmap(bgb);
					bg.clearAnimation();
					if(useBackground)
						bg.setVisibility(View.VISIBLE);
					else
						bg.setVisibility(View.INVISIBLE);

//						try {
						// If the ImageView had a bitmap...
						Drawable bitmap = null;
						if(vi.getDrawable() != null)
							bitmap = (vi.getDrawable());//.getBitmap();
						// And it's not the same bitmap as the one in the corresponding AirPage...
						if(np.pageBitmap != null && bitmap != np.pageBitmap)// && !np.pageBitmap.isRecycled())
						{
							// Set the AirPage bitmap
							vi.setImageDrawable(np.pageBitmap);

							// And kill the former one in fire if the controller doesn't need it.
							if(op != null && op.shouldRecycle && bitmap != null && bitmap != none) //&& !bitmap.isRecycled()
								;
//						    		bitmap.recycle();
							if(op != null)
								op.shouldRecycle = false;
						}
						if(np.pageBitmap == null)
							vi.setImageDrawable(none);
						// Set the page to the "nope, we're not using it in the viewer" state.
						if(op != null)
							op.busy = false;
				}
				else
				{
					vi.setImageBitmap(null);
				}

				if(np != null)
					np.busy = true;
			}

			if(self.left != null)
				self.left.unlockBitmap();
			if(self.right != null)
				self.right.unlockBitmap();
			if(self.current != null)
				self.current.unlockBitmap();

			if(current != null)
				current.unlockBitmap();
			if(left != null)
				left.unlockBitmap();
			if(right != null)
				right.unlockBitmap();

			self.left = left;
			self.right = right;
			self.current = current;

			redrawPages();
				
			}
			
		});

	}
	
	boolean animateIn = true;

	@Override
	public synchronized void redrawPages() {
		if(width > 0 && height > 0)
			handler.post(new Runnable() {

                @Override
                public void run() {

                    AirPage[] airs = new AirPage[3];

                    airs[0] = current;
                    airs[1] = left;
                    airs[2] = right;

                    Matrix m;

                    for (int i = 0; i < airs.length; i++) {
                        AirPage ap = airs[i];
                        ImageView vi = views[i];
                        LinearLayout di = dls[i];
                        ImageView bg = bgs[i];

                        // Change images if something new was cached in AirPage.
                        // Accurately, to avoid setting recycled stuff to an ImageView.
                        if (ap != null)
                            try {
                                Drawable bitmap = null;
                                if (vi.getDrawable() != null) {
                                    bitmap = (vi.getDrawable());
                                }
                                if (ap.pageBitmap != null && bitmap != ap.pageBitmap)
                                {
                                    vi.setImageDrawable(ap.pageBitmap);

                                    if (ap.shouldRecycle && bitmap != null && bitmap != none);
                                    ap.shouldRecycle = false;
                                } else if (ap.pageBitmap == null && bitmap != none) {
                                    vi.setImageDrawable(none);
                                }

                            } catch (NullPointerException e) {
                            }
                        try {
                            // Set the translation matrix for the image based on corresponding AirPage.
                            if (ap != null) {
                                m = new Matrix();
                                int xm = 0, ym = 0;
                                if (vi.getDrawable() != null) {
                                    Drawable bitmap = (vi.getDrawable());//.getBitmap();
                                    if (bitmap != null) {
                                        float scale = Math.min(ap.getWidth() / (float) bitmap.getIntrinsicWidth(), ap.getHeight() / (float) bitmap.getIntrinsicHeight());
                                        m.setScale(scale, scale);
                                        if (Math.abs(ap.getWidth() / 2f - bitmap.getIntrinsicWidth() * scale / 2f) > 1)
                                            xm = (int) (ap.getWidth() / 2f - bitmap.getIntrinsicWidth() * scale / 2f);
                                        if (Math.abs(ap.getHeight() / 2f - bitmap.getIntrinsicHeight() * scale / 2f) > 1)
                                            ym = (int) (ap.getHeight() / 2f - bitmap.getIntrinsicHeight() * scale / 2f);
                                        m.postTranslate((int) ap.x + xm, (int) ap.y + ym);
                                    }
                                    if (bitmap == none) {
                                        m.setScale(((float) ap.defaultWidth / (float) bitmap.getIntrinsicWidth()) * ap.zoom, ((float) ap.defaultHeight / (float) bitmap.getIntrinsicHeight()) * ap.zoom);
                                        m.postTranslate(ap.x, ap.y);
                                    }
                                    vi.setImageMatrix(m);
                                    if (ap.hasAlpha)
                                        vi.setAlpha(ap.alpha);
                                    else
                                        vi.setAlpha(1f);
                                }

                                if (xm == 0 && ym == 0) {
                                    bg.clearAnimation();
                                    bg.setVisibility(View.GONE);
                                } else if (useBackground) {
                                    m = new Matrix();
                                    m.setScale(ap.getWidth(), ap.getHeight());
                                    m.postTranslate((int) ap.x, (int) ap.y);
                                    bg.setImageMatrix(m);
                                    bg.clearAnimation();
                                    bg.setVisibility(View.VISIBLE);
                                    if (ap.hasAlpha)
                                        bg.setAlpha(ap.alpha);
                                    else
                                        bg.setAlpha(1f);
                                }
                            }
                            if (ap == null) {
                                vi.clearAnimation();
                                vi.setVisibility(View.GONE);
                                di.setVisibility(View.GONE);
                            } else {
                                vi.clearAnimation();
                                vi.setVisibility(View.VISIBLE);

                                if (ap.isDownloading) {
                                    di.setVisibility(View.VISIBLE);
                                    di.bringToFront();
                                    if (di.getWidth() > 0) {
                                        di.setTranslationX(ap.x + (ap.getWidth()) / 2f - di.getWidth() / 2f);
                                        di.setTranslationY(ap.y + (ap.getHeight()) / 2f - di.getHeight() / 2f);
                                    }

                                    if (ap.hasAlpha)
                                        di.setAlpha(ap.alpha);
                                    else
                                        di.setAlpha(1f);
                                } else {
                                    di.setVisibility(View.GONE);
                                }
                            }
                        } catch (NullPointerException e) {
							e.printStackTrace();
                        }
                    }

                    self.invalidate();
                    self.requestLayout();
                }
            });
	}

	@Override
	public Point getDimensions() {
		// TODO Auto-generated method stub
		
		return new Point(width, height);
	}
}