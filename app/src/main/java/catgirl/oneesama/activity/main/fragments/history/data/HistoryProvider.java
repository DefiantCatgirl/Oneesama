package catgirl.oneesama.activity.main.fragments.history.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.data.model.ChapterAuthor;
import catgirl.oneesama.activity.common.data.AutoRefreshableRealmProvider;
import catgirl.oneesama.data.model.chapter.serializable.Chapter;
import catgirl.oneesama.data.model.chapter.serializable.Tag;
import catgirl.oneesama.data.model.chapter.ui.UiChapter;
import catgirl.oneesama.data.model.chapter.ui.UiTag;
import catgirl.oneesama.data.realm.RealmProvider;
import catgirl.oneesama.data.settings.RecentlyOpenedChapters;
import catgirl.oneesama.data.settings.SettingsProvider;
import catgirl.oneesama.tools.NaturalOrderComparator;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;

public class HistoryProvider extends AutoRefreshableRealmProvider<Chapter, UiChapter> {
    private RealmProvider realmProvider;
    private SettingsProvider<RecentlyOpenedChapters> settingsProvider;

    public HistoryProvider(RealmProvider realmProvider, SettingsProvider<RecentlyOpenedChapters> settingsProvider) {
        this.realmProvider = realmProvider;
        this.settingsProvider = settingsProvider;
    }

    @Override
    public Realm getRealm() {
        return realmProvider.provideRealm();
    }

    @Override
    public RealmQuery<Chapter> getQuery(Realm realm) {
        return realm.where(Chapter.class);
    }

    @Override
    public List<UiChapter> processQueryResults(Realm realm, RealmResults<Chapter> results) {
        List<UiChapter> result = new ArrayList<>();
        RecentlyOpenedChapters recentlyOpenedChapters = settingsProvider.retrieve();

        Observable.from(results)
                .filter(chapter -> recentlyOpenedChapters.getOpenDate(chapter.getId()) != 0)
                .map(UiChapter::new)
                .toList()
                .subscribe(result::addAll);

        Collections.sort(result, (lhs, rhs) -> {
            long lhsOpenDate = recentlyOpenedChapters.getOpenDate(lhs.getId());
            long rhsOpenDate = recentlyOpenedChapters.getOpenDate(rhs.getId());

            if (lhsOpenDate > rhsOpenDate)
                return -1;

            if (rhsOpenDate > lhsOpenDate)
                return 1;

            return new NaturalOrderComparator().compare(lhs.getLongTitle(), rhs.getLongTitle());
        });


        return result;
    }
}
