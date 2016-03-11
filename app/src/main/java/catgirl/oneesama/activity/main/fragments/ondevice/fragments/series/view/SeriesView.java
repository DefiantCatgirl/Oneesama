package catgirl.oneesama.activity.main.fragments.ondevice.fragments.series.view;

import catgirl.oneesama.activity.common.view.SimpleRecyclerView;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.series.data.model.SeriesAuthor;

public interface SeriesView extends SimpleRecyclerView<SeriesAuthor> {
    void switchToChapterList(int tagId);
}
