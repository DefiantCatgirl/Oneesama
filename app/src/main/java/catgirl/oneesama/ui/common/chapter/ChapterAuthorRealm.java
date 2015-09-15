package catgirl.oneesama.ui.common.chapter;

import catgirl.oneesama.model.chapter.serializable.Chapter;
import catgirl.oneesama.model.chapter.serializable.Tag;

public class ChapterAuthorRealm {
    public Chapter chapter;
    public Tag author;
    public ChapterAuthorRealm(Chapter chapter, Tag author) {
        this.chapter = chapter;
        this.author = author;
    }
}