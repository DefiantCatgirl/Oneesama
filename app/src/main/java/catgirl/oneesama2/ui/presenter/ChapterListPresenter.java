package catgirl.oneesama2.ui.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import javax.inject.Inject;

import catgirl.mvp.BasePresenter;
import catgirl.oneesama.ui.common.chapter.ChapterAuthor;
import catgirl.oneesama2.ui.fragment.ChapterListView;

public class ChapterListPresenter extends BasePresenter<ChapterListView> {

    @Inject

    List<ChapterAuthor> chapters;
    private final String seriesId;

    public ChapterListPresenter(String seriesId) {
        this.seriesId = seriesId;
    }

    @Override
    public void onCreate(@Nullable Bundle bundle) {

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void bindView(ChapterListView view) {
        super.bindView(view);
        if (chapters != null)
            view.showContents(chapters, seriesId);
    }

    @Override
    public void unbindView() {

    }

    @Override
    public ChapterListView getView() {
        return null;
    }

    public ChapterAuthor getChapter(int position) {
        if (chapters == null)
            return null;
        else
            return chapters.get(position);
    }

    public int getChaptersCount() {
        if (chapters == null)
            return 0;
        else
            return chapters.size();
    }

    public void onItemClicked(int position) {

    }

    public void onItemDeleted(int position) {
        
    }
}
