package catgirl.oneesama.data.network.api;

import catgirl.oneesama.BuildConfig;
import catgirl.oneesama.data.model.chapter.serializable.Chapter;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;
import rx.Observable;

public interface DynastyService {
    @Headers({"User-Agent: Oneesama-" + BuildConfig.VERSION_NAME + "-Android"})
    @GET("/chapters/{chapter}.json")
    Observable<Chapter> getChapter(@Path("chapter") String chapter);
}
