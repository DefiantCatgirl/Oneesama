package catgirl.oneesama.ui.common.chapter;

import catgirl.oneesama.model.chapter.ui.UiChapter;
import catgirl.oneesama.model.chapter.ui.UiTag;

public class ChapterAuthor {
    public UiChapter chapter;
    public UiTag author;
    public ChapterAuthor(UiChapter chapter, UiTag author) {
        this.chapter = chapter;
        this.author = author;
    }
}