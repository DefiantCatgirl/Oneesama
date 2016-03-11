package catgirl.oneesama.activity.chapterlist.fragments.chapterlist.view;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.data.model.ChapterAuthor;
import catgirl.oneesama.activity.common.view.SimpleRecyclerView;

public interface ChapterListView extends SimpleRecyclerView<ChapterAuthor> {
    void setSeriesTitle(String title);
    void setDisplayAuthor(boolean shouldDisplayAuthor);
    void showSeriesImage(@NonNull Bitmap seriesImage, boolean animateIn);
    void switchToReader(int id);
    void showDeleteConfirmation(int position);
    void showItemDeleted(int position);
}
