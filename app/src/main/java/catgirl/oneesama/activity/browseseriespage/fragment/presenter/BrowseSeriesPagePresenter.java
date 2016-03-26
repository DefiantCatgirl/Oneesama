package catgirl.oneesama.activity.browseseriespage.fragment.presenter;

import android.os.Bundle;

import java.util.ArrayList;

import catgirl.mvp.implementations.BasePresenter;
import catgirl.oneesama.BuildConfig;
import catgirl.oneesama.activity.browseseriespage.fragment.data.BrowseSeriesPageProvider;
import catgirl.oneesama.activity.browseseriespage.fragment.data.BrowseSeriesPageToLocalProvider;
import catgirl.oneesama.activity.browseseriespage.fragment.data.model.BrowseSeriesPage;
import catgirl.oneesama.activity.browseseriespage.fragment.view.BrowseSeriesPageView;
import catgirl.oneesama.application.Config;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BrowseSeriesPagePresenter extends BasePresenter<BrowseSeriesPageView> {

    private BrowseSeriesPageProvider seriesPageProvider;
    private BrowseSeriesPageToLocalProvider toLocalProvider;

    private String permalink;
    private String title;

    BrowseSeriesPage seriesPage;

    Subscription subscription;

    boolean errorShown = false;

    public BrowseSeriesPagePresenter(BrowseSeriesPageProvider seriesPageProvider, BrowseSeriesPageToLocalProvider toLocalProvider) {
        this.seriesPageProvider = seriesPageProvider;
        this.toLocalProvider = toLocalProvider;
    }

    public void configure(String permalink, String title) {
        this.permalink = permalink;
        this.title = title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toLocalProvider.setCurrentItems(new ArrayList<>());
        toLocalProvider.subscribeForItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        withLocalChapters -> {
                            if (seriesPage != null) {
                                seriesPage.objects = withLocalChapters;
                                if (getView() != null) {
                                    getView().updateExistingItems(seriesPage.objects);
                                }
                            }
                        }
                );
    }

    @Override
    public void onDestroy() {
        toLocalProvider.onDestroy();
    }


    public void requestData() {
        if (subscription != null)
            return;

        errorShown = false;
        getView().showLoading();

        subscription = seriesPageProvider.getSeriesPage(permalink)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            seriesPage = result;

                            if (getView() != null) {
                                getView().setTitle(seriesPage.name);
                                getView().loadCover(Config.apiEndpoint + seriesPage.cover);
                                getView().showContents(seriesPage.objects);
                            }

                            toLocalProvider.setCurrentItems(seriesPage.objects);

                            subscription = null;
                        },
                        error -> {
                            if (BuildConfig.DEBUG)
                                error.printStackTrace();

                            errorShown = true;
                            if (getView() != null)
                                getView().showError();

                            subscription = null;
                        }
                );
    }

    @Override
    public void bindView(BrowseSeriesPageView view) {
        super.bindView(view);

        if (seriesPage == null) {
            if (getView() != null) {
                getView().setTitle(title);
                if (!errorShown) {
                    getView().showLoading();
                } else {
                    getView().showError();
                }
            }
            requestData();
        } else {
            if (getView() != null) {
                getView().showContents(seriesPage.objects);
                getView().setTitle(seriesPage.name);
                getView().loadCover(Config.apiEndpoint + seriesPage.cover);
            }
        }
    }

    public Object getItem(int position) {
        return seriesPage.objects.get(position);
    }

    public int getItemCount() {
        if (seriesPage == null)
            return 0;

        return seriesPage.objects.size();
    }
}
