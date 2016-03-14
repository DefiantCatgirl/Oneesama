package catgirl.oneesama.activity.common.presenter;

import java.util.List;

import catgirl.oneesama.activity.common.data.AutoRefreshableRealmProvider;
import catgirl.oneesama.activity.common.view.SimpleRecyclerView;
import rx.Observable;

/**
 * To be honest this class is only needed to make sure resources are cleaned out, which is an inconvenient consequence of opening threads in auto-refreshing providers
 * @param <T>
 * @param <V>
 * @param <P>
 */
public abstract class AutoRefreshableRecyclerPresenter<T, V extends SimpleRecyclerView<T>, P extends AutoRefreshableRealmProvider> extends SimpleRecyclerPresenter<T, V> {
    public abstract P getProvider();

    @Override
    public Observable<List<T>> getItemObservable() {
        // The warning is an unfortunate implication of not wanting to mention RealmObject in the templates
        return getProvider().subscribeForItems();
    }

    @Override
    public void onDestroy() {
        getProvider().onDestroy();
    }
}
