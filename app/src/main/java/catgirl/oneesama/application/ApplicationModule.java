package catgirl.oneesama.application;

import android.content.Context;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import catgirl.oneesama.data.controller.ChaptersController;
import catgirl.oneesama.data.network.api.DynastyService;
import catgirl.oneesama.data.realm.RealmProvider;
import catgirl.oneesama.data.settings.RecentlyOpenedChapters;
import catgirl.oneesama.data.settings.SettingsProvider;
import catgirl.oneesama.data.settings.SharedSettingsProvider;
import dagger.Module;
import dagger.Provides;
import io.realm.RealmObject;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

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

    @Provides
    public DynastyService provideDynastyService() {
        // Hack to make GSON play well with Realm
        // TODO: check if it's still necessary
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.apiEndpoint)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(DynastyService.class);
    }

    @Provides
    public SettingsProvider<RecentlyOpenedChapters> provideRecentlyOpenedSettingsProvider(Context context) {
        return new SharedSettingsProvider<>(context, RecentlyOpenedChapters.class);
    }

    @Provides
    public Context provideApplicationContext() {
        return Application.getContextOfApplication();
    }
}
