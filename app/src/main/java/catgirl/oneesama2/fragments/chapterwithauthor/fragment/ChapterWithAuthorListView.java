package catgirl.oneesama2.fragments.chapterwithauthor.fragment;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.List;

import catgirl.oneesama.ui.common.chapter.ChapterAuthor;

public interface ChapterWithAuthorListView {
    void showContents(@NonNull List<ChapterAuthor> chapters, @NonNull String seriesName);
    void showSeriesImage(@NonNull Bitmap seriesImage, boolean animateIn);
}
