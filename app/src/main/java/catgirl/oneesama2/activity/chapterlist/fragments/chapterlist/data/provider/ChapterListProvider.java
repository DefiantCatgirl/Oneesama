package catgirl.oneesama2.activity.chapterlist.fragments.chapterlist.data.provider;

import java.util.List;

import catgirl.oneesama2.data.model.chapter.ui.UiTag;
import catgirl.oneesama.ui.common.chapter.ChapterAuthor;
import rx.Observable;

public interface ChapterListProvider {
    Observable<List<ChapterAuthor>> getChapterAuthorList(int tagId, boolean sortByVolumes);
    UiTag getTag(int tagId);
}
