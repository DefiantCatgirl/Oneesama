package catgirl.oneesama.activity.browseseriespage.fragment.data;

import catgirl.oneesama.activity.browseseriespage.fragment.data.model.BrowseSeriesPage;
import catgirl.oneesama.data.network.api.DynastyService;
import rx.Observable;

public class BrowseSeriesPageProvider {
    private DynastyService api;

    public BrowseSeriesPageProvider(DynastyService api) {
        this.api = api;
    }

    public Observable<BrowseSeriesPage> getSeriesPage(String permalink) {
        return api.getSeriesPage(permalink);
    }
}
