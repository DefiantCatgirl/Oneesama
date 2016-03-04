package catgirl.oneesama2.fragments.chapterwithauthor.data.provider;

import java.util.List;

import catgirl.oneesama.ui.common.chapter.ChapterAuthor;
import rx.Observable;

public interface ChapterWithAuthorListProvider {
    Observable<List<ChapterAuthor>> getChapterAuthorList(int tagId);
}
