package catgirl.oneesama.api;

import catgirl.oneesama.model.chapter.serializable.Chapter;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;
import rx.Observable;

public interface DynastyService {
    @Headers({"User-Agent: Oneesama-0.1-Android"})
    @GET("/chapters/{chapter}.json")
    Observable<Chapter> getChapter(@Path("chapter") String chapter);
}
