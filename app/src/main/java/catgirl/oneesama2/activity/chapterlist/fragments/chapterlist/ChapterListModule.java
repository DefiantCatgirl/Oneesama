package catgirl.oneesama2.activity.chapterlist.fragments.chapterlist;

import catgirl.oneesama.controller.ChaptersController;
import catgirl.oneesama2.data.realm.RealmProvider;
import catgirl.oneesama2.activity.chapterlist.fragments.chapterlist.data.database.RealmChapterListProvider;
import catgirl.oneesama2.activity.chapterlist.fragments.chapterlist.data.provider.ChapterListProvider;
import catgirl.oneesama2.activity.chapterlist.fragments.chapterlist.presenter.ChapterListPresenterFactory;
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
