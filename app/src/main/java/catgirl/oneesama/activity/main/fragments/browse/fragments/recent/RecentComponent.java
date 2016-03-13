package catgirl.oneesama.activity.main.fragments.browse.fragments.recent;

import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.presenter.RecentPresenter;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.view.RecentFragment;
import dagger.Subcomponent;

@Subcomponent(modules = {
        RecentModule.class,
})
public interface RecentComponent {
    void inject(RecentFragment recentFragment);

    RecentPresenter getPresenter();
}
