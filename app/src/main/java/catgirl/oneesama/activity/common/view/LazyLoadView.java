package catgirl.oneesama.activity.common.view;

import java.util.List;

import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.model.RecentChapter;

public interface LazyLoadView<T> {
    void showExistingItems(List<T> items, boolean finished);
    void showMoreItems(List<T> items, boolean finished);
    void showNewItems(List<T> items);
    void showLoadingNewItems();
    void hideLoadingNewItems();
    void showMoreItemsError(boolean showToast);
    void showNewItemsError();
    void showInitialState();
    void showLoadingMoreItems();
    void updateExistingItems(List<T> items);
}
