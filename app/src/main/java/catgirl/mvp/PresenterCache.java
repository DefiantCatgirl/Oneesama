package catgirl.mvp;

public interface PresenterCache {
    long generateId();
    <T> Presenter<T> getPresenter(Long id);
    <T> void setPresenter(Long id, Presenter<T> presenter);
}
