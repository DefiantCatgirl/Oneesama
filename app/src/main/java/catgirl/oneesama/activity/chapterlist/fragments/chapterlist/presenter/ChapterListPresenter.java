package catgirl.oneesama.activity.chapterlist.fragments.chapterlist.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.data.ChapterListProvider;
import catgirl.oneesama.application.Config;
import catgirl.oneesama.data.controller.ChaptersController;
import catgirl.oneesama.activity.common.presenter.SimpleRecyclerPresenter;
import catgirl.oneesama.data.model.chapter.ui.UiTag;
import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.data.model.ChapterAuthor;
import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.view.ChapterListView;
import rx.Observable;

public class ChapterListPresenter extends SimpleRecyclerPresenter<ChapterAuthor, ChapterListView> {

    ChapterListProvider listProvider;
    ChaptersController chaptersController;

    private final Integer tagId;

    boolean deleteConfirmationShown = false;
    int deleteConfirmationPosition;
    UiTag tag;

    boolean isDoujinsPage;

    public ChapterListPresenter(ChapterListProvider listProvider, ChaptersController chaptersController, int tagId) {
        this.chaptersController = chaptersController;
        this.listProvider = listProvider;
        this.tagId = tagId;
    }

    @Override
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        tag = listProvider.getTag(tagId);
        isDoujinsPage = !tag.getType().equals(UiTag.SERIES);
    }

    @Override
    public void bindView(ChapterListView view) {
        super.bindView(view);

        view.setSeriesTitle(tag.getName());
        view.setIsDoujinsPage(isDoujinsPage);

        if (deleteConfirmationShown) {
            view.showDeleteConfirmation(deleteConfirmationPosition);
        }
    }

    @Override
    public void unbindView() {
        super.unbindView();
    }

    @Override
    public Observable<List<ChapterAuthor>> getItemObservable() {
        listProvider.setTagId(tagId, tag.getType().equals(UiTag.SERIES));
        return listProvider.subscribeForItems();
    }

    @Override
    public void onDestroy() {
        listProvider.onDestroy();
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

    public void onBrowseClicked() {
        if (isDoujinsPage) {
            getView().openUrl(Config.apiEndpoint + "doujins/" + tag.getPermalink());
        } else {
            getView().switchToBrowseSeriesPage(tag.getPermalink(), tag.getName());
        }
    }
}
