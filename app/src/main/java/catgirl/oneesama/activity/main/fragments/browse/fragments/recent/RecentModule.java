package catgirl.oneesama.activity.main.fragments.browse.fragments.recent;

import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.RecentProvider;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.presenter.RecentPresenter;
import catgirl.oneesama.data.network.api.DynastyService;
import dagger.Module;
import dagger.Provides;

@Module
public class RecentModule {
    @Provides
    public RecentPresenter provideRecentPresenter(RecentProvider recentProvider) {
        return new RecentPresenter(recentProvider);
    }

    @Provides
    public RecentProvider provideRecentProvider(DynastyService api) {
        return new RecentProvider(api);
    }
}
