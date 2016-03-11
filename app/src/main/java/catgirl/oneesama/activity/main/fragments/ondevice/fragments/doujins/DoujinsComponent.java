package catgirl.oneesama.activity.main.fragments.ondevice.fragments.doujins;

import catgirl.oneesama.activity.main.fragments.ondevice.fragments.doujins.presenter.DoujinsPresenter;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.doujins.view.DoujinsFragment;
import dagger.Subcomponent;

@Subcomponent(modules = {
        DoujinsModule.class,
})
public interface DoujinsComponent {
    void inject(DoujinsFragment doujinsFragment);

    DoujinsPresenter getPresenter();
}
