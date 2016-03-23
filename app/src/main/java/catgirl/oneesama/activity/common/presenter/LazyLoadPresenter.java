package catgirl.oneesama.activity.common.presenter;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import catgirl.mvp.BasePresenter;
import catgirl.oneesama.BuildConfig;
import catgirl.oneesama.activity.common.data.model.LazyLoadResult;
import catgirl.oneesama.activity.common.view.LazyLoadView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

//TODO: write tests
//TODO: if you need "protected" access for anything but getting the item list - you're doing it wrong, refactor
public abstract class LazyLoadPresenter<Model, View extends LazyLoadView<Model>> extends BasePresenter<View> {

    protected List<Model> items = new ArrayList<>();

    protected boolean finished = false;
    protected boolean errorShown = false;

    protected Subscription moreItemsSubscription;
    protected Subscription newItemsSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        loadMore();
    }

    @Override
    public void onDestroy() {
        if (moreItemsSubscription != null) {
            moreItemsSubscription.unsubscribe();
            moreItemsSubscription = null;
        }
        if (newItemsSubscription != null) {
            newItemsSubscription.unsubscribe();
            newItemsSubscription = null;
        }
    }

    @Override
    public void bindView(View view) {
        super.bindView(view);

        if (!items.isEmpty() || finished) {
            view.showExistingItems(items, finished);
        } else {
            view.showInitialState();
        }

        if (newItemsSubscription != null) {
            view.showLoadingNewItems();
        }

        if (errorShown) {
            view.showMoreItemsError(false);
        }
    }

    public Model getItem(int position) {
        return items.get(position);
    }

    public int getItemCount() {
        return items.size();
    }

    public void loadMore() {
        if (moreItemsSubscription != null || finished)
            return;

        errorShown = false;

        if (getView() != null) {
            getView().showLoadingMoreItems();
        }

        moreItemsSubscription = getMoreChaptersObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            moreItemsSubscription = null;

                            items.addAll(result.elements);
                            onItemsUpdated();

                            finished = result.finished;

                            if (getView() != null) {
                                getView().showMoreItems(result.elements, finished);
                            }
                        },
                        error -> {
                            if (BuildConfig.DEBUG) {
                                error.printStackTrace();
                            }

                            moreItemsSubscription = null;
                            errorShown = true;

                            if (getView() != null) {
                                getView().showMoreItemsError(true);
                            }
                        }
                );
    }

    protected abstract Observable<LazyLoadResult<Model>> getMoreChaptersObservable();
    protected abstract Observable<List<Model>> getNewChaptersObservable();
    protected abstract void onItemsUpdated();
    protected abstract void itemClicked(int position);

    public void loadNew() {
        if (newItemsSubscription != null) {
            return;
        }

        newItemsSubscription = getNewChaptersObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            items.addAll(0, result);
                            onItemsUpdated();

                            newItemsSubscription = null;

                            if (getView() != null) {
                                getView().showNewItems(result);
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
