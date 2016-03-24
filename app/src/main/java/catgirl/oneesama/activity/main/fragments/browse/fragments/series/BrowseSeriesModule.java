package catgirl.oneesama.activity.main.fragments.browse.fragments.series;

import catgirl.oneesama.activity.main.fragments.browse.fragments.series.data.SeriesProvider;
import catgirl.oneesama.activity.main.fragments.browse.fragments.series.presenter.SeriesPresenter;
import catgirl.oneesama.data.network.api.DynastyService;
import dagger.Module;
import dagger.Provides;

@Module
public class BrowseSeriesModule {
    @Provides
    SeriesProvider provideSeriesProvider(DynastyService api) {
        return new SeriesProvider(api);
    }

    @Provides
    SeriesPresenter provideSeriesPresenter(SeriesProvider seriesProvider) {
        return new SeriesPresenter(seriesProvider);
    }
}
