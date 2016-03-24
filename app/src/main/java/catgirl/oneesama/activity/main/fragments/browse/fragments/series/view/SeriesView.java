package catgirl.oneesama.activity.main.fragments.browse.fragments.series.view;

import catgirl.oneesama.activity.common.view.LazyLoadView;
import catgirl.oneesama.activity.main.fragments.browse.fragments.series.data.model.SeriesItem;

public interface SeriesView extends LazyLoadView<SeriesItem> {
    void switchToSeries(String seriesPermalink, String title);
}
