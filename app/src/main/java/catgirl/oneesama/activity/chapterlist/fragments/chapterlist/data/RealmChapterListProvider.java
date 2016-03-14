package catgirl.oneesama.activity.chapterlist.fragments.chapterlist.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.data.model.ChapterAuthor;
import catgirl.oneesama.data.model.chapter.serializable.Chapter;
import catgirl.oneesama.data.model.chapter.serializable.Tag;
import catgirl.oneesama.data.model.chapter.ui.UiChapter;
import catgirl.oneesama.data.model.chapter.ui.UiTag;
import catgirl.oneesama.tools.NaturalOrderComparator;
import catgirl.oneesama.data.realm.RealmProvider;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;

public class RealmChapterListProvider implements ChapterListProvider {

    private RealmProvider realmProvider;

    public RealmChapterListProvider(RealmProvider realmProvider) {
        this.realmProvider = realmProvider;
    }

    @Override
    public Observable<List<ChapterAuthor>> getChapterAuthorList(int tagId, boolean sortByVolumes) {
        return Observable.fromCallable(() -> {
            Realm realm = realmProvider.provideRealm();

            RealmResults<Chapter> results = realm.allObjects(Chapter.class)
                    .where()
                    .equalTo("tags.id", tagId)
                    .findAll();

            List<ChapterAuthor> result = new ArrayList<>();

            Observable.from(results)
                    .map(chapter -> new ChapterAuthor(new UiChapter(chapter), new UiTag(chapter.getTags()
                            .where()
                            .equalTo("type", UiTag.AUTHOR)
                            .findFirst())))
                    .toList()
                    .subscribe(result::addAll);

            if (sortByVolumes) {
                Collections.sort(result, (lhs, rhs) -> {
                    if (lhs.chapter.getVolumeName() == null && rhs.chapter.getVolumeName() != null)
                        return 1;
                    if (rhs.chapter.getVolumeName() == null && lhs.chapter.getVolumeName() != null)
                        return -1;
                    int r = 0;
                    if (lhs.chapter.getVolumeName() != null)
                        r = new NaturalOrderComparator().compare(lhs.chapter.getVolumeName(), rhs.chapter.getVolumeName());
                    if (r != 0)
                        return r;
                    return new NaturalOrderComparator().compare(lhs.chapter.getTitle(), rhs.chapter.getTitle());
                });
            } else {
                Collections.sort(result, (lhs, rhs) -> new NaturalOrderComparator().compare(lhs.chapter.getTitle(), rhs.chapter.getTitle()));
            }

            realm.close();

            return result;
        });
        // TODO: subscription thread should probably be decided in presenter configuration
    }

    @Override
    public UiTag getTag(int tagId) {
        Realm realm = realmProvider.provideRealm();
        Tag tag = realm.where(Tag.class).equalTo("id", tagId).findFirst();
        UiTag uiTag = new UiTag(tag);
        realm.close();
        return uiTag;
    }
}
