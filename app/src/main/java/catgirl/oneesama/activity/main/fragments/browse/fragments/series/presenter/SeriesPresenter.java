package catgirl.oneesama.activity.main.fragments.browse.fragments.series.presenter;

import java.util.List;

import catgirl.oneesama.activity.common.data.model.LazyLoadResult;
import catgirl.oneesama.activity.common.presenter.ReplaceOnRefreshPresenter;
import catgirl.oneesama.activity.main.fragments.browse.fragments.series.data.model.SeriesItem;
import catgirl.oneesama.activity.main.fragments.browse.fragments.series.data.SeriesProvider;
import catgirl.oneesama.activity.main.fragments.browse.fragments.series.view.SeriesView;
import rx.Observable;

public class SeriesPresenter extends ReplaceOnRefreshPresenter<SeriesItem, SeriesView> {

    private SeriesProvider seriesProvider;

    public SeriesPresenter(SeriesProvider seriesProvider) {
        this.seriesProvider = seriesProvider;
    }

    @Override
    protected Observable<LazyLoadResult<SeriesItem>> getMoreItemsObservable() {
        return seriesProvider.getMoreSeries();
    }

    @Override
    protected Observable<List<SeriesItem>> getNewItemsObservable() {
        return seriesProvider.getNewSeries();
    }

    @Override
    protected void onItemsUpdated() {

    }

    @Override
    public void itemClicked(int position) {
        if (getView() != null)
            getView().switchToSeries(items.get(position).permalink, items.get(position).name);
    }
}
