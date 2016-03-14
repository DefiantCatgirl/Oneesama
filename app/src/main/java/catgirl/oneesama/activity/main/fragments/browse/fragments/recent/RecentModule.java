package catgirl.oneesama.activity.main.fragments.browse.fragments.recent;

import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.RecentProvider;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.RecentToLocalProvider;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.presenter.RecentPresenter;
import catgirl.oneesama.data.network.api.DynastyService;
import catgirl.oneesama.data.realm.RealmProvider;
import dagger.Module;
import dagger.Provides;

@Module
public class RecentModule {
    @Provides
    public RecentPresenter provideRecentPresenter(RecentProvider recentProvider, RecentToLocalProvider recentToLocalProvider) {
        return new RecentPresenter(recentProvider, recentToLocalProvider);
    }

    @Provides
    public RecentProvider provideRecentProvider(DynastyService api) {
        return new RecentProvider(api);
    }

    @Provides
    public RecentToLocalProvider provideRecentToLocalProvider(RealmProvider realmProvider) {
        return new RecentToLocalProvider(realmProvider);
    }
}
