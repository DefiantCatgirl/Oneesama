package catgirl.oneesama.activity.main.fragments.ondevice.fragments.doujins.view;

import catgirl.oneesama.activity.common.view.SimpleRecyclerView;
import catgirl.oneesama.data.model.chapter.ui.UiTag;

public interface DoujinsView extends SimpleRecyclerView<UiTag> {
    void switchToChapterList(int tagId);
}
