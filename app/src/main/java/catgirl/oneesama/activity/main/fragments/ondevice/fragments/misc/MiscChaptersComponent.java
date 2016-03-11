package catgirl.oneesama.activity.main.fragments.ondevice.fragments.misc;

import catgirl.oneesama.activity.main.fragments.ondevice.fragments.misc.presenter.MiscChaptersPresenter;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.misc.view.MiscChaptersFragment;
import dagger.Subcomponent;

@Subcomponent(modules = {
        MiscChaptersModule.class,
})
public interface MiscChaptersComponent {
    void inject(MiscChaptersFragment fragment);

    MiscChaptersPresenter getPresenter();
}
