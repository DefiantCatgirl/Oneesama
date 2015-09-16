package catgirl.oneesama;

import android.content.Context;

public class Application extends android.app.Application {

    private static Application appContext;

    @Override
    public void onCreate() {
        appContext = this;
    }

    public static Context getContextOfApplication() {
        return appContext.getApplicationContext();
    }
}
