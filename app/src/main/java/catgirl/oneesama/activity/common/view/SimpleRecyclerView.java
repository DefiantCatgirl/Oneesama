package catgirl.oneesama.activity.common.view;

import android.support.annotation.NonNull;

import java.util.List;

public interface SimpleRecyclerView<T> {
    void showContents(@NonNull List<T> contents);
}
