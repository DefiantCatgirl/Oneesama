package catgirl.oneesama.activity.main.fragments.history;

import catgirl.oneesama.activity.main.fragments.history.presenter.HistoryPresenter;
import catgirl.oneesama.activity.main.fragments.history.view.HistoryFragment;
import dagger.Subcomponent;

@Subcomponent(modules = {
        HistoryModule.class,
})
public interface HistoryComponent {
    void inject(HistoryFragment fragment);

    HistoryPresenter createPresenter();
}
