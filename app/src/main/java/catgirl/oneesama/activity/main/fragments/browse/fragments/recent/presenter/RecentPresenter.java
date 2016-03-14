 package catgirl.oneesama.activity.main.fragments.browse.fragments.recent.presenter;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import catgirl.mvp.BasePresenter;
import catgirl.oneesama.BuildConfig;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.RecentProvider;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.RecentToLocalProvider;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.model.RecentChapter;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.model.RecentChapterPage;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.view.RecentView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RecentPresenter extends BasePresenter<RecentView> {

    private RecentProvider recentProvider;
    private RecentToLocalProvider recentToLocalProvider;

    List<RecentChapter> items = new ArrayList<>();

    boolean finished = false;
    boolean errorShown = false;

    int currentPage = 0;

    static final int pageBatchSize = 1;

    Subscription moreItemsSubscription;
    Subscription newItemsSubscription;

    public RecentPresenter(RecentProvider recentProvider, RecentToLocalProvider recentToLocalProvider) {
        this.recentProvider = recentProvider;
        this.recentToLocalProvider = recentToLocalProvider;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        loadMore();

        recentToLocalProvider.setCurrentItems(items);
        recentToLocalProvider.subscribeForItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                recentChapters -> {
                    items = recentChapters;
                    if (getView() != null) {
                        getView().updateExistingItems(items);
                    }
                }
        );
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
        recentToLocalProvider.onDestroy();
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
                .map(
                        result -> {
                            // New items may have been added on the server, we need to avoid duplication

                            // TODO: WRITE A TEST FOR THIS; this is a perfect thing to test seriously
                            // TODO: that means figuring out how to test this, yes

                            if (result.chapters != null && !result.chapters.isEmpty()) {
                                for (int i = items.size() - 1; i >= 0; i--) {
                                    if (result.chapters.get(0).permalink.equals(items.get(i).permalink)) {
                                        int resultSize = result.chapters.size();
                                        for (int j = 0; j < Math.min(resultSize, items.size() - i); j++) {
                                            result.chapters.remove(0);
                                        }
                                        break;
                                    }
                                }
                                return new LoadMoreResult(result, true);
                            } else {
                                return new LoadMoreResult(result, false);
                            }
                        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loadMoreResult -> {
                            RecentChapterPage result = loadMoreResult.chapters;

                            moreItemsSubscription = null;
                            currentPage = result.currentPage;

                            // That odd case where every item had been filtered out so we received an empty array
                            // that is NOT the result of an empty page, which would mean we loaded everything.
                            // TODO: this is a bullshit way to handle this tbch
                            if (result.chapters != null && result.chapters.isEmpty() && loadMoreResult.hadItems) {
                                loadMore();
                                return;
                            }

                            if (result.totalPages <= result.currentPage || result.chapters == null || result.chapters.isEmpty()) {
                                finished = true;
                            }

                            if (result.chapters != null) {
                                items.addAll(result.chapters);
                                recentToLocalProvider.setCurrentItems(items);
                            }

                            if (getView() != null) {
                                getView().showMoreItems(result.chapters, finished);
                            }
                        },
                        error -> {
                            if (BuildConfig.DEBUG) {
                                error.printStackTrace();
                            }
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
        if (newItemsSubscription != null) {
            return;
        }

        newItemsSubscription = recentProvider.getNewChapters(items.isEmpty() ? null : items.get(0))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            items.addAll(0, result);
                            recentToLocalProvider.setCurrentItems(items);

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

     class LoadMoreResult {
         public RecentChapterPage chapters;
         public boolean hadItems;

         public LoadMoreResult(RecentChapterPage chapters, boolean hadItems) {
             this.chapters = chapters;
             this.hadItems = hadItems;
         }
     }
}
