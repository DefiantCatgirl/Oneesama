package catgirl.oneesama.application;

import javax.inject.Singleton;

import catgirl.oneesama.data.controller.ChaptersController;
import catgirl.oneesama.data.realm.RealmProvider;
import dagger.Module;
import dagger.Provides;

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
