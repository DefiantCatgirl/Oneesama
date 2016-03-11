package catgirl.oneesama.activity.main.fragments.ondevice.fragments.series;

import catgirl.oneesama.activity.main.fragments.ondevice.fragments.series.presenter.SeriesPresenter;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.series.view.SeriesFragment;
import dagger.Subcomponent;

@Subcomponent(modules = {
        SeriesModule.class,
})
public interface SeriesComponent {
    void inject(SeriesFragment seriesFragment);

    SeriesPresenter getPresenter();
}
