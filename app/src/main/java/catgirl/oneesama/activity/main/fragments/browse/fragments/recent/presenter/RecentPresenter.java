 package catgirl.oneesama.activity.main.fragments.browse.fragments.recent.presenter;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import catgirl.mvp.BasePresenter;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.RecentProvider;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.model.RecentChapter;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.view.RecentView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

 public class RecentPresenter extends BasePresenter<RecentView> {

    private RecentProvider recentProvider;

    List<RecentChapter> items = new ArrayList<>();

    boolean finished = false;
    boolean errorShown = false;

    int currentPage = 0;

    static final int pageBatchSize = 1;

    Subscription moreItemsSubscription;
    Subscription newItemsSubscription;

    public RecentPresenter(RecentProvider recentProvider) {
        this.recentProvider = recentProvider;
    }

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
    public void bindView(RecentView view) {
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

    public RecentChapter getItem(int position) {
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

        moreItemsSubscription = recentProvider.getPages(currentPage + 1, pageBatchSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if (result.totalPages <= result.currentPage || result.chapters == null || result.chapters.isEmpty()) {
                                finished = true;
                            }

                            if (result.chapters != null) {
                                items.addAll(result.chapters);
                            }

                            moreItemsSubscription = null;

                            currentPage = result.currentPage;

                            if (getView() != null) {
                                getView().showMoreItems(result.chapters, finished);
                            }
                        },
                        error -> {
                            errorShown = true;

                            moreItemsSubscription = null;

                            if (getView() != null) {
                                getView().showMoreItemsError(true);
                            }
                        }
                );
    }

    public void itemClicked(int position) {
        if (getView() != null) {
            getView().loadChapter(items.get(position).permalink);
        }
    }

    public void loadNew() {
        if (getView() != null) {
            getView().showNewItems(new ArrayList<>());
        }
    }
}
