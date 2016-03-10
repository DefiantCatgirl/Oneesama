package catgirl.oneesama2.application;

import javax.inject.Singleton;

import catgirl.oneesama.controller.ChaptersController;
import catgirl.oneesama2.data.realm.RealmProvider;
import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

@Module
public class ApplicationModule {
    @Provides
    @Singleton
    public RealmProvider provideRealmProvider() {
        return new RealmProvider();
    }

    @Provides
    @Singleton
    public ChaptersController provideChaptersController() {
        return ChaptersController.getInstance();
    }
}
