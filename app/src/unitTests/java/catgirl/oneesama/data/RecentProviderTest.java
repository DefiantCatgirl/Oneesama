package catgirl.oneesama.data;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.RecentProvider;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.model.RecentChapter;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.model.RecentChapterPage;
import catgirl.oneesama.activity.main.fragments.browse.fragments.series.data.model.SeriesItem;
import catgirl.oneesama.data.model.chapter.serializable.Chapter;
import catgirl.oneesama.data.network.api.DynastyService;
import rx.Observable;
import rx.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;

public class RecentProviderTest {

    @Test
    public void parseResult_shouldNotMakeChangesWhenNoIntersection() {
        List<RecentChapter> cache = new ArrayList<>();
        List<RecentChapter> newChapters = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            RecentChapter chapter = new RecentChapter();
            chapter.permalink = Integer.toString(i);
            cache.add(chapter);
        }

        for (int i = 10; i < 19; i++) {
            RecentChapter chapter = new RecentChapter();
            chapter.permalink = Integer.toString(i);
            newChapters.add(chapter);
        }

        List<RecentChapter> results = RecentProvider.parseResult(newChapters, cache);

        assertEquals(newChapters.size(), results.size());

        int i = 10;
        for (RecentChapter result : results) {
            assertEquals(Integer.toString(i), result.permalink);
            i++;
        }
    }

    @Test
    public void parseResult_shouldDeleteBeginningWhenIntersection() {
        List<RecentChapter> cache = new ArrayList<>();
        List<RecentChapter> newChapters = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            RecentChapter chapter = new RecentChapter();
            chapter.permalink = Integer.toString(i);
            cache.add(chapter);
        }

        for (int i = 7; i < 17; i++) {
            RecentChapter chapter = new RecentChapter();
            chapter.permalink = Integer.toString(i);
            newChapters.add(chapter);
        }

        List<RecentChapter> results = RecentProvider.parseResult(newChapters, cache);

        assertEquals(7, results.size());

        int i = 10;
        for (RecentChapter result : results) {
            assertEquals(Integer.toString(i), result.permalink);
            i++;
        }
    }

    @Test
    public void parseResult_shouldDeleteAllWhenContains() {
        List<RecentChapter> cache = new ArrayList<>();
        List<RecentChapter> newChapters = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            RecentChapter chapter = new RecentChapter();
            chapter.permalink = Integer.toString(i);
            cache.add(chapter);
        }

        for (int i = 7; i < 17; i++) {
            RecentChapter chapter = new RecentChapter();
            chapter.permalink = Integer.toString(i);
            newChapters.add(chapter);
        }

        List<RecentChapter> results = RecentProvider.parseResult(newChapters, cache);

        assertEquals(0, results.size());
    }

    // TODO: should maybe have different mocks for different tests?
    public DynastyService provideDynastyService() {
        return new DynastyService() {
            @Override
            public Observable<Chapter> getChapter(String chapter) {
                return null;
            }

            @Override
            public Observable<RecentChapterPage> getRecentPage(int page) {
                if (page <= 0)
                    throw new RuntimeException("Pages start from 1 on Dynasty");
                else if (page == 1) {
                    // Initial set of pages
                    RecentChapterPage chapterPage = new RecentChapterPage();
                    chapterPage.totalPages = 2;
                    chapterPage.currentPage = 1;
                    for (int i = 0; i < 20; i++) {
                        RecentChapter recentChapter = new RecentChapter();
                        recentChapter.permalink = Integer.toString(i);
                        chapterPage.chapters.add(recentChapter);
                    }
                    return Observable.just(chapterPage);
                } else if (page == 2) {
                    // All items are existing items (e.g. there were a lot of new chapters added above)
                    RecentChapterPage chapterPage = new RecentChapterPage();
                    chapterPage.totalPages = 3;
                    chapterPage.currentPage = 2;
                    for (int i = 5; i < 15; i++) {
                        RecentChapter recentChapter = new RecentChapter();
                        recentChapter.permalink = Integer.toString(i);
                        chapterPage.chapters.add(recentChapter);
                    }
                    return Observable.just(chapterPage);
                } else if (page == 3) {
                    // Some of the items are new items
                    RecentChapterPage chapterPage = new RecentChapterPage();
                    chapterPage.totalPages = 4;
                    chapterPage.currentPage = 3;
                    for (int i = 15; i < 25; i++) {
                        RecentChapter recentChapter = new RecentChapter();
                        recentChapter.permalink = Integer.toString(i);
                        chapterPage.chapters.add(recentChapter);
                    }
                    return Observable.just(chapterPage);
                } else if (page == 4) {
                    // All of the items are new items
                    RecentChapterPage chapterPage = new RecentChapterPage();
                    chapterPage.totalPages = 4;
                    chapterPage.currentPage = 4;
                    for (int i = 25; i < 35; i++) {
                        RecentChapter recentChapter = new RecentChapter();
                        recentChapter.permalink = Integer.toString(i);
                        chapterPage.chapters.add(recentChapter);
                    }
                    return Observable.just(chapterPage);
                }
                return null;
            }

            @Override
            public Observable<Map<String, SeriesItem[]>> getAllSeries() {
                return null;
            }
        };
    }

    @Test
    public void getMoreChapters_shouldReturnAllPage1Items() {
        RecentProvider provider = new RecentProvider(provideDynastyService());

        provider.getMoreChapters()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(result -> {
                    assertEquals(false, result.finished);
                    assertEquals(20, result.elements.size());
                    for (int i = 0; i < 20; i++) {
                        assertEquals(Integer.toString(i), result.elements.get(i).permalink);
                    }
                });
    }

    @Test
    public void getMoreChapters_shouldReturnNoPage2ItemsSomePage3Items() {
        RecentProvider provider = new RecentProvider(provideDynastyService());

        provider.getMoreChapters()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe();

        provider.getMoreChapters()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(result -> {
                    assertEquals(false, result.finished);
                    assertEquals(5, result.elements.size());
                    for (int i = 0; i < 5; i++) {
                        assertEquals(Integer.toString(i + 20), result.elements.get(i).permalink);
                    }
                });
    }

    @Test
    public void getMoreChapters_shouldReturnAllPage4ItemsAndFinish() {
        RecentProvider provider = new RecentProvider(provideDynastyService());

        provider.getMoreChapters()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe();

        provider.getMoreChapters()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe();

        provider.getMoreChapters()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(result -> {
                    assertEquals(true, result.finished);
                    assertEquals(10, result.elements.size());
                    for (int i = 0; i < 10; i++) {
                        assertEquals(Integer.toString(i + 25), result.elements.get(i).permalink);
                    }
                });
    }

    public DynastyService provideNewChaptersDynastyService() {
        return new DynastyService() {
            public int requestCount = 0;
            @Override
            public Observable<Chapter> getChapter(String chapter) {
                return null;
            }

            @Override
            public Observable<RecentChapterPage> getRecentPage(int page) {
                RecentChapterPage chapterPage = new RecentChapterPage();
                chapterPage.currentPage = 1;
                chapterPage.totalPages = 5;

                if (page == 2) {
                    for (int i = 10; i < 20; i++) {
                        // requestCount == 3 should ask for more items, some of them are new
                        RecentChapter recentChapter = new RecentChapter();
                        recentChapter.permalink = Integer.toString(i);
                        chapterPage.chapters.add(recentChapter);
                    }
                } else if (requestCount == 0) {
                    // Initial set of items
                    for (int i = 20; i < 30; i++) {
                        RecentChapter recentChapter = new RecentChapter();
                        recentChapter.permalink = Integer.toString(i);
                        chapterPage.chapters.add(recentChapter);
                    }
                } else if (requestCount == 1) {
                    // No new items added
                    for (int i = 20; i < 30; i++) {
                        RecentChapter recentChapter = new RecentChapter();
                        recentChapter.permalink = Integer.toString(i);
                        chapterPage.chapters.add(recentChapter);
                    }
                } else if (requestCount == 2) {
                    // Some new items added
                    for (int i = 15; i < 25; i++) {
                        RecentChapter recentChapter = new RecentChapter();
                        recentChapter.permalink = Integer.toString(i);
                        chapterPage.chapters.add(recentChapter);
                    }
                } else if (requestCount == 3) {
                    // More new items added than fits on the page, should ask for page 2
                    for (int i = 0; i < 10; i++) {
                        RecentChapter recentChapter = new RecentChapter();
                        recentChapter.permalink = Integer.toString(i);
                        chapterPage.chapters.add(recentChapter);
                    }
                }

                requestCount++;
                return Observable.just(chapterPage);
            }

            @Override
            public Observable<Map<String, SeriesItem[]>> getAllSeries() {
                return null;
            }
        };
    }

    @Test
    public void getNewChapters_shouldGetNoChanges() {
        RecentProvider provider = new RecentProvider(provideNewChaptersDynastyService());

        provider.getMoreChapters()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe();

        provider.getNewChapters()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(recentChapters -> {
                    assertEquals(0, recentChapters.size());
                });
    }

    @Test
    public void getNewChapters_shouldGetSomeChanges() {
        RecentProvider provider = new RecentProvider(provideNewChaptersDynastyService());

        provider.getMoreChapters()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe();

        provider.getNewChapters()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe();

        provider.getNewChapters()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(recentChapters -> {
                    assertEquals(5, recentChapters.size());
                    for (int i = 0; i < 5; i++) {
                        assertEquals(Integer.toString(i + 15), recentChapters.get(i).permalink);
                    }
                });
    }

    @Test
    public void getNewChapters_shouldGetSeveralPagesWorthOfChanges() {
        RecentProvider provider = new RecentProvider(provideNewChaptersDynastyService());

        provider.getMoreChapters()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe();

        provider.getNewChapters()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe();

        provider.getNewChapters()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe();

        provider.getNewChapters()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(recentChapters -> {
                    assertEquals(15, recentChapters.size());
                    for (int i = 0; i < 15; i++) {
                        assertEquals(Integer.toString(i), recentChapters.get(i).permalink);
                    }
                });
    }

}
