package catgirl.oneesama.activity.main.fragments.history.view;

import catgirl.oneesama.activity.common.view.SimpleRecyclerView;
import catgirl.oneesama.data.model.chapter.ui.UiChapter;

public interface HistoryView extends SimpleRecyclerView<UiChapter> {
    void switchToReader(int id);
    void showDeleteConfirmation(int position);
    void showItemDeleted(int position);
}
