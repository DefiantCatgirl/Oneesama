package catgirl.oneesama.ui.activity.chapters;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import catgirl.oneesama.R;
import catgirl.oneesama.model.chapter.serializable.Chapter;
import catgirl.oneesama.model.chapter.ui.UiChapter;
import catgirl.oneesama.tools.NaturalOrderComparator;
import catgirl.oneesama.tools.RealmObservable;
import catgirl.oneesama.ui.common.CommonPage;
import catgirl.oneesama.ui.common.chapter.ChapterAuthor;
import catgirl.oneesama.ui.common.chapter.ChapterViewHolder;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;

public class SeriesChaptersFragment extends CommonPage<ChapterAuthor, ChapterViewHolder> {

    @Override
    public List<ChapterAuthor> getDataSource() {
        RealmResults<Chapter> results = realm.allObjects(Chapter.class)
                .where()
                .equalTo("tags.id", getArguments().getInt("TAG_ID"))
                .findAll();

        List<ChapterAuthor> result = new ArrayList<>();

        Observable.from(results)
                .map(chapter -> new ChapterAuthor(new UiChapter(chapter), null))
                .toList()
                .subscribe(result::addAll);

        Collections.sort(result, (lhs, rhs) -> {
            if (lhs.chapter.getVolumeName() == null && rhs.chapter.getVolumeName() != null)
                return 1;
            if (rhs.chapter.getVolumeName() == null && lhs.chapter.getVolumeName() != null)
                return -1;
            int r = 0;
            if (lhs.chapter.getVolumeName() != null)
                r = new NaturalOrderComparator().compare(lhs.chapter.getVolumeName(), rhs.chapter.getVolumeName());
            if (r != 0)
                return r;
            return new NaturalOrderComparator().compare(lhs.chapter.getTitle(), rhs.chapter.getTitle());
        });
        return result;
    }

    @Override
    public int getDataItemCount() {
        return (int) realm.allObjects(Chapter.class)
                .where()
                .equalTo("tags.id", getArguments().getInt("TAG_ID"))
                .count();
    }

    @Override
    public ChapterViewHolder provideViewHolder(ViewGroup parent) {
        return new ChapterViewHolder(getActivity().getLayoutInflater().inflate(R.layout.item_chapter_inner, parent, false), recycler) {
            @Override
            public void bind(int position, ChapterAuthor data) {
                super.bind(position, data);
                setTitle(title, data);
            }
        };
    }

    public void setTitle(TextView title, ChapterAuthor data) {
        String tagName = getArguments().getString("TAG_NAME");
        if(tagName != null && data.chapter.getTitle().startsWith(tagName))
            title.setText(data.chapter.getTitle().substring(tagName.length()));
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
                .setText(R.string.activity_chapters_no_chapters);

        return emptyMessage;
    }

}
