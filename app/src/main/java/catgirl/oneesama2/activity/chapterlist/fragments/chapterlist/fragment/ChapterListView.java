package catgirl.oneesama2.activity.chapterlist.fragments.chapterlist.fragment;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.List;

import catgirl.oneesama.ui.common.chapter.ChapterAuthor;

public interface ChapterListView {
    void showContents(@NonNull List<ChapterAuthor> chapters);
    void setSeriesTitle(String title);
    void setDisplayAuthor(boolean shouldDisplayAuthor);
    void showSeriesImage(@NonNull Bitmap seriesImage, boolean animateIn);
    void switchToReader(int id);
    void showDeleteConfirmation(int position);
    void showItemDeleted(int position);
}
