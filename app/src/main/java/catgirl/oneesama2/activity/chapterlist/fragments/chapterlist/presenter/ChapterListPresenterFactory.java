package catgirl.oneesama2.activity.chapterlist.fragments.chapterlist.presenter;

import catgirl.oneesama.controller.ChaptersController;
import catgirl.oneesama2.activity.chapterlist.fragments.chapterlist.data.provider.ChapterListProvider;

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
