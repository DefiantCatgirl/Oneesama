package catgirl.oneesama.activity.browseseriespage.fragment;

import catgirl.oneesama.activity.browseseriespage.fragment.data.BrowseSeriesPageProvider;
import catgirl.oneesama.activity.browseseriespage.fragment.data.BrowseSeriesPageToLocalProvider;
import catgirl.oneesama.activity.browseseriespage.fragment.presenter.BrowseSeriesPagePresenter;
import catgirl.oneesama.data.network.api.DynastyService;
import catgirl.oneesama.data.realm.RealmProvider;
import dagger.Module;
import dagger.Provides;

@Module
public class BrowseSeriesPageModule {
    @Provides
    public BrowseSeriesPageProvider getProvider(DynastyService api) {
        return new BrowseSeriesPageProvider(api);
    }

    @Provides
    public BrowseSeriesPageToLocalProvider getToLocalProvider(RealmProvider realmProvider) {
        return new BrowseSeriesPageToLocalProvider(realmProvider);
    }

    @Provides
    public BrowseSeriesPagePresenter getPresenter(
            BrowseSeriesPageProvider seriesPageProvider,
            BrowseSeriesPageToLocalProvider toLocalProvider) {
        return new BrowseSeriesPagePresenter(seriesPageProvider, toLocalProvider);
    }
}
