package catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.model;

import java.util.List;

import catgirl.oneesama.data.model.chapter.ui.UiTag;

public class RecentChapter {
    public int id;
    public boolean isLocal = false;
    public String title;
    public String series;
    public String permalink;
    public List<UiTag> tags;
}
