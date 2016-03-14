package catgirl.oneesama.activity.legacyreader.activityreader;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;

import catgirl.oneesama.application.Application;

public class BaseActivity extends AppCompatActivity {
    protected Application mMyApp;
    
    public boolean paused = false;
    
    public static long stoppedAt = 0;
    
    public int width;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyApp = (Application) this.getApplicationContext();
        
		Display display = getWindowManager().getDefaultDisplay(); 
		if(android.os.Build.VERSION.SDK_INT >= 13)
		{
			Point out = new Point();
			display.getSize(out);
			width = out.x;
		}
		else
			width = display.getWidth();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
//        MApplication.setCurrentActivity(this);
    }
    
    @Override
    protected void onPause() {
        clearReferences();
        super.onPause();
    }

    @Override
    protected void onDestroy() {        
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences(){
//        Activity currActivity = MApplication.getCurrentActivity();
//        if (currActivity != null && currActivity.equals(this))
//            MApplication.setCurrentActivity(null);
    }
}
