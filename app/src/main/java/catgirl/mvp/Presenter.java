package catgirl.mvp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Inspired by and largely copied from <a href="https://github.com/grandstaish/compartment">Compartment</a> by Bradley Campbell
 * @param <T>
 */
public interface Presenter<T> {
    void onCreate(@Nullable Bundle bundle);

    void onSaveInstanceState(@NonNull Bundle bundle);

    void onDestroy();

    void bindView(T view);

    void unbindView();

    T getView();
}