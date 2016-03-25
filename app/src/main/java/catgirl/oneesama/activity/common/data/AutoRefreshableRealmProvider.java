package catgirl.oneesama.activity.common.data;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;
import rx.subjects.PublishSubject;

public abstract class AutoRefreshableRealmProvider<R extends RealmObject, T> {
    private Realm realm;
    protected PublishSubject<List<T>> subject = PublishSubject.create();
    protected RealmChangeListener changeListener;
    protected RealmResults<R> results;
    protected HandlerThread thread;
    protected Handler mainThreadHandler = new Handler();

    public Observable<List<T>> subscribeForItems() {
        thread = new HandlerThread(getClass().getName()) {
            private int lastCount = -1;

            @Override
            protected void onLooperPrepared() {
                realm = getRealm();

                results = getQuery(realm).findAllAsync();

                changeListener = () -> {
                    // Hack to let the download animations work properly
                    if (results.size() == lastCount)
                        return;

                    lastCount = results.size();

                    subject.onNext(processQueryResults(realm, results));
                };

                results.addChangeListener(changeListener);
            }
        };

        // To avoid getting the first change listener message before we subscribed to the subject
        mainThreadHandler.post(thread::start);

        return subject;
    }


    public void onDestroy() {
        if (thread != null && thread.getLooper() != null) {
            new Handler(thread.getLooper()).post(() -> {
                results.removeChangeListeners();
                realm.close();
                thread.quit();
            });
        }
    };

    public abstract Realm getRealm();
    public abstract RealmQuery<R> getQuery(Realm realm);
    public abstract List<T> processQueryResults(Realm realm, RealmResults<R> results);
}
