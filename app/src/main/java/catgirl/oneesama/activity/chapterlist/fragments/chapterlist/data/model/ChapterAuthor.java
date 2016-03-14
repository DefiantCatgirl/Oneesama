package catgirl.oneesama.activity.chapterlist.fragments.chapterlist.data.model;

import catgirl.oneesama.data.model.chapter.ui.UiChapter;
import catgirl.oneesama.data.model.chapter.ui.UiTag;

public class ChapterAuthor {
    public UiChapter chapter;
    public UiTag author;
    public ChapterAuthor(UiChapter chapter, UiTag author) {
        this.chapter = chapter;
        this.author = author;
    }
}
