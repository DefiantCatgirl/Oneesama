package catgirl.oneesama.activity.main.fragments.browse.fragments.recent.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.model.RecentChapter;
import catgirl.oneesama.data.model.chapter.ui.UiTag;

public class RecentViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.Item_Chapter_Title) TextView title;
    @Bind(R.id.Item_Chapter_AuthorAndSeries) TextView authorsAndDoujins;
    @Bind(R.id.Item_Chapter_Tags) TextView tags;

    private RecentViewHolderDelegate delegate;

    public RecentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this::onClick);
    }

    private void onClick(View view) {
        if (delegate != null)
            delegate.onClick();
    }

    public void bind(RecentChapter chapter, RecentViewHolderDelegate delegate) {
        this.delegate = delegate;

        title.setText(chapter.title);

        List<String> generalTags = new ArrayList<>();
        List<String> authorTags = new ArrayList<>();
        List<String> doujinTags = new ArrayList<>();

        for (UiTag tag : chapter.tags) {
            if (tag.getType().equals(UiTag.GENERAL)) {
                generalTags.add(tag.getName());
            } else if (tag.getType().equals(UiTag.AUTHOR)) {
                authorTags.add(tag.getName());
            } else if (tag.getType().equals(UiTag.DOUJIN)) {
                doujinTags.add(tag.getName());
            }
        }

        if (!generalTags.isEmpty()) {
            tags.setVisibility(View.VISIBLE);
            tags.setText(StringUtils.join(generalTags, ",   "));
        } else {
            tags.setVisibility(View.GONE);
        }

        if (authorTags.isEmpty() && doujinTags.isEmpty()) {
            authorsAndDoujins.setVisibility(View.GONE);
        } else {
            authorsAndDoujins.setVisibility(View.VISIBLE);

            String authorList = StringUtils.join(authorTags, ", ");
            String doujinList = StringUtils.join(doujinTags, ", ") + " Doujin";

            if(authorTags.isEmpty()) {
                authorsAndDoujins.setText(doujinList);
            } else if (doujinTags.isEmpty()) {
                authorsAndDoujins.setText(authorList);
            } else {
                authorsAndDoujins.setText(authorList + " - " + doujinList);
            }
        }
    }

    public interface RecentViewHolderDelegate {
        void onClick();
    }
}
