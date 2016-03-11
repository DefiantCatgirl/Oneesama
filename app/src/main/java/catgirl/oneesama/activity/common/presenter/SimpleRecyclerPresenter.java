package catgirl.oneesama.activity.common.presenter;

import java.util.List;

import catgirl.mvp.BasePresenter;
import catgirl.oneesama.activity.common.view.SimpleRecyclerView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Simple presenter that loads some data and tells the view, assuming it implements SimpleRecyclerView, to display it.
 * Basically a class with boilerplate subscription management code.
 * @param <T> View model class
 * @param <V> View class
 */
public abstract class SimpleRecyclerPresenter<T, V extends SimpleRecyclerView<T>> extends BasePresenter<V> {

    public List<T> items;
    private Subscription subscription;

    @Override
    public void onDestroy() {
        subscription.unsubscribe();
        subscription = null;
        super.onDestroy();
    }

    @Override
    public void bindView(V view) {
        super.bindView(view);

        if (items != null) {
            view.showContents(items);
        } else if (subscription == null) {
            subscription = getItemObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(items -> {
                        this.items = items;
                        if (getView() != null)
                            getView().showContents(items);
                    });
        }
    }

    public T getItem(int position) {
        if (items == null)
            return null;
        else
            return items.get(position);
    }

    public int getItemsCount() {
        if (items == null)
            return 0;
        else
            return items.size();
    }

    public abstract Observable<List<T>> getItemObservable();
}
