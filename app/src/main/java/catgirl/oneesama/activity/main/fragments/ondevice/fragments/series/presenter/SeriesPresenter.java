package catgirl.oneesama.activity.main.fragments.ondevice.fragments.series.presenter;

import catgirl.oneesama.activity.common.presenter.AutoRefreshableRecyclerPresenter;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.series.data.SeriesProvider;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.series.data.model.SeriesAuthor;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.series.view.SeriesView;

public class SeriesPresenter extends AutoRefreshableRecyclerPresenter<SeriesAuthor, SeriesView, SeriesProvider> {

    private SeriesProvider seriesProvider;

    public SeriesPresenter(SeriesProvider seriesProvider) {
        this.seriesProvider = seriesProvider;
    }

    @Override
    public SeriesProvider getProvider() {
        return seriesProvider;
    }

    @Override
    public void onDestroy() {
        seriesProvider.onDestroy();
        super.onDestroy();
    }

    public void onItemClicked(int position) {
        if (getView() != null)
            getView().switchToChapterList(items.get(position).series.getId());
    }
}
