package catgirl.oneesama.ui.activity.main.ondevice.pages;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import catgirl.oneesama2.data.model.chapter.serializable.Chapter;
import catgirl.oneesama2.data.model.chapter.serializable.Tag;
import catgirl.oneesama2.data.model.chapter.ui.UiChapter;
import catgirl.oneesama2.data.model.chapter.ui.UiTag;
import catgirl.oneesama.ui.common.CommonPage;
import catgirl.oneesama.ui.activity.main.ondevice.OnDeviceFragment;
import catgirl.oneesama.ui.common.chapter.ChapterAuthor;
import catgirl.oneesama.ui.common.chapter.ChapterViewHolder;
import io.realm.RealmResults;
import rx.Observable;

public class MiscPage extends CommonPage<ChapterAuthor, ChapterViewHolder> {

    @Override
    public List<ChapterAuthor> getDataSource() {
        RealmResults<Chapter> results = realm.allObjects(Chapter.class)
                .where()
                .not()
                .equalTo("tags.type", "Series")
                .not()
                .equalTo("tags.type", "Doujin")
                .findAllSorted("title");

        List<ChapterAuthor> result = new ArrayList<>();

        Observable.from(results)
                .map(chapter -> {
                    Tag tag = chapter.getTags()
                            .where()
                            .equalTo("type", "Author")
                            .findFirst();
                    if (tag == null) {
                        tag = new Tag();
                        tag.setName("No author");
                        tag.setType("Author");
                    }
                    return new ChapterAuthor(new UiChapter(chapter), new UiTag(tag));
                })
                .toList()
                .subscribe(result::addAll);

        return result;
    }

    @Override
    public int getDataItemCount() {
        return (int) realm.allObjects(Chapter.class)
                .where()
                .not()
                .equalTo("tags.type", "Series")
                .not()
                .equalTo("tags.type", "Doujin")
                .count();
    }

    @Override
    public ChapterViewHolder provideViewHolder(ViewGroup parent) {
        return new MiscChapterViewHolder(getActivity().getLayoutInflater().inflate(R.layout.item_chapter_author, parent, false), recycler);
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

        emptyMessage.findViewById(R.id.Common_Empty_BrowseButton)
                .setOnClickListener(button -> ((OnDeviceFragment.OnDeviceFragmentDelegate) getActivity()).onBrowseButtonPressed());

        ((TextView) emptyMessage.findViewById(R.id.Common_Empty_MessageText))
                .setText(R.string.page_misc_no_chapters);

        return emptyMessage;
    }

    public static class MiscChapterViewHolder extends ChapterViewHolder {
        @Bind(R.id.Item_Chapter_Author) TextView author;

        public MiscChapterViewHolder(View itemView, RecyclerView recycler) {
            super(itemView, recycler);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(int position, ChapterAuthor data) {
            super.bind(position, data);
            author.setText(data.author.getName());
        }

        @Override
        public void reset() {
            super.reset();
            author.setText("");
        }
    }

}
