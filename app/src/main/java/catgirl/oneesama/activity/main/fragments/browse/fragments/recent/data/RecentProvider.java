package catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data;

import java.util.ArrayList;
import java.util.List;

import catgirl.oneesama.activity.common.data.model.LazyLoadResult;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.model.RecentChapter;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.model.RecentChapterPage;
import catgirl.oneesama.data.network.api.DynastyService;
import rx.Observable;
import rx.schedulers.Schedulers;

public class RecentProvider {

    private DynastyService api;

    private List<RecentChapter> cache = new ArrayList<>();
    private int currentPage = 0;

    public RecentProvider(DynastyService api) {
        this.api = api;
    }

    public Observable<LazyLoadResult<RecentChapter>> getMoreChapters() {
        return Observable.fromCallable(() -> {
            final RecentChapterPage combinedResult = new RecentChapterPage();
            final Throwable[] error = {null};
            int requestPage = currentPage + 1;

            while (combinedResult.chapters.isEmpty()) {
                api.getRecentPage(requestPage)
                        .toBlocking()
                        .subscribe(
                                chapterPage -> {
                                    combinedResult.chapters.addAll(parseResult(chapterPage.chapters, cache));
                                    combinedResult.totalPages = chapterPage.totalPages;
                                    combinedResult.currentPage = chapterPage.currentPage;
                                },
                                e -> error[0] = e
                        );

                if(error[0] != null) {
                    throw new RuntimeException(error[0]);
                }

                if (combinedResult.currentPage >= combinedResult.totalPages) {
                    break;
                }

                requestPage++;
            }

            boolean finished = (combinedResult.currentPage >= combinedResult.totalPages || combinedResult.chapters.isEmpty());
            LazyLoadResult<RecentChapter> result = new LazyLoadResult<>(combinedResult.chapters, finished);
            currentPage = combinedResult.currentPage;
            cache.addAll(combinedResult.chapters);

            return result;
        });
    }

    // TODO: TEST
    public static List<RecentChapter> parseResult(List<RecentChapter> newChapters, List<RecentChapter> cache) {
        List<RecentChapter> result = new ArrayList<>(newChapters);

        for (int i = cache.size() - 1; i >= 0; i--) {
            if (result.get(0).permalink.equals(cache.get(i).permalink)) {
                int resultSize = result.size();
                for (int j = 0; j < Math.min(resultSize, cache.size() - i); j++) {
                    result.remove(0);
                }
                break;
            }
        }
        return result;
    }

    // TODO: TEST
    public Observable<List<RecentChapter>> getNewChapters() {
        return Observable.fromCallable(() -> {
            List<RecentChapter> chapters = new ArrayList<>();

            int i = 1;
            final boolean[] running = {true};
            while (running[0]) {
                api.getRecentPage(i)
                        .toBlocking()
                        .subscribe(
                                result -> {
                                    if (result.chapters == null || result.chapters.isEmpty()) {
                                        running[0] = false;
                                    } else {
                                        if (cache.isEmpty()) {
                                            chapters.addAll(result.chapters);
                                            running[0] = false;
                                            return;
                                        }

                                        for (RecentChapter chapter : result.chapters) {
                                            if (!chapter.permalink.equals(cache.get(0).permalink)) {
                                                chapters.add(chapter);
                                            } else {
                                                running[0] = false;
                                                return;
                                            }
                                        }
                                    }
                                }, error -> {
                                    throw new RuntimeException(error);
                                }
                        );
                i++;
            }

            cache.addAll(0, chapters);
            return chapters;
        });
    }
}
