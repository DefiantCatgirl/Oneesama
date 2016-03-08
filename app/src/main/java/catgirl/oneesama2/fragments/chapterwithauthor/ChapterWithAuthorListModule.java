package catgirl.oneesama2.fragments.chapterwithauthor;

import catgirl.oneesama2.data.realm.RealmProvider;
import catgirl.oneesama2.fragments.chapterwithauthor.data.database.RealmChapterWithAuthorListProvider;
import catgirl.oneesama2.fragments.chapterwithauthor.data.provider.ChapterWithAuthorListProvider;
import catgirl.oneesama2.fragments.chapterwithauthor.presenter.ChapterWithAuthorListPresenterFactory;
import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

@Module
public class ChapterWithAuthorListModule {
    @Provides
    public ChapterWithAuthorListProvider getProvider(RealmProvider realmProvider) {
        return new RealmChapterWithAuthorListProvider(realmProvider);
    }

    @Provides
    public ChapterWithAuthorListPresenterFactory getPresenterFactory(ChapterWithAuthorListProvider listProvider) {
        return new ChapterWithAuthorListPresenterFactory(listProvider);
    }
}
