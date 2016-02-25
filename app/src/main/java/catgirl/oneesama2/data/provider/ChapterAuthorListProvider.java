package catgirl.oneesama2.data.provider;

import java.util.List;

import catgirl.oneesama.ui.common.chapter.ChapterAuthor;
import rx.Observable;

public interface ChapterAuthorListProvider {
    Observable<List<ChapterAuthor>> getChapterAuthorList(String tagId);
}
