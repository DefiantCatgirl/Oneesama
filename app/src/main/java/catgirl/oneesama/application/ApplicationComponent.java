package catgirl.oneesama.application;

import javax.inject.Singleton;

import catgirl.oneesama.activity.main.MainActivityComponent;
import catgirl.oneesama.activity.main.MainActivityModule;
import catgirl.oneesama.data.realm.RealmProvider;
import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.ChapterListComponent;
import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.ChapterListModule;
import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class,
})
public interface ApplicationComponent {
    // For those odd cases where it's tiresome to bother with injections
    RealmProvider getRealmProvider();

    // Activity scoped subcomponents
    MainActivityComponent plus(MainActivityModule module);

    // Fragment scoped subcomponents
    ChapterListComponent plus(ChapterListModule module);
}
