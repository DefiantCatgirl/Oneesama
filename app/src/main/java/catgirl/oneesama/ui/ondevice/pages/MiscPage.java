package catgirl.oneesama.ui.ondevice.pages;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import catgirl.oneesama.model.chapter.gson.Chapter;
import catgirl.oneesama.model.chapter.gson.Tag;
import catgirl.oneesama.tools.RealmObservable;
import catgirl.oneesama.ui.ondevice.OnDeviceFragment;
import rx.Observable;

public class MiscPage extends CommonPage<MiscPage.ChapterAuthor, MiscPage.ChapterAuthorRealm, MiscPage.ChapterViewHolder> {

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
                new catgirl.oneesama.model.chapter.ui.Chapter(source.chapter),
                new catgirl.oneesama.model.chapter.ui.Tag(source.author));
    }

    @Override
    public ChapterViewHolder provideViewHolder(ViewGroup parent) {
        return new ChapterViewHolder(getActivity().getLayoutInflater().inflate(R.layout.item_chapter_author, parent, false));
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
                .setText("You don't have any uncategorized chapters yet.");

        return emptyMessage;
    }

    class ChapterAuthor {
        catgirl.oneesama.model.chapter.ui.Chapter chapter;
        catgirl.oneesama.model.chapter.ui.Tag author;
        public ChapterAuthor(catgirl.oneesama.model.chapter.ui.Chapter chapter, catgirl.oneesama.model.chapter.ui.Tag author) {
            this.chapter = chapter;
            this.author = author;
        }
    }

    class ChapterAuthorRealm {
        Chapter chapter;
        Tag author;
        public ChapterAuthorRealm(Chapter chapter, Tag author) {
            this.chapter = chapter;
            this.author = author;
        }
    }

    class ChapterViewHolder extends CommonPage.ViewHolder {

        @Bind(R.id.Item_Chapter_Author) TextView author;
        @Bind(R.id.Item_Chapter_Title) TextView title;

        @Bind(R.id.Item_Chapter_StatusLayout) View statusLayout;
        @Bind(R.id.Item_Chapter_ProgressLayout) View progressLayout;
        @Bind(R.id.Item_Chapter_DownloadedLayout) View downloadedLayout;
        @Bind(R.id.Item_Chapter_ReloadLayout) View reloadLayout;
        @Bind(R.id.Item_Chapter_ProgressBar) ProgressWheel progressBar;

        public ChapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(int id, ChapterAuthor data) {
            author.setText(data.author.getName());
            title.setText(data.chapter.getTitle());

            statusLayout.setVisibility(View.VISIBLE);
            progressLayout.setVisibility(View.GONE);

            boolean isDownloaded = data.chapter.isCompletelyDownloaded();

            downloadedLayout.setVisibility(isDownloaded ? View.VISIBLE : View.GONE);
            reloadLayout.setVisibility(isDownloaded ? View.GONE : View.VISIBLE);
        }

        public void reset() {
            author.setText("");
            title.setText("");

            statusLayout.setVisibility(View.GONE);
        }
    }
}
