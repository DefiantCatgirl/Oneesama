package catgirl.oneesama.activity.browseseriespage.fragment.presenter;

import catgirl.mvp.implementations.BasePresenter;
import catgirl.oneesama.activity.browseseriespage.fragment.data.BrowseSeriesPageProvider;
import catgirl.oneesama.activity.browseseriespage.fragment.data.model.BrowseSeriesPage;
import catgirl.oneesama.activity.browseseriespage.fragment.view.BrowseSeriesPageView;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.model.RecentChapter;
import catgirl.oneesama.application.Config;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Action2;

public class BrowseSeriesPagePresenter extends BasePresenter<BrowseSeriesPageView> {

    private BrowseSeriesPageProvider seriesPageProvider;
    private String permalink;
    private String title;

    BrowseSeriesPage seriesPage;

    Subscription subscription;

    public BrowseSeriesPagePresenter(BrowseSeriesPageProvider seriesPageProvider) {
        this.seriesPageProvider = seriesPageProvider;
    }

    public void configure(String permalink, String title) {
        this.permalink = permalink;
        this.title = title;

        requestData();
    }

    public void requestData() {
        if (subscription != null)
            return;

        subscription = seriesPageProvider.getSeriesPage(permalink).subscribe(
                result -> {
                    seriesPage = result;

                    if (getView() != null) {
                        getView().setTitle(seriesPage.name);
                        getView().loadCover(Config.apiEndpoint + seriesPage.cover);
                    }
                },
                error -> {

                }
        );
    }

    @Override
    public void bindView(BrowseSeriesPageView view) {
        super.bindView(view);

        if (seriesPage == null) {
            if (getView() != null)
                getView().setTitle(title);
        } else {
            if (getView() != null) {
                getView().setTitle(seriesPage.name);
                getView().loadCover(Config.apiEndpoint + seriesPage.cover);
            }
        }
    }

    public RecentChapter getItem(int position) {
        return null;
    }
}
