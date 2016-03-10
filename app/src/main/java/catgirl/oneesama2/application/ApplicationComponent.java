package catgirl.oneesama2.application;

import javax.inject.Singleton;

import catgirl.oneesama2.data.realm.RealmProvider;
import catgirl.oneesama2.activity.chapterlist.fragments.chapterlist.ChapterListComponent;
import catgirl.oneesama2.activity.chapterlist.fragments.chapterlist.ChapterListModule;
import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class,
})
public interface ApplicationComponent {
    // For those odd cases where it's tiresome to bother with injections
    RealmProvider getRealmProvider();

    // Fragment scoped subcomponents
    ChapterListComponent plus(ChapterListModule module);
}
