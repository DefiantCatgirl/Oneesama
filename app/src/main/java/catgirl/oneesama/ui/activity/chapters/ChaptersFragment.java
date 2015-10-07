package catgirl.oneesama.ui.activity.chapters;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import catgirl.oneesama.R;
import catgirl.oneesama.model.chapter.serializable.Chapter;
import catgirl.oneesama.model.chapter.serializable.Tag;
import catgirl.oneesama.model.chapter.ui.UiChapter;
import catgirl.oneesama.model.chapter.ui.UiTag;
import catgirl.oneesama.tools.RealmObservable;
import catgirl.oneesama.ui.activity.main.ondevice.OnDeviceFragment;
import catgirl.oneesama.ui.activity.main.ondevice.pages.MiscPage;
import catgirl.oneesama.ui.common.CommonPage;
import catgirl.oneesama.ui.common.chapter.ChapterAuthor;
import catgirl.oneesama.ui.common.chapter.ChapterAuthorRealm;
import catgirl.oneesama.ui.common.chapter.ChapterViewHolder;
import rx.Observable;

public class ChaptersFragment extends CommonPage<ChapterAuthor, ChapterAuthorRealm, ChapterViewHolder> {

    @Override
    public Observable<ChapterAuthorRealm> getDataSource(int id) {
        return RealmObservable.object(getActivity(), realm1 -> {
            Chapter chapter = realm1.allObjects(Chapter.class)
                    .where()
                    .equalTo("tags.id", getArguments().getInt("TAG_ID"))
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
                .equalTo("tags.id", getArguments().getInt("TAG_ID"))
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
        String tagType = getArguments().getString("TAG_TYPE");
        if(tagType != null && tagType.equals("Series"))
            return new ChapterViewHolder(getActivity().getLayoutInflater().inflate(R.layout.item_chapter_inner, parent, false), recycler) {
                @Override
                public void bind(int position, ChapterAuthor data) {
                    super.bind(position, data);
                    setTitle(title, data);
                }
            };
        else
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
