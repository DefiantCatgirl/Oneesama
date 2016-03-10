package catgirl.oneesama.ui.activity.main.browse.pages;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import catgirl.oneesama.scraper.recentlist.DynastyRecentChapterPage;
import catgirl.oneesama.ui.activity.main.MainActivity;

public class RecentChaptersViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.Item_Chapter_Title) TextView title;
    @Bind(R.id.Item_Chapter_AuthorAndSeries) TextView authorAndSeries;
    @Bind(R.id.Item_Chapter_Tags) TextView tags;

    DynastyRecentChapterPage.RecentChapter data;

    public RecentChaptersViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        itemView.setOnClickListener(v -> {
            if(data == null)
                return;

            Intent intent = new Intent(itemView.getContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(data.chapterId));
            itemView.getContext().startActivity(intent);
        });
    }

    public void bind(DynastyRecentChapterPage.RecentChapter chapter) {
        data = chapter;

        title.setText(chapter.chapterName);

        if(chapter.authorName == null && chapter.doujinName == null) {
            authorAndSeries.setVisibility(View.GONE);
        } else {
            authorAndSeries.setVisibility(View.VISIBLE);
            if(chapter.authorName != null && chapter.doujinName != null)
                authorAndSeries.setText(chapter.authorName + " - " + chapter.doujinName);
            else if(chapter.authorName != null)
                authorAndSeries.setText(chapter.authorName);
            else
                authorAndSeries.setText(chapter.doujinName);
        }

        tags.setText(StringUtils.join(chapter.tags, ",   "));
    }
}
