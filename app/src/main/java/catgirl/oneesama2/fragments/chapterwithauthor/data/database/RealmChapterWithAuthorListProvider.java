package catgirl.oneesama2.fragments.chapterwithauthor.data.database;

import java.util.ArrayList;
import java.util.List;

import catgirl.oneesama.model.chapter.serializable.Chapter;
import catgirl.oneesama.model.chapter.ui.UiChapter;
import catgirl.oneesama.model.chapter.ui.UiTag;
import catgirl.oneesama.ui.common.chapter.ChapterAuthor;
import catgirl.oneesama2.fragments.chapterwithauthor.data.provider.ChapterWithAuthorListProvider;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;

public class RealmChapterWithAuthorListProvider implements ChapterWithAuthorListProvider {
    @Override
    public Observable<List<ChapterAuthor>> getChapterAuthorList(String tagId) {
        return Observable.fromCallable(() -> {
            // TODO: provide with Dagger
            Realm realm = Realm.getDefaultInstance();

            RealmResults<Chapter> results = realm.allObjects(Chapter.class)
                    .where()
                    .equalTo("tags.id", tagId)
                    .findAllSorted("title");

            List<ChapterAuthor> result = new ArrayList<>();

            Observable.from(results)
                    .map(chapter -> new ChapterAuthor(new UiChapter(chapter), new UiTag(chapter.getTags()
                            .where()
                            .equalTo("type", "Author")
                            .findFirst())))
                    .toList()
                    .subscribe(result::addAll);

            return result;
        });
        // TODO: subscription thread should probably be decided in presenter configuration
    }
}
