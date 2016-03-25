package catgirl.oneesama.activity.browseseriespage.fragment;

import catgirl.oneesama.activity.browseseriespage.fragment.data.BrowseSeriesPageProvider;
import catgirl.oneesama.activity.browseseriespage.fragment.presenter.BrowseSeriesPagePresenter;
import catgirl.oneesama.data.network.api.DynastyService;
import dagger.Module;
import dagger.Provides;

@Module
public class BrowseSeriesPageModule {
    @Provides
    public BrowseSeriesPageProvider getProvider(DynastyService api) {
        return new BrowseSeriesPageProvider(api);
    }

    @Provides
    public BrowseSeriesPagePresenter getPresenter(BrowseSeriesPageProvider seriesPageProvider) {
        return new BrowseSeriesPagePresenter(seriesPageProvider);
    }
}
