package catgirl.oneesama.activity.browseseriespage.fragment.data;

import catgirl.oneesama.activity.browseseriespage.fragment.data.model.BrowseSeriesPage;
import catgirl.oneesama.activity.browseseriespage.fragment.data.model.BrowseSeriesPageChapter;
import catgirl.oneesama.activity.browseseriespage.fragment.data.model.BrowseSeriesPageJsonTagging;
import catgirl.oneesama.activity.browseseriespage.fragment.data.model.BrowseSeriesPageVolume;
import catgirl.oneesama.data.network.api.DynastyService;
import rx.Observable;

public class BrowseSeriesPageProvider {
    private DynastyService api;

    public BrowseSeriesPageProvider(DynastyService api) {
        this.api = api;
    }

    public Observable<BrowseSeriesPage> getSeriesPage(String permalink) {
        return api.getSeriesPage(permalink)
                .map(browseSeriesPageJson -> {
                    BrowseSeriesPage seriesPage = new BrowseSeriesPage(
                            browseSeriesPageJson.name,
                            browseSeriesPageJson.permalink,
                            browseSeriesPageJson.cover,
                            browseSeriesPageJson.description);

                    for (BrowseSeriesPageJsonTagging tagging : browseSeriesPageJson.taggings) {
                        if (tagging.permalink == null) {
                            seriesPage.objects.add(new BrowseSeriesPageVolume(tagging.header));
                        } else {
                            seriesPage.objects.add(new BrowseSeriesPageChapter(
                                    tagging.title, tagging.permalink, tagging.tags));
                        }
                    }

                    return seriesPage;
                });
    }
}
