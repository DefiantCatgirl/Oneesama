package catgirl.oneesama.activity.main;

import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.RecentComponent;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.RecentModule;
import catgirl.oneesama.activity.main.fragments.browse.fragments.series.BrowseSeriesComponent;
import catgirl.oneesama.activity.main.fragments.browse.fragments.series.BrowseSeriesModule;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.doujins.DoujinsComponent;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.doujins.DoujinsModule;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.misc.MiscChaptersComponent;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.misc.MiscChaptersModule;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.series.SeriesComponent;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.series.SeriesModule;
import dagger.Subcomponent;

@Subcomponent(modules = {
        MainActivityModule.class,
})
public interface MainActivityComponent {
    void inject(MainActivity activity);

    // On device
    SeriesComponent plus(SeriesModule module);
    DoujinsComponent plus(DoujinsModule module);
    MiscChaptersComponent plus(MiscChaptersModule module);

    // Browse
    RecentComponent plus(RecentModule module);
    BrowseSeriesComponent plus(BrowseSeriesModule browseSeriesModule);
}
