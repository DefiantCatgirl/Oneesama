package catgirl.oneesama.activity.chapterlist.fragments.chapterlist;

import catgirl.oneesama.data.controller.ChaptersController;
import catgirl.oneesama.data.realm.RealmProvider;
import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.data.RealmChapterListProvider;
import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.data.ChapterListProvider;
import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.presenter.ChapterListPresenterFactory;
import dagger.Module;
import dagger.Provides;

@Module
public class ChapterListModule {
    @Provides
    public ChapterListProvider getProvider(RealmProvider realmProvider) {
        return new RealmChapterListProvider(realmProvider);
    }

    @Provides
    public ChapterListPresenterFactory getPresenterFactory(ChapterListProvider listProvider, ChaptersController chaptersController) {
        return new ChapterListPresenterFactory(listProvider, chaptersController);
    }
}
