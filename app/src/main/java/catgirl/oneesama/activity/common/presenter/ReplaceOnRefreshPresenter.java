package catgirl.oneesama.activity.common.presenter;

import catgirl.oneesama.BuildConfig;
import catgirl.oneesama.activity.common.view.LazyLoadView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class ReplaceOnRefreshPresenter<Model, View extends LazyLoadView<Model>> extends LazyLoadPresenter<Model, View> {
    @Override
    public void loadNew() {
        if (newItemsSubscription != null) {
            return;
        }

        newItemsSubscription = getNewItemsObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            items.clear();
                            items.addAll(0, result);
                            onItemsUpdated();

                            newItemsSubscription = null;

                            if (getView() != null) {
                                getView().hideLoadingNewItems();
                                getView().showExistingItems(items, finished);
                            }
                        }, error -> {
                            if (BuildConfig.DEBUG) {
                                error.printStackTrace();
                            }

                            newItemsSubscription = null;

                            if (getView() != null) {
                                getView().showNewItemsError();
                            }
                        }
                );
    }
}
