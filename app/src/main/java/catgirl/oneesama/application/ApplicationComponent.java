package catgirl.oneesama.application;

import javax.inject.Singleton;

import catgirl.oneesama.activity.browseseriespage.BrowseSeriesPageActivityComponent;
import catgirl.oneesama.activity.browseseriespage.BrowseSeriesPageActivityModule;
import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.ChapterListComponent;
import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.ChapterListModule;
import catgirl.oneesama.activity.legacyreader.activityreader.ReaderActivity;
import catgirl.oneesama.activity.main.MainActivityComponent;
import catgirl.oneesama.activity.main.MainActivityModule;
import catgirl.oneesama.data.realm.RealmProvider;
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
    BrowseSeriesPageActivityComponent plus(BrowseSeriesPageActivityModule module);

    // Fragment scoped subcomponents
    ChapterListComponent plus(ChapterListModule module);

    // That one annoying legacy activity
    void inject(ReaderActivity readerActivity);
}
