package catgirl.oneesama.activity.browseseriespage.fragment.view;

import java.util.List;

import catgirl.oneesama.activity.browseseriespage.fragment.data.model.BrowseSeriesPageChapter;

public interface BrowseSeriesPageView {
    void setTitle(String title);
    void loadCover(String url);

    void showContents(List<Object> chapters);
    void showError();
    void showLoading();

    void updateExistingItems(List<Object> chapters);
}
