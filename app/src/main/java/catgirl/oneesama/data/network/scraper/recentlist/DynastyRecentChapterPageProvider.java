package catgirl.oneesama.data.network.scraper.recentlist;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import catgirl.oneesama.application.Config;
import catgirl.oneesama.data.network.scraper.DynastyPage;
import rx.Observable;
import rx.schedulers.Schedulers;

public class DynastyRecentChapterPageProvider {

    // TODO: dagger
    private static DynastyRecentChapterPageProvider _provider;

    public static DynastyRecentChapterPageProvider getProvider() {
        if(_provider == null)
            _provider = new DynastyRecentChapterPageProvider();
        return _provider;
    }

    private DynastyRecentChapterPageProvider() {

    }

    public DynastyRecentChapterPage page;

    public DynastyRecentChapterPage provideSeriesPage() throws Throwable {

        DynastyRecentChapterPage seriesPage = new DynastyRecentChapterPage();
        final Throwable[] error = {null};

        List<Integer> numbers = Arrays.asList(1, 2, 3);
        Observable.from(numbers)
                .concatMapEager(integer -> Observable.fromCallable(() -> parseSinglePage(integer)).subscribeOn(Schedulers.io()))
                .toBlocking()
                .subscribe(seriesPage.chapters::addAll, e -> {
                    error[0] = e;
                });

        if(error[0] != null)
            throw error[0];

        page = seriesPage;

        return seriesPage;
    }

    private List<DynastyRecentChapterPage.RecentChapter> parseSinglePage(int pageNumber) throws Exception {
        String url = Config.apiEndpoint + "chapters/added.json?page=" + pageNumber;

        JSONObject recentChaptersObject = new JSONObject(DynastyPage.getBody(url));
        JSONArray chaptersArray = recentChaptersObject.getJSONArray("chapters");

        List<DynastyRecentChapterPage.RecentChapter> chapters = new ArrayList<>();

        String tagType;
        String tagName;

        for(int i = 0; i < chaptersArray.length(); i++) {
            JSONObject chapter = chaptersArray.getJSONObject(i);
            DynastyRecentChapterPage.RecentChapter c = new DynastyRecentChapterPage.RecentChapter();

            c.chapterId = chapter.getString("permalink");
            c.chapterName = chapter.getString("title");

            if (chapter.has("tags")) {
                JSONArray tagsArray = chapter.getJSONArray("tags");
                for (int j = 0; j < tagsArray.length(); j++) {
                    JSONObject tag = tagsArray.getJSONObject(j);
                    tagType = tag.getString("type");
                    tagName = tag.getString("name");
                    if (tagType.equals("Author")) {
                        c.authorName = tagName;
                    } else if (tagType.equals("Doujin")) {
                        c.doujinName = tagName;
                    } else if (tagType.equals("General")) {
                        c.tags.add(tagName);
                    }
                }
            }

            chapters.add(c);
        }

        return chapters;
    }
}
