package catgirl.oneesama.ui.activity.main.ondevice.pages;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import catgirl.oneesama.model.chapter.serializable.Chapter;
import catgirl.oneesama.model.chapter.serializable.Tag;
import catgirl.oneesama.model.chapter.ui.UiChapter;
import catgirl.oneesama.model.chapter.ui.UiTag;
import catgirl.oneesama.tools.RealmObservable;
import catgirl.oneesama.ui.common.CommonPage;
import catgirl.oneesama.ui.activity.main.ondevice.OnDeviceFragment;
import catgirl.oneesama.ui.common.chapter.ChapterAuthor;
import catgirl.oneesama.ui.common.chapter.ChapterAuthorRealm;
import catgirl.oneesama.ui.common.chapter.ChapterViewHolder;
import rx.Observable;

public class MiscPage extends CommonPage<ChapterAuthor, ChapterAuthorRealm, ChapterViewHolder> {

    @Override
    public Observable<ChapterAuthorRealm> getDataSource(int id) {
        return RealmObservable.object(getActivity(), realm1 -> {
            Chapter chapter = realm1.allObjects(Chapter.class)
                    .where()
                    .not()
                    .equalTo("tags.type", "Series")
                    .not()
                    .equalTo("tags.type", "Doujin")
                    .findAllSorted("title")
                    .get(id);

            Tag author = chapter.getTags()
                    .where()
                    .equalTo("type", "Author")
                    .findFirst();

            return new ChapterAuthorRealm(chapter, author);
        });
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
    public ChapterAuthor convertDataFromRealm(ChapterAuthorRealm source) {
        return new ChapterAuthor(
                new UiChapter(source.chapter),
                new UiTag(source.author));
    }

    @Override
    public ChapterViewHolder provideViewHolder(ViewGroup parent) {
        return new MiscChapterViewHolder(getActivity().getLayoutInflater().inflate(R.layout.item_chapter_author, parent, false));
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

        public MiscChapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(int id, ChapterAuthor data) {
            super.bind(id, data);
            author.setText(data.author.getName());
        }

        @Override
        public void reset() {
            super.reset();
            author.setText("");
        }
    }

}
