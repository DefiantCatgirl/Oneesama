package catgirl.oneesama.activity.browseseriespage.fragment.data.model;

import catgirl.oneesama.data.model.chapter.ui.UiTag;

public class BrowseSeriesPageJsonTagging {
    public String header;

    public String title;
    public String permalink; // Using the existence of this field as the type identificator
    public UiTag[] tags;
}
