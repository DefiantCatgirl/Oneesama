package catgirl.oneesama.activity.main.fragments.ondevice.fragments.series;

import catgirl.oneesama.activity.main.fragments.ondevice.fragments.series.data.SeriesProvider;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.series.presenter.SeriesPresenter;
import catgirl.oneesama.data.realm.RealmProvider;
import dagger.Module;
import dagger.Provides;

@Module
public class SeriesModule {
    @Provides
    public SeriesProvider provideSeriesProvider(RealmProvider realmProvider) {
        return new SeriesProvider(realmProvider);
    }

    @Provides
    public SeriesPresenter getSeriesPresenter(SeriesProvider seriesProvider) {
        return new SeriesPresenter(seriesProvider);
    }
}
