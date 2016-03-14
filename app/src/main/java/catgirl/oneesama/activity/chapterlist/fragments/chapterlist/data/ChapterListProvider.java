package catgirl.oneesama.activity.chapterlist.fragments.chapterlist.data;

import java.util.List;

import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.data.model.ChapterAuthor;
import catgirl.oneesama.data.model.chapter.ui.UiTag;
import rx.Observable;

public interface ChapterListProvider {
    Observable<List<ChapterAuthor>> getChapterAuthorList(int tagId, boolean sortByVolumes);
    UiTag getTag(int tagId);
}
