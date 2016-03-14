package catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RecentChapterPage {
    @SerializedName("current_page")
    public int currentPage = 0;

    @SerializedName("total_pages")
    public int totalPages = 0;

    public List<RecentChapter> chapters = new ArrayList<>();
}
