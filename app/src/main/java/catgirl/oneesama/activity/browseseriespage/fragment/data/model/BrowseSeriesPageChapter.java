package catgirl.oneesama.activity.browseseriespage.fragment.data.model;

import catgirl.oneesama.data.model.chapter.ui.UiChapter;
import catgirl.oneesama.data.model.chapter.ui.UiTag;

public class BrowseSeriesPageChapter {
    public String title;
    public String permalink;
    public UiTag[] tags;
    public UiChapter chapter;

    public BrowseSeriesPageChapter(String title, String permalink, UiTag[] tags) {
        this.title = title;
        this.permalink = permalink;
        this.tags = tags;
    }
}
