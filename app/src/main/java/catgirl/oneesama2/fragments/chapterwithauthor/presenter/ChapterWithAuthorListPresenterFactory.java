package catgirl.oneesama2.fragments.chapterwithauthor.presenter;

import catgirl.oneesama2.fragments.chapterwithauthor.data.provider.ChapterWithAuthorListProvider;

public class ChapterWithAuthorListPresenterFactory {

    private ChapterWithAuthorListProvider listProvider;

    public ChapterWithAuthorListPresenterFactory(ChapterWithAuthorListProvider listProvider) {
        this.listProvider = listProvider;
    }

    public ChapterWithAuthorListPresenter createPresenter(int tagId) {
        return new ChapterWithAuthorListPresenter(listProvider, tagId);
    }
}
