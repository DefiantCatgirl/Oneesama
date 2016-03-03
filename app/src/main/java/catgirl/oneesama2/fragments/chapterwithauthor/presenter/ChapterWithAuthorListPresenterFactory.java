package catgirl.oneesama2.fragments.chapterwithauthor.presenter;

public class ChapterWithAuthorListPresenterFactory {
    public ChapterWithAuthorListPresenter createPresenter(String tagId) {
        return new ChapterWithAuthorListPresenter(tagId);
    }
}
