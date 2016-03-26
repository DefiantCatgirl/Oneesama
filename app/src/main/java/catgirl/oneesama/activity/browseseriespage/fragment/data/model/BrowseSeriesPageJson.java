package catgirl.oneesama.activity.browseseriespage.fragment.data.model;

import java.util.List;

public class BrowseSeriesPageJson {
    public String name;
    public String permalink;
    public String cover;
    public String description;

    // BrowseSeriesPageChapter or BrowseSeriesPageVolume
    public List<BrowseSeriesPageJsonTagging> taggings;
}
