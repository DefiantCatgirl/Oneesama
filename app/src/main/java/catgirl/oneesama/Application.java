package catgirl.oneesama;

import android.content.Context;
import android.util.Log;

import com.yandex.metrica.YandexMetrica;

import catgirl.oneesama.migrations.MigrateChapterNames;
import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

public class Application extends android.app.Application {

    private static Application appContext;

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = this;

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .schemaVersion(1)
                .migration(new RealmMigration() {
                    @Override
                    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                        Log.v("Realm", "Migrating: " + oldVersion + " -> " + newVersion);

                        if (oldVersion == 0) {
                            realm.getSchema().get("Chapter").addField("volumeName", String.class);
                        }
                    }
                })
                .build();

        Realm.setDefaultConfiguration(config);

        // Migrations
        MigrateChapterNames.migrateChapterNames(this);

        YandexMetrica.activate(getApplicationContext(), getString(R.string.metrics_token));
        YandexMetrica.setSessionTimeout(600);
        YandexMetrica.setCollectInstalledApps(false);
        YandexMetrica.setTrackLocationEnabled(false);
    }

    public static Context getContextOfApplication() {
        return appContext.getApplicationContext();
    }
}
