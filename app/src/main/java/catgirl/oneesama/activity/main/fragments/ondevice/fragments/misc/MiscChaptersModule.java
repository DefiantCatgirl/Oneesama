package catgirl.oneesama.activity.main.fragments.ondevice.fragments.misc;

import catgirl.oneesama.data.controller.ChaptersController;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.misc.data.MiscChaptersProvider;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.misc.presenter.MiscChaptersPresenter;
import catgirl.oneesama.data.realm.RealmProvider;
import dagger.Module;
import dagger.Provides;

@Module
public class MiscChaptersModule {
    @Provides
    public MiscChaptersProvider getProvider(RealmProvider realmProvider) {
        return new MiscChaptersProvider(realmProvider);
    }

    @Provides
    public MiscChaptersPresenter getPresenter(MiscChaptersProvider listProvider, ChaptersController chaptersController) {
        return new MiscChaptersPresenter(listProvider, chaptersController);
    }
}
