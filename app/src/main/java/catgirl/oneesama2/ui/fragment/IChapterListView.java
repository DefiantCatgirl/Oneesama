package catgirl.oneesama2.ui.fragment;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.List;

import catgirl.oneesama.model.chapter.ui.UiChapter;
import catgirl.oneesama.ui.common.chapter.ChapterAuthor;

public interface IChapterListView {
    void showContents(@NonNull List<ChapterAuthor> chapters, @NonNull String seriesName);
    void showSeriesImage(@NonNull Bitmap seriesImage, boolean animateIn);
}
