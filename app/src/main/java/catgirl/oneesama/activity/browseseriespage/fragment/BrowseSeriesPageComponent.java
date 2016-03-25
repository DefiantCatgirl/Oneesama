package catgirl.oneesama.activity.browseseriespage.fragment;

import catgirl.oneesama.activity.browseseriespage.fragment.presenter.BrowseSeriesPagePresenter;
import catgirl.oneesama.activity.browseseriespage.fragment.view.BrowseSeriesPageFragment;
import dagger.Subcomponent;

@Subcomponent(modules = {
        BrowseSeriesPageModule.class,
})
public interface BrowseSeriesPageComponent {
    void inject(BrowseSeriesPageFragment fragment);

    BrowseSeriesPagePresenter createPresenter();
}
