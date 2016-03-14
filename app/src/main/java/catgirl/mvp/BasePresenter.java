package catgirl.mvp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class BasePresenter<T> implements Presenter<T> {
    private T view;

    @Override
    public void onCreate(@Nullable Bundle bundle) {

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void bindView(T view) {
        if (this.view != null)
            throw new IllegalStateException("Old view still bound");

        this.view = view;
    }

    @Override
    public void unbindView() {
        this.view = null;
    }

    @Override
    public T getView() {
        return view;
    }
}
