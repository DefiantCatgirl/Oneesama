package catgirl.oneesama.activity.chapterlist.fragments.chapterlist.presenter;

import catgirl.oneesama.data.controller.ChaptersController;
import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.data.ChapterListProvider;

public class ChapterListPresenterFactory {

    private ChapterListProvider listProvider;
    private ChaptersController chaptersController;

    public ChapterListPresenterFactory(ChapterListProvider listProvider, ChaptersController chaptersController) {
        this.listProvider = listProvider;
        this.chaptersController = chaptersController;
    }

    public ChapterListPresenter createPresenter(int tagId) {
        return new ChapterListPresenter(listProvider, chaptersController, tagId);
    }
}
