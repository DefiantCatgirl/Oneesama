package catgirl.oneesama.data.network.scraper.chaptername;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import catgirl.oneesama.application.Config;
import catgirl.oneesama.data.network.scraper.DynastyPage;

public class DynastySeriesPageProvider {
    static Map<String, DynastySeriesPage> cache = new HashMap<>();

    public static DynastySeriesPage provideSeriesPage(String seriesId) throws Exception {

        DynastySeriesPage seriesPage = new DynastySeriesPage();

        String url = Config.apiEndpoint + "series/" + seriesId + ".json";

        JSONObject seriesObject = new JSONObject(DynastyPage.getBody(url));
        JSONArray taggings = seriesObject.getJSONArray("taggings");
        String currentVolume = null;

        for (int i = 0; i < taggings.length(); i++) {
            JSONObject tagging = taggings.getJSONObject(i);
            if (tagging.has("header")) {
                currentVolume = tagging.getString("header");
            }
            if (tagging.has("permalink")) {
                DynastySeriesPage.Chapter c = new DynastySeriesPage.Chapter();

                c.chapterId = tagging.getString("permalink");
                c.chapterName = tagging.getString("title");
                c.volumeName = currentVolume;

                seriesPage.chapters.add(c);
            }
        }

        return seriesPage;
    }

    public static DynastySeriesPage.Chapter provideChapterInfo(String seriesId, String chapterId) throws Exception {
        if(cache.containsKey(seriesId)) {
            Log.v("Debug", "Cached");
            DynastySeriesPage.Chapter c = cache.get(seriesId).getChapter(chapterId);
            if (c != null)
                return c;
        }

        DynastySeriesPage seriesPage = provideSeriesPage(seriesId);

        DynastySeriesPage.Chapter result = null;
        for(DynastySeriesPage.Chapter c : seriesPage.chapters) {
            if(c.chapterId.equals(chapterId)) {
                result = c;
                break;
            }
        }

        Log.v("Debug", "Put into cache");
        cache.put(seriesId, seriesPage);

        return result;
    }
}
