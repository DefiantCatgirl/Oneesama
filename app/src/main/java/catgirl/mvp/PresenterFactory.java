package catgirl.mvp;

public interface PresenterFactory<P extends Presenter> {
    P createPresenter();
}
