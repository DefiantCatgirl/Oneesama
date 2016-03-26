package catgirl.oneesama.activity.browseseriespage.fragment.data;

import java.util.ArrayList;
import java.util.List;

import catgirl.oneesama.activity.browseseriespage.fragment.data.model.BrowseSeriesPage;
import catgirl.oneesama.activity.browseseriespage.fragment.data.model.BrowseSeriesPageChapter;
import catgirl.oneesama.activity.common.data.AutoRefreshableRealmProvider;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.model.RecentChapter;
import catgirl.oneesama.data.model.chapter.serializable.Chapter;
import catgirl.oneesama.data.model.chapter.ui.UiChapter;
import catgirl.oneesama.data.realm.RealmProvider;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;
import rx.schedulers.Schedulers;

public class BrowseSeriesPageToLocalProvider extends AutoRefreshableRealmProvider<Chapter, Object> {

    private final RealmProvider realmProvider;
    private List<Object> chapters;

    public BrowseSeriesPageToLocalProvider(RealmProvider realmProvider) {
        this.realmProvider = realmProvider;
    }

    // TODO: this class should work with immutable data, don't be lazy
    public void setCurrentItems(List<Object> chapters) {
        this.chapters = new ArrayList<>(chapters);

        Observable.fromCallable(
                () -> {
                    Realm realm = getRealm();
                    return processQueryResults(realm, getQuery(realm).findAll());
                })
                .subscribeOn(Schedulers.io())
                .subscribe(subject::onNext);
    }

    @Override
    public Realm getRealm() {
        return realmProvider.provideRealm();
    }

    @Override
    public RealmQuery<Chapter> getQuery(Realm realm) {
        return realm.where(Chapter.class);
    }

    // Synchronized because shitty code, everything is too mutable
    @Override
    public synchronized List<Object> processQueryResults(Realm realm, RealmResults<Chapter> results) {
        for (Object object : chapters) {
            if (object instanceof BrowseSeriesPageChapter) {
                BrowseSeriesPageChapter chapter = (BrowseSeriesPageChapter) object;

                Chapter local = results.where().equalTo("permalink", chapter.permalink).findFirst();
                if (local != null) {
                    chapter.chapter = new UiChapter(local);
                } else {
                    chapter.chapter = null;
                }
            }
        }
        return new ArrayList<>(chapters);
    }
}
