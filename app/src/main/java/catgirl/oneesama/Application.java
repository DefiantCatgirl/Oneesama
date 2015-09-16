package catgirl.oneesama;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Application extends android.app.Application {

    private static Application appContext;

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = this;

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .schemaVersion(0)
                .build();

        Realm.setDefaultConfiguration(config);
    }

    public static Context getContextOfApplication() {
        return appContext.getApplicationContext();
    }
}
