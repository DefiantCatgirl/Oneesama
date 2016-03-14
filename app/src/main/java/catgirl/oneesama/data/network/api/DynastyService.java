package catgirl.oneesama.data.network.api;

import catgirl.oneesama.BuildConfig;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.model.RecentChapterPage;
import catgirl.oneesama.data.model.chapter.serializable.Chapter;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface DynastyService {
    @Headers({"User-Agent: Oneesama-" + BuildConfig.VERSION_NAME + "-Android"})
    @GET("/chapters/{chapter}.json")
    Observable<Chapter> getChapter(@Path("chapter") String chapter);

    @Headers({"User-Agent: Oneesama-" + BuildConfig.VERSION_NAME + "-Android"})
    @GET("/chapters/added.json")
    Observable<RecentChapterPage> getRecentPage(@Query("page") int page);
}
