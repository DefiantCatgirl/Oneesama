package catgirl.oneesama.activity.main.fragments.browse.fragments.recent.view;

import java.util.List;

import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.model.RecentChapter;

public interface RecentView {
    void showExistingItems(List<RecentChapter> items, boolean finished);
    void showMoreItems(List<RecentChapter> items, boolean finished);
    void showNewItems(List<RecentChapter> items);
    void showLoadingNewItems();
    void showMoreItemsError(boolean showToast);
    void showNewItemsError();
    void showInitialState();
    void loadChapter(String permalink);
    void showLoadingMoreItems();
    void updateExistingItems(List<RecentChapter> items);
}
