package catgirl.oneesama.data.settings;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RecentlyOpenedChapters {
    Map<Integer, Long> openDates = new HashMap<>();

    public void refreshOpenDate(int chapterId) {
        openDates.put(chapterId, new Date().getTime());
    }

    public void deleteOpenDate(int chapterId) {
        openDates.remove(chapterId);
    }

    public long getOpenDate(int chapterId) {
        return openDates.containsKey(chapterId) ? openDates.get(chapterId) : 0;
    }
}
