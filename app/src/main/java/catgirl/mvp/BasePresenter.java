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
        this.view = view;
    }

    @Override
    public void unbindView() {

    }

    @Override
    public T getView() {
        return view;
    }
}
