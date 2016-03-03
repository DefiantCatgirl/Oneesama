package catgirl.mvp;

public interface ComponentPresenterCache {
    long generateId();
    <T> Presenter<T> getPresenter(long id);
    <T> void setPresenter(long id, Presenter<T> presenter);
    <C> C getComponent(long id);
    <C> void setComponent(long id, C component);
}
