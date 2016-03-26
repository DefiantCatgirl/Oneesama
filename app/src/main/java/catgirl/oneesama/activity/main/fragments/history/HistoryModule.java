package catgirl.oneesama.activity.main.fragments.history;

import catgirl.oneesama.activity.main.fragments.history.data.HistoryProvider;
import catgirl.oneesama.activity.main.fragments.history.presenter.HistoryPresenter;
import catgirl.oneesama.data.controller.ChaptersController;
import catgirl.oneesama.data.realm.RealmProvider;
import catgirl.oneesama.data.settings.RecentlyOpenedChapters;
import catgirl.oneesama.data.settings.SettingsProvider;
import dagger.Module;
import dagger.Provides;

@Module
public class HistoryModule {

    @Provides
    HistoryProvider provideHistoryProvider(RealmProvider realmProvider, SettingsProvider<RecentlyOpenedChapters> settingsProvider) {
        return new HistoryProvider(realmProvider, settingsProvider);
    }

    @Provides
    HistoryPresenter provideHistoryPresenter(HistoryProvider historyProvider, ChaptersController chaptersController) {
        return new HistoryPresenter(historyProvider, chaptersController);
    }

}
