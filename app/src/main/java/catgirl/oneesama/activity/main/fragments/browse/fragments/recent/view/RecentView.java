package catgirl.oneesama.activity.main.fragments.browse.fragments.recent.view;

import catgirl.oneesama.activity.common.view.LazyLoadView;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.model.RecentChapter;

public interface RecentView extends LazyLoadView<RecentChapter> {
    void loadChapter(String permalink);
}
