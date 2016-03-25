package catgirl.oneesama.activity.browseseriespage;

import catgirl.oneesama.activity.browseseriespage.fragment.BrowseSeriesPageComponent;
import catgirl.oneesama.activity.browseseriespage.fragment.BrowseSeriesPageModule;
import dagger.Subcomponent;

@Subcomponent(modules = {
        BrowseSeriesPageActivityModule.class,
})
public interface BrowseSeriesPageActivityComponent {
    void inject(BrowseSeriesPageActivity activity);

    BrowseSeriesPageComponent plus(BrowseSeriesPageModule module);
}
