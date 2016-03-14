package catgirl.oneesama.activity.main.fragments.ondevice.fragments.misc.view;

import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.data.model.ChapterAuthor;
import catgirl.oneesama.activity.common.view.SimpleRecyclerView;

public interface MiscChaptersView extends SimpleRecyclerView<ChapterAuthor> {
    void switchToReader(int id);
    void showDeleteConfirmation(int position);
    void showItemDeleted(int position);
}
