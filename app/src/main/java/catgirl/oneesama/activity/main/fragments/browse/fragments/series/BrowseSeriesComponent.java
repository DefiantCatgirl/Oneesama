package catgirl.oneesama.activity.main.fragments.browse.fragments.series;

import catgirl.oneesama.activity.main.fragments.browse.fragments.series.presenter.SeriesPresenter;
import catgirl.oneesama.activity.main.fragments.browse.fragments.series.view.SeriesFragment;
import dagger.Subcomponent;

@Subcomponent(modules = {
        BrowseSeriesModule.class,
})
public interface BrowseSeriesComponent {
    void inject(SeriesFragment fragment);

    SeriesPresenter getPresenter();
}
