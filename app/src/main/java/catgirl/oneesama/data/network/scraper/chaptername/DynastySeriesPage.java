package catgirl.oneesama.data.network.scraper.chaptername;

import java.util.ArrayList;
import java.util.List;

public class DynastySeriesPage {

    public List<Chapter> chapters = new ArrayList<>();

    public Chapter getChapter(String chapterId) {
        for(Chapter c : chapters)
            if(c.chapterId.equals(chapterId))
                return c;
        return null;
    }

    public static class Chapter {
        public String chapterId;
        public String chapterName;
        public String volumeName;
    }
}
