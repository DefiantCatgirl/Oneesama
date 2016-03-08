package catgirl.oneesama2.fragments.chapterwithauthor.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import catgirl.mvp.BasePresenter;
import catgirl.oneesama.Application;
import catgirl.oneesama.ui.common.chapter.ChapterAuthor;
import catgirl.oneesama2.fragments.chapterwithauthor.data.provider.ChapterWithAuthorListProvider;
import catgirl.oneesama2.fragments.chapterwithauthor.fragment.ChapterWithAuthorListView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChapterWithAuthorListPresenter extends BasePresenter<ChapterWithAuthorListView> {

    @Inject
    ChapterWithAuthorListProvider listProvider;

    Subscription subscription;

    List<ChapterAuthor> chapterAuthors;
    private final Integer tagId;

    public ChapterWithAuthorListPresenter(ChapterWithAuthorListProvider listProvider, int tagId) {
        this.listProvider = listProvider;
        this.tagId = tagId;
    }

    @Override
    public void onCreate(@Nullable Bundle bundle) {

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
    public void bindView(ChapterWithAuthorListView view) {
        super.bindView(view);
        if (chapterAuthors != null) {
            view.showContents(chapterAuthors, "Test");
        } else if (subscription == null) {
            subscription = listProvider
                    .getChapterAuthorList(tagId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(chapterAuthors -> {
                        this.chapterAuthors = chapterAuthors;
                        view.showContents(chapterAuthors, "Test");
                    });
        }
    }

    @Override
    public void unbindView() {
        super.unbindView();
    }

    @Override
    public ChapterWithAuthorListView getView() {
        return null;
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
        Toast.makeText(Application.getContextOfApplication(), "Item " + position + " clicked.", Toast.LENGTH_SHORT).show();
    }

    public void onItemDeleted(int position) {
        Toast.makeText(Application.getContextOfApplication(), "Item " + position + " deleted.", Toast.LENGTH_SHORT).show();
    }
}
