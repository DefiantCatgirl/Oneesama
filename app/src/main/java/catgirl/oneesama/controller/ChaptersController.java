package catgirl.oneesama.controller;

import android.net.Uri;
import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

import catgirl.oneesama.Application;
import catgirl.oneesama.api.Config;
import catgirl.oneesama.api.DynastyService;
import catgirl.oneesama.controller.legacy.Book;
import catgirl.oneesama.controller.legacy.BookStateDelegate;
import catgirl.oneesama.controller.legacy.CacherDelegate;
import catgirl.oneesama.model.chapter.serializable.Chapter;
import catgirl.oneesama.model.chapter.ui.UiChapter;
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
            Realm realm = Realm.getInstance(Application.getContextOfApplication());
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
    public Observable<Book> requestChapterController(final Uri uri) {
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

        return service.getChapter(uri.getLastPathSegment())
                .subscribeOn(Schedulers.io())
                .doOnNext(response -> {
                    Realm realm = Realm.getInstance(Application.getContextOfApplication());
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
            Realm realm = Realm.getInstance(Application.getContextOfApplication());
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
}
