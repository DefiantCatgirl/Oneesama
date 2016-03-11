package catgirl.oneesama.activity.main.fragments.ondevice.fragments.misc.presenter;

import catgirl.oneesama.data.controller.ChaptersController;
import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.data.model.ChapterAuthor;
import catgirl.oneesama.activity.common.presenter.AutoRefreshableRecyclerPresenter;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.misc.data.MiscChaptersProvider;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.misc.view.MiscChaptersView;

public class MiscChaptersPresenter extends AutoRefreshableRecyclerPresenter<ChapterAuthor, MiscChaptersView, MiscChaptersProvider> {

    MiscChaptersProvider listProvider;
    ChaptersController chaptersController;

    boolean deleteConfirmationShown = false;
    int deleteConfirmationPosition;

    public MiscChaptersPresenter(MiscChaptersProvider listProvider, ChaptersController chaptersController) {
        this.chaptersController = chaptersController;
        this.listProvider = listProvider;
    }

    @Override
    public MiscChaptersProvider getProvider() {
        return listProvider;
    }

    public void onItemClicked(int position) {
        if (getView() != null)
            getView().switchToReader(items.get(position).chapter.getId());
    }

    public void onItemDeleteClicked(int position) {
        if (getView() != null)
            getView().showDeleteConfirmation(position);

        deleteConfirmationShown = true;
        deleteConfirmationPosition = position;
    }

    public void onItemDeletionConfirmed(int position) {
        chaptersController.deleteChapter(items.get(position).chapter.getId());
        items.remove(position);
        if (getView() != null)
            getView().showItemDeleted(position);
        onItemDeletionDismissed();
    }

    public void onItemDeletionDismissed() {
        deleteConfirmationShown = false;
    }
}
