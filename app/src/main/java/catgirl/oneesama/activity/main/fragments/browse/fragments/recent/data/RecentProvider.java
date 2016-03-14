package catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data;

import java.util.ArrayList;
import java.util.List;

import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.model.RecentChapter;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.model.RecentChapterPage;
import catgirl.oneesama.data.network.api.DynastyService;
import rx.Observable;
import rx.schedulers.Schedulers;

public class RecentProvider {

    private DynastyService api;

    public RecentProvider(DynastyService api) {
        this.api = api;
    }

    public Observable<RecentChapterPage> getPages(int offset, int count) {
        return Observable.fromCallable(() -> {
            RecentChapterPage result = new RecentChapterPage();
            final Throwable[] error = {null};

            List<Integer> numbers = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                numbers.add(offset + i);
            }

            Observable.from(numbers)
                    .concatMapEager(api::getRecentPage)
                    .toBlocking()
                    .subscribe(chapterPage -> {
                        if (result.chapters == null) {
                            result.chapters = chapterPage.chapters;
                        } else if (chapterPage.chapters != null) {
                            result.chapters.addAll(chapterPage.chapters);
                        }
                        result.currentPage = chapterPage.currentPage;
                        result.totalPages = chapterPage.totalPages;
                    }, e -> error[0] = e);

            // Technically the last pages could return a very valid 404 so only throw up if we didn't go that far.
            if(error[0] != null && !(result.totalPages > 0 && result.currentPage >= result.totalPages))
                throw new RuntimeException(error[0]);

            return result;
        });
    }

    // TODO: TEST
    public Observable<List<RecentChapter>> getNewChapters(RecentChapter topChapter) {
        return Observable.fromCallable(() -> {
            List<RecentChapter> chapters = new ArrayList<>();

            int i = 1;
            final boolean[] running = {true};
            while (running[0]) {
                api.getRecentPage(i)
                        .subscribeOn(Schedulers.immediate())
                        .observeOn(Schedulers.immediate())
                        .toBlocking()
                        .subscribe(
                                result -> {
                                    if (result.chapters == null || result.chapters.isEmpty()) {
                                        running[0] = false;
                                    } else {
                                        // If no top chapter was supplied just return whatever we got
                                        if (topChapter == null) {
                                            chapters.addAll(result.chapters);
                                            running[0] = false;
                                            return;
                                        }

                                        for (RecentChapter chapter : result.chapters) {
                                            if (!chapter.permalink.equals(topChapter.permalink)) {
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

            return chapters;
        });
    }
}
