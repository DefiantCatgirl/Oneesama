package catgirl.oneesama.activity.main.fragments.ondevice.fragments.misc.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.data.model.ChapterAuthor;
import catgirl.oneesama.activity.common.data.AutoRefreshableRealmProvider;
import catgirl.oneesama.data.model.chapter.serializable.Chapter;
import catgirl.oneesama.data.model.chapter.serializable.Tag;
import catgirl.oneesama.data.model.chapter.ui.UiChapter;
import catgirl.oneesama.data.model.chapter.ui.UiTag;
import catgirl.oneesama.data.realm.RealmProvider;
import catgirl.oneesama.tools.NaturalOrderComparator;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;

public class MiscChaptersProvider extends AutoRefreshableRealmProvider<Chapter, ChapterAuthor> {

    private RealmProvider realmProvider;

    public MiscChaptersProvider(RealmProvider realmProvider) {
        this.realmProvider = realmProvider;
    }

    @Override
    public Realm getRealm() {
        return realmProvider.provideRealm();
    }

    @Override
    public RealmQuery<Chapter> getQuery(Realm realm) {
        return realm.allObjects(Chapter.class)
                .where()
                .not()
                .equalTo("tags.type", UiTag.SERIES)
                .not()
                .equalTo("tags.type", UiTag.DOUJIN);
    }

    @Override
    public List<ChapterAuthor> processQueryResults(Realm realm, RealmResults<Chapter> results) {
        List<ChapterAuthor> result = new ArrayList<>();

        Observable.from(results)
                .map(chapter -> {
                    Tag tag = chapter.getTags()
                            .where()
                            .equalTo("type", UiTag.AUTHOR)
                            .findFirst();
                    if (tag == null) {
                        tag = new Tag();
                        tag.setName("No author");
                        tag.setType(UiTag.AUTHOR);
                    }
                    return new ChapterAuthor(new UiChapter(chapter), new UiTag(tag));
                })
                .toList()
                .subscribe(result::addAll);

        Collections.sort(result, (lhs, rhs) -> new NaturalOrderComparator().compare(lhs.chapter.getTitle(), rhs.chapter.getTitle()));

        return result;
    }
}
