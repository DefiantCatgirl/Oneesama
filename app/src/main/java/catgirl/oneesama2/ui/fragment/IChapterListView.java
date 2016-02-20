package catgirl.oneesama2.ui.fragment;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.List;

import catgirl.oneesama.model.chapter.ui.UiChapter;

public interface IChapterListView {
    void showContents(@NonNull List<UiChapter> chapters, @NonNull String seriesName);
    void showSeriesImage(@NonNull Bitmap seriesImage, boolean animateIn);
}
