package catgirl.oneesama.controller;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import catgirl.oneesama.api.Config;
import catgirl.oneesama.api.DynastyService;
import catgirl.oneesama.controller.legacy.Book;
import catgirl.oneesama.controller.legacy.BookStateDelegate;
import catgirl.oneesama.controller.legacy.CacherDelegate;
import catgirl.oneesama.model.chapter.serializable.Chapter;
import catgirl.oneesama.model.chapter.serializable.Page;
import catgirl.oneesama.model.chapter.serializable.Tag;
import catgirl.oneesama.model.chapter.ui.UiChapter;
import catgirl.oneesama.scraper.DynastySeriesPage;
import catgirl.oneesama.scraper.DynastySeriesPageProvider;
import io.realm.Realm;
import io.realm.RealmObject;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChaptersController implements BookStateDelegate, CacherDelegate {
    private static ChaptersController ourInstance = new ChaptersController();

    public static ChaptersController getInstance() {
        return ourInstance;
    }

    private Map<Integer, Book> controllers = new HashMap<>();

    private ChaptersController() {

    }

    public Book getChapterController(int id) {
        if(controllers.containsKey(id))
            return controllers.get(id);
        else {
            Realm realm = Realm.getDefaultInstance();
            Chapter chapter = realm.where(Chapter.class).equalTo("id", id).findFirst();
            realm.close();
            Book book = new Book(new UiChapter(chapter), this, this, false, null);
            book.startDownload();
            controllers.put(id, book);
            return book;
        }
    }

    public boolean isChapterControllerActive(int id) {
        return controllers.containsKey(id);
    }

    // TODO create a data abstraction layer
    public Observable<Book> requestChapterController(final String uri) {
        // TODO check if the next version of Gson doesn't require this hack to work with RealmObjects
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.apiEndpoint)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        DynastyService service = retrofit.create(DynastyService.class);

        return service.getChapter(uri)
                .subscribeOn(Schedulers.io())
                .doOnNext(response -> {
                    // Find out the real, full chapter name and the corresponding volume
                    // This is only available on the series page, does not apply if not part of a series
                    String series = null;
                    for(Tag tag : response.getTags()) {
                        if(tag.getType().equals("Series")) {
                            series = tag.getPermalink();
                            break;
                        }
                    }

                    if(series != null) {
                        try {
                            DynastySeriesPage.Chapter c = DynastySeriesPageProvider.provideChapterInfo(series, response.getPermalink());
                            response.setTitle(c.chapterName);
                            response.setVolumeName(c.volumeName);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }
                })
                .doOnNext(response -> {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(response);
                    realm.commitTransaction();
                    realm.close();
                })
                .map(response -> {
                    Book book = new Book(new UiChapter(response), this, this, false, null);
                    controllers.put(response.getId(), book);
                    book.startDownload();
                    return book;
                })
                .doOnNext(response -> controllers.put(response.data.getId(), response))
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void pageDownloaded(int id, boolean bookDownloaded, int pageId, boolean onlyProgress) {

    }

    @Override
    public void completelyDownloaded(int id, boolean success) {
        if(success) {
            Realm realm = Realm.getDefaultInstance();
            Chapter chapter = realm.where(Chapter.class).equalTo("id", id).findFirst();
            realm.beginTransaction();
            chapter.setCompletelyDownloaded(true);
            realm.copyToRealmOrUpdate(chapter);
            realm.commitTransaction();
            realm.close();
        }
    }

    @Override
    public void onCacheUpdated() {

    }

    @Override
    public void onPageDimensionsChanged(int pageId) {

    }

    public void deleteChapter(int id) {
        if(controllers.containsKey(id)) {
            Book book = controllers.get(id);
            book.cancelDownload();
            controllers.remove(id);
        }
        Realm realm = Realm.getDefaultInstance();
        Chapter chapter = realm.where(Chapter.class).equalTo("id", id).findFirst();
        realm.beginTransaction();

        List<RealmObject> toRemove = new ArrayList<>();

        // Collect orphaned pages
        for(Page page : chapter.getPages())
            toRemove.add(page);

        List<Tag> tags = new ArrayList<>();
        tags.addAll(chapter.getTags());

        chapter.removeFromRealm();

        // Collect orphaned tags
        for(Tag tag : tags) {
            if(realm.where(Chapter.class).equalTo("tags.id", tag.getId()).count() == 0)
                toRemove.add(tag);
        }

        // Clean orphaned tags and pages
        for(RealmObject object : toRemove)
            object.removeFromRealm();

        realm.commitTransaction();
        realm.close();

        FileManager.deleteFolder(id);
    }
}
