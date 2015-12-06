package catgirl.oneesama.migrations;

import android.content.Context;
import android.util.Log;

import catgirl.oneesama.model.chapter.serializable.Chapter;
import catgirl.oneesama.scraper.chaptername.DynastySeriesPage;
import catgirl.oneesama.scraper.chaptername.DynastySeriesPageProvider;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.schedulers.Schedulers;

public class MigrateChapterNames {
    public static void migrateChapterNames(Context context) {

        int success = context.getSharedPreferences("migrations", Context.MODE_PRIVATE).getInt("migrateChapterNames", 0);

        if (success < 5) {
            Observable
                    .fromCallable(() -> {
                        Realm realm = Realm.getDefaultInstance();

                        RealmResults<Chapter> chapters = realm
                                .allObjects(Chapter.class)
                                .where()
                                .equalTo("tags.type", "Series")
                                .equalTo("volumeName", (String) null)
                                .findAll();

                        for(int i = 0; i < chapters.size(); i++) {
                            Chapter c = chapters.get(i);
                            Log.v("Debug", c.getTitle());
                            DynastySeriesPage.Chapter chapter = DynastySeriesPageProvider.provideChapterInfo(
                                    c.getTags().where().equalTo("type", "Series").findFirst().getPermalink(),
                                    c.getPermalink());
                            if (chapter != null) {
                                realm.beginTransaction();
                                c.setTitle(chapter.chapterName);
                                c.setVolumeName(chapter.volumeName);
                                realm.copyToRealmOrUpdate(c);
                                realm.commitTransaction();
                            }
                        }

                        return true;
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            result -> context.getSharedPreferences("migrations", Context.MODE_PRIVATE).edit().putInt("migrateChapterNames", success + 1).commit(),
                            Throwable::printStackTrace);
        }
    }
}
