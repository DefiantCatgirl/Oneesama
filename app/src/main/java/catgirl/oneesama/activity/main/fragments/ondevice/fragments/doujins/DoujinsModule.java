package catgirl.oneesama.activity.main.fragments.ondevice.fragments.doujins;

import catgirl.oneesama.activity.main.fragments.ondevice.fragments.doujins.data.DoujinsProvider;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.doujins.presenter.DoujinsPresenter;
import catgirl.oneesama.data.realm.RealmProvider;
import dagger.Module;
import dagger.Provides;

@Module
public class DoujinsModule {
    @Provides
    public DoujinsProvider provideSeriesProvider(RealmProvider realmProvider) {
        return new DoujinsProvider(realmProvider);
    }

    @Provides
    public DoujinsPresenter getSeriesPresenter(DoujinsProvider doujinsProvider) {
        return new DoujinsPresenter(doujinsProvider);
    }
}
