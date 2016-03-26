package catgirl.oneesama.activity.main.fragments.history.presenter;

import catgirl.oneesama.activity.common.presenter.AutoRefreshableRecyclerPresenter;
import catgirl.oneesama.activity.main.fragments.history.data.HistoryProvider;
import catgirl.oneesama.activity.main.fragments.history.view.HistoryView;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.misc.view.MiscChaptersView;
import catgirl.oneesama.data.controller.ChaptersController;
import catgirl.oneesama.data.model.chapter.ui.UiChapter;

public class HistoryPresenter extends AutoRefreshableRecyclerPresenter<UiChapter, HistoryView, HistoryProvider> {
    private HistoryProvider historyProvider;
    ChaptersController chaptersController;

    boolean deleteConfirmationShown = false;
    int deleteConfirmationPosition;

    public HistoryPresenter(HistoryProvider historyProvider, ChaptersController chaptersController) {
        this.historyProvider = historyProvider;
        this.chaptersController = chaptersController;
    }

    @Override
    public void bindView(HistoryView view) {
        super.bindView(view);

        if (deleteConfirmationShown) {
            view.showDeleteConfirmation(deleteConfirmationPosition);
        }
    }

    public void onItemClicked(int position) {
        if (getView() != null)
            getView().switchToReader(items.get(position).getId());
    }

    public void onItemDeleteClicked(int position) {
        if (getView() != null)
            getView().showDeleteConfirmation(position);

        deleteConfirmationShown = true;
        deleteConfirmationPosition = position;
    }

    public void onItemDeletionConfirmed(int position) {
        chaptersController.deleteChapter(items.get(position).getId());
        items.remove(position);
        if (getView() != null)
            getView().showItemDeleted(position);
        onItemDeletionDismissed();
    }

    public void onItemDeletionDismissed() {
        deleteConfirmationShown = false;
    }

    @Override
    public HistoryProvider getProvider() {
        return historyProvider;
    }

    public void onResume() {
        historyProvider.onDestroy();
        historyProvider.subscribeForItems();
    }
}
