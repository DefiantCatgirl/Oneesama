package catgirl.oneesama.ui.activity.legacyreader.tools;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;

import catgirl.oneesama.Application;

// Mostly unsorted stuff from google and stackoverflow to make life easier
public class ActivityUtils {

	public ActivityUtils() {
		// TODO Auto-generated constructor stub
	}

	public static void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
		int childCount = viewGroup.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View view = viewGroup.getChildAt(i);
			view.setEnabled(enabled);
			if (view instanceof ViewGroup) {
				enableDisableViewGroup((ViewGroup) view, enabled);
			}
		}
	}

	public static int dpToPx(int dp)
	{
		return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
	}
	
	// These two methods copypasted from google examples :P I'm lazy, sorry m(__)m
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	    
	    if(reqWidth == 0 || reqHeight == 0)
	    	return 1;

	    if (height > reqHeight || width > reqWidth) {

	        // Calculate ratios of height and width to requested height and width
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);

	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }

	    return inSampleSize;
	}
	
	public static int calculateInSampleSizeMin(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	    
	    if(reqWidth == 0 || reqHeight == 0)
	    	return 1;

	    if (height > reqHeight || width > reqWidth) {

	        // Calculate ratios of height and width to requested height and width
	        final int heightRatio = (int) Math.ceil((float) height / (float) reqHeight);
	        final int widthRatio = (int) Math.ceil((float) width / (float) reqWidth);

	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
	    }

	    return inSampleSize;
	}
	
	public static Bitmap decodeBitmap(InputStream istr,
	        int reqWidth, int reqHeight) {
		if(istr == null)
			return null;
		
		istr.mark(1000000000);
	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    options.inPreferredConfig = Config.ARGB_8888;

	    BitmapFactory.decodeStream(istr, null, options);
	    
	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    try {
			istr.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}

	    Bitmap b = BitmapFactory.decodeStream(istr, null, options);
	    
	    return b;
	}
	
	public static Bitmap decodeBitmap(int resource,
	        int reqWidth, int reqHeight) {
		
	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    options.inPreferredConfig = Config.ARGB_8888;

	    BitmapFactory.decodeResource(Application.getContextOfApplication().getResources(), resource, options);
	    
	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;

	    Bitmap b = BitmapFactory.decodeResource(Application.getContextOfApplication().getResources(), resource, options);
	    
	    return b;
	}
	
	public static void setViewBackgroundWithoutResettingPadding(final View v, final int backgroundResId) {
	    final int paddingBottom = v.getPaddingBottom(), paddingLeft = v.getPaddingLeft();
	    final int paddingRight = v.getPaddingRight(), paddingTop = v.getPaddingTop();
	    v.setBackgroundResource(backgroundResId);
	    v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
	}
	
	public static View getLiveListChild(ListView listView, int position)
	{
		int firstPosition = listView.getFirstVisiblePosition() - listView.getHeaderViewsCount(); // This is the same as child #0
		int wantedChild = position - firstPosition;
		// Say, first visible position is 8, you want position 10, wantedChild will now be 2
		// So that means your view is child #2 in the ViewGroup:
		if (wantedChild < 0 || wantedChild >= listView.getChildCount()) {
			return null;
		}
		return listView.getChildAt(wantedChild);
	}
	
	public static Embed embed(float containerWidth, float containerHeight, float contentWidth, float contentHeight)
	{
		Embed result = new Embed();
		
		float xFactor = containerWidth / contentWidth;
		float yFactor = containerHeight / contentHeight;
		float factor = Math.min(xFactor, yFactor);
		
		result.x = contentWidth * factor;
		result.y = contentHeight * factor;
		result.factor = factor;
		
		return result;
	}
	
    public static final int MAX_SIZE = 2048;
    
    public static class ByteArray {
  	  public byte[] array;
  	  public int count;
    }
    
    public static ByteArray byteArrayFromStream(InputStream istr)
    {
	        byte[] byteArr = new byte[0];
	        byte[] buffer = new byte[1024];
	        int len;
	        int count = 0;
			
          try {
				while ((len = istr.read(buffer)) > -1) {
				    if (len != 0) {
				        if (count + len > byteArr.length) {
				            byte[] newbuf = new byte[(count + len) * 2];
				            System.arraycopy(byteArr, 0, newbuf, 0, count);
				            byteArr = newbuf;
				        }

				        System.arraycopy(buffer, 0, byteArr, count, len);
				        count += len;
				    }
				}
		  } catch (IOException e1) {
			  e1.printStackTrace();
		  } catch (OutOfMemoryError e2) {
			  return null;
		  }

          ByteArray a = new ByteArray();
          a.array = byteArr;
          a.count = count;
          return a;
    }

    @SuppressLint("NewApi")
	public static Drawable createLargeDrawable(ByteArray a) throws IOException {

		BitmapFactory.Options onlySize = new BitmapFactory.Options();
		onlySize.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(a.array, 0, a.count, onlySize);

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inDither = true;

		BitmapRegionDecoder brd = null;

		try {
			if (onlySize.outWidth <= MAX_SIZE && onlySize.outHeight <= MAX_SIZE) {

//            	brd = BitmapRegionDecoder.newInstance(a.array, 0, a.count, true);
//            	return new BitmapDrawable(MApplication.getAppContext().getResources(), brd.decodeRegion(new Rect(0, 0, brd.getWidth(), brd.getHeight()), opts));
				try {
					return new BitmapDrawable(Application.getContextOfApplication().getResources(), BitmapFactory.decodeByteArray(a.array, 0, a.count, opts));
				} catch (OutOfMemoryError e) {
					try {
						opts.inSampleSize = 2;
						return new BitmapDrawable(Application.getContextOfApplication().getResources(), BitmapFactory.decodeByteArray(a.array, 0, a.count, opts));
					} catch (OutOfMemoryError e1) {
						return null;
					}
				}
			}

			brd = BitmapRegionDecoder.newInstance(a.array, 0, a.count, true);

			int rowCount = (int) Math.ceil((float) brd.getHeight() / (float) MAX_SIZE);
			int colCount = (int) Math.ceil((float) brd.getWidth() / (float) MAX_SIZE);

			BitmapDrawable[] drawables = new BitmapDrawable[rowCount * colCount];

			for (int i = 0; i < rowCount; i++) {

				int top = MAX_SIZE * i;
				int bottom = i == rowCount - 1 ? brd.getHeight() : top + MAX_SIZE;

				for (int j = 0; j < colCount; j++) {

					int left = MAX_SIZE * j;
					int right = j == colCount - 1 ? brd.getWidth() : left + MAX_SIZE;

					Bitmap b = brd.decodeRegion(new Rect(left, top, right, bottom), opts);

					if(b == null)
						throw new StreamCorruptedException();

					BitmapDrawable bd = new BitmapDrawable(Application.getContextOfApplication().getResources(), b);
					bd.setGravity(Gravity.TOP | Gravity.LEFT);
					drawables[i * colCount + j] = bd;
				}
			}

			LayerDrawable ld = new LayerDrawable(drawables);
			for (int i = 0; i < rowCount; i++) {
				for (int j = 0; j < colCount; j++) {
					ld.setLayerInset(i * colCount + j, MAX_SIZE * j, MAX_SIZE * i, 0, 0);
				}
			}

			return ld;
		}
		finally {
			if(brd != null)
				brd.recycle();
		}
	}
    
    @SuppressLint("NewApi")
	public static Drawable createLargeDrawable(Bitmap b) throws IOException {

  	  BitmapFactory.Options opts = new BitmapFactory.Options();
  	  opts.inDither = true;

        try {
            if (b.getWidth() <= MAX_SIZE && b.getHeight() <= MAX_SIZE) {
                return new BitmapDrawable(Application.getContextOfApplication().getResources(), b);
            }

            int rowCount = (int) Math.ceil((float) b.getHeight() / (float) MAX_SIZE);
            int colCount = (int) Math.ceil((float) b.getWidth() / (float) MAX_SIZE);

            BitmapDrawable[] drawables = new BitmapDrawable[rowCount * colCount];

            for (int i = 0; i < rowCount; i++) {

                int top = MAX_SIZE * i;
                int bottom = i == rowCount - 1 ? b.getHeight() : top + MAX_SIZE;

                for (int j = 0; j < colCount; j++) {

                    int left = MAX_SIZE * j;
                    int right = j == colCount - 1 ? b.getWidth() : left + MAX_SIZE;
                    
                    if(left >= b.getWidth() || top >= b.getHeight())
                  	  continue;

                    Bitmap bb = Bitmap.createBitmap(b, left, top, Math.min(right - left, (b.getWidth() - left)), Math.min(bottom - top, (b.getHeight() - top)), null, false);
                    BitmapDrawable bd = new BitmapDrawable(Application.getContextOfApplication().getResources(), bb);
                    bd.setGravity(Gravity.TOP | Gravity.LEFT);
                    drawables[i * colCount + j] = bd;
                }
            }

            LayerDrawable ld = new LayerDrawable(drawables);
            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < colCount; j++) {
                    ld.setLayerInset(i * colCount + j, MAX_SIZE * j, MAX_SIZE * i, 0, 0);
                }
            }

            return ld;
        }
        finally {
//            brd.recycle();
        }
    }

	enum Numeral {
		I(1), IV(4), V(5), IX(9), X(10), XL(40), L(50), XC(90), C(100), CD(400), D(500), CM(900), M(1000);
		int weight;

		Numeral(int weight) {
			this.weight = weight;
		}
	};

	public static String roman(long n) {

		if( n <= 0) {
			throw new IllegalArgumentException();
		}

		StringBuilder buf = new StringBuilder();

		final Numeral[] values = Numeral.values();
		for (int i = values.length - 1; i >= 0; i--) {
			while (n >= values[i].weight) {
				buf.append(values[i]);
				n -= values[i].weight;
			}
		}
		return buf.toString();
	}
}
