package catgirl.oneesama.data.settings;

public interface SettingsProvider<T> {
    void commit(T model);
    T retrieve();
}
