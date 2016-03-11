package catgirl.oneesama.activity.main.fragments.browse.legacy.pages;

import android.view.ViewGroup;

import java.util.List;

import catgirl.oneesama.R;
import catgirl.oneesama.data.network.scraper.recentlist.DynastyRecentChapterPage;
import catgirl.oneesama.data.network.scraper.recentlist.DynastyRecentChapterPageProvider;

public class RecentChaptersPage extends CommonBrowsePage<DynastyRecentChapterPage.RecentChapter, RecentChaptersViewHolder> {

    static List<DynastyRecentChapterPage.RecentChapter> cache;

    @Override
    public List<DynastyRecentChapterPage.RecentChapter> getCache() {
        return cache;
    }

    @Override
    public void loadData() {
        try {
            cache = DynastyRecentChapterPageProvider.getProvider().provideSeriesPage().chapters;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RecentChaptersViewHolder provideViewHolder(ViewGroup parent) {
        return new RecentChaptersViewHolder(getActivity().getLayoutInflater().inflate(R.layout.item_recent_chapter, parent, false));
    }

    @Override
    public void bindViewHolder(RecentChaptersViewHolder holder, int position, DynastyRecentChapterPage.RecentChapter data) {
        holder.bind(data);
    }
}
