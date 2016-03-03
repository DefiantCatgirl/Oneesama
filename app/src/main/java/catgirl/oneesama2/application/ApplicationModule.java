package catgirl.oneesama2.application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

@Module
public class ApplicationModule {
    @Provides
    @Singleton
    public Realm provideRealm() {
        return Realm.getDefaultInstance();
    }
}
