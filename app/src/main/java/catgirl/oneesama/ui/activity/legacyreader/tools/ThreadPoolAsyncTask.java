package catgirl.oneesama.ui.activity.legacyreader.tools;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

@SuppressLint("NewApi")
public abstract class ThreadPoolAsyncTask<Param, Progress, Result> extends AsyncTask<Param, Progress, Result> {

    private static final boolean API_LEVEL_11 
        = android.os.Build.VERSION.SDK_INT >= 11;

    public void asyncExecute(Param... aParams) {     
        if(API_LEVEL_11)
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, aParams); 
        else
            super.execute(aParams);
    }

}