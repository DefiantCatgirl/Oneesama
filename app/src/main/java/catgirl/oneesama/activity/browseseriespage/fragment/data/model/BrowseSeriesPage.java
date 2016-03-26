package catgirl.oneesama.activity.browseseriespage.fragment.data.model;

import java.util.ArrayList;
import java.util.List;

public class BrowseSeriesPage {
    public String name;
    public String permalink;
    public String cover;
    public String description;

    // BrowseSeriesPageChapter or BrowseSeriesPageVolume
    public List<Object> objects = new ArrayList<>();

    public BrowseSeriesPage(String name, String permalink, String cover, String description) {
        this.name = name;
        this.permalink = permalink;
        this.cover = cover;
        this.description = description;
    }
}
