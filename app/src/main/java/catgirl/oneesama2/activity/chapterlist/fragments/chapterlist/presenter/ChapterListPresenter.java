package catgirl.oneesama2.activity.chapterlist.fragments.chapterlist.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import catgirl.mvp.BasePresenter;
import catgirl.oneesama.controller.ChaptersController;
import catgirl.oneesama2.data.model.chapter.ui.UiTag;
import catgirl.oneesama.ui.common.chapter.ChapterAuthor;
import catgirl.oneesama2.activity.chapterlist.fragments.chapterlist.data.provider.ChapterListProvider;
import catgirl.oneesama2.activity.chapterlist.fragments.chapterlist.fragment.ChapterListView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChapterListPresenter extends BasePresenter<ChapterListView> {

    ChapterListProvider listProvider;
    ChaptersController chaptersController;

    Subscription subscription;

    List<ChapterAuthor> chapterAuthors;
    private final Integer tagId;

    boolean deleteConfirmationShown = false;
    int deleteConfirmationPosition;
    UiTag tag;

    public ChapterListPresenter(ChapterListProvider listProvider, ChaptersController chaptersController, int tagId) {
        this.chaptersController = chaptersController;
        this.listProvider = listProvider;
        this.tagId = tagId;
    }

    @Override
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        tag = listProvider.getTag(tagId);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {

    }

    @Override
    public void onDestroy() {
        subscription.unsubscribe();
        subscription = null;
    }

    @Override
    public void bindView(ChapterListView view) {
        super.bindView(view);

        view.setSeriesTitle(tag.getName());
        view.setDisplayAuthor(!tag.getType().equals(UiTag.SERIES));

        if (chapterAuthors != null) {
            view.showContents(chapterAuthors);
        } else if (subscription == null) {
            subscription = listProvider
                    .getChapterAuthorList(tagId, tag.getType().equals(UiTag.SERIES))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(chapterAuthors -> {
                        this.chapterAuthors = chapterAuthors;
                        if (getView() != null)
                            getView().showContents(chapterAuthors);
                    });
        }

        if (deleteConfirmationShown) {
            view.showDeleteConfirmation(deleteConfirmationPosition);
        }
    }

    @Override
    public void unbindView() {
        super.unbindView();
    }

    public ChapterAuthor getChapter(int position) {
        if (chapterAuthors == null)
            return null;
        else
            return chapterAuthors.get(position);
    }

    public int getChaptersCount() {
        if (chapterAuthors == null)
            return 0;
        else
            return chapterAuthors.size();
    }

    public void onItemClicked(int position) {
        if (getView() != null)
            getView().switchToReader(chapterAuthors.get(position).chapter.getId());
    }

    public void onItemDeleteClicked(int position) {
        if (getView() != null)
            getView().showDeleteConfirmation(position);

        deleteConfirmationShown = true;
        deleteConfirmationPosition = position;
    }

    public void onItemDeletionConfirmed(int position) {
        chaptersController.deleteChapter(chapterAuthors.get(position).chapter.getId());
        chapterAuthors.remove(position);
        if (getView() != null)
            getView().showItemDeleted(position);
        onItemDeletionDismissed();
    }

    public void onItemDeletionDismissed() {
        deleteConfirmationShown = false;
    }
}
