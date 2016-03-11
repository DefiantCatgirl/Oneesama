package catgirl.oneesama.data.network.scraper.recentlist;

import java.util.ArrayList;
import java.util.List;

public class DynastyRecentChapterPage {

    public List<RecentChapter> chapters = new ArrayList<>();

    public static class RecentChapter {
        public String chapterId;
        public String chapterName;
        public String authorName;
        public String doujinName;
        public List<String> tags = new ArrayList<>();
    }
}
