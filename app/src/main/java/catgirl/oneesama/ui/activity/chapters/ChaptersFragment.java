package catgirl.oneesama.ui.activity.chapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import catgirl.oneesama.R;
import catgirl.oneesama.model.chapter.serializable.Chapter;
import catgirl.oneesama.model.chapter.serializable.Tag;
import catgirl.oneesama.model.chapter.ui.UiChapter;
import catgirl.oneesama.model.chapter.ui.UiTag;
import catgirl.oneesama.tools.RealmObservable;
import catgirl.oneesama.ui.activity.main.ondevice.pages.MiscPage;
import catgirl.oneesama.ui.activity.main.ondevice.pages.SeriesPage;
import catgirl.oneesama.ui.common.CommonPage;
import catgirl.oneesama.ui.common.chapter.ChapterAuthor;
import catgirl.oneesama.ui.common.chapter.ChapterViewHolder;
import io.realm.RealmResults;
import rx.Observable;

public class ChaptersFragment extends CommonPage<ChapterAuthor, ChapterViewHolder> {

    List<ChapterAuthor> cache = null;

    @Override
    public List<ChapterAuthor> getDataSource() {
        RealmResults<Chapter> results = realm.allObjects(Chapter.class)
                .where()
                .equalTo("tags.id", getArguments().getInt("TAG_ID"))
                .findAllSorted("title");

        List<ChapterAuthor> result = new ArrayList<>();

        Observable.from(results)
                .map(chapter -> new ChapterAuthor(new UiChapter(chapter), new UiTag(chapter.getTags()
                        .where()
                        .equalTo("type", "Author")
                        .findFirst())))
                .toList()
                .subscribe(result::addAll);

        return result;
    }

    public int getDataItemCount() {
        return (int) realm.allObjects(Chapter.class)
                .where()
                .equalTo("tags.id", getArguments().getInt("TAG_ID"))
                .count();
    }


    @Override
    public ChapterViewHolder provideViewHolder(ViewGroup parent) {
        return new MiscPage.MiscChapterViewHolder(getActivity().getLayoutInflater().inflate(R.layout.item_chapter_author, parent, false), recycler) {
            @Override
            public void bind(int position, ChapterAuthor data) {
                super.bind(position, data);
                setTitle(title, data);
            }
        };
    }

    public void setTitle(TextView title, ChapterAuthor data) {
        String tagName = getArguments().getString("TAG_NAME");
        title.setText(data.chapter.getLongTitle());
        if (tagName != null && data.chapter.getLongTitle().startsWith(tagName))
            title.setText(data.chapter.getLongTitle().substring(tagName.length()));
    }

    @Override
    public void bindViewHolder(ChapterViewHolder holder, int position, ChapterAuthor data) {
        holder.bind(position, data);
    }

    @Override
    public void resetViewHolder(ChapterViewHolder holder, int position) {
        holder.reset();
    }

    @Override
    public View getEmptyMessage(ViewGroup parent) {

        View emptyMessage = getActivity().getLayoutInflater().inflate(R.layout.common_empty_browse, parent, false);

        Button button = (Button) emptyMessage.findViewById(R.id.Common_Empty_BrowseButton);

        button.setOnClickListener(b -> getActivity().onBackPressed());
        button.setText(R.string.activity_chapters_go_back);

        ((TextView) emptyMessage.findViewById(R.id.Common_Empty_MessageText))
                .setText(R.string.activity_chapters_no_comics);

        return emptyMessage;
    }
}
