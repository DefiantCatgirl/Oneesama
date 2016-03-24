 package catgirl.oneesama.activity.main.fragments.browse.fragments.recent.presenter;

import android.os.Bundle;

import java.util.List;

import catgirl.oneesama.activity.common.data.model.LazyLoadResult;
import catgirl.oneesama.activity.common.presenter.LazyLoadPresenter;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.RecentProvider;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.RecentToLocalProvider;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.model.RecentChapter;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.view.RecentView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RecentPresenter extends LazyLoadPresenter<RecentChapter, RecentView> {

    private RecentProvider recentProvider;
    private RecentToLocalProvider recentToLocalProvider;

    public RecentPresenter(RecentProvider recentProvider, RecentToLocalProvider recentToLocalProvider) {
        this.recentProvider = recentProvider;
        this.recentToLocalProvider = recentToLocalProvider;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        recentToLocalProvider.onDestroy();
    }

    @Override
    protected Observable<LazyLoadResult<RecentChapter>> getMoreItemsObservable() {
        return recentProvider.getMoreChapters();
    }

    @Override
    protected Observable<List<RecentChapter>> getNewItemsObservable() {
        return recentProvider.getNewChapters();
    }

    @Override
    public void onItemsUpdated() {
        recentToLocalProvider.setCurrentItems(items);
    }

    @Override
    public void itemClicked(int position) {
        if (getView() != null) {
            getView().loadChapter(items.get(position).permalink);
        }
    }
}
