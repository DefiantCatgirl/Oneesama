package catgirl.oneesama.ui.activity.main.ondevice.pages;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import catgirl.oneesama.model.chapter.serializable.Chapter;
import catgirl.oneesama.model.chapter.serializable.Tag;
import catgirl.oneesama.model.chapter.ui.UiTag;
import catgirl.oneesama.tools.RealmObservable;
import catgirl.oneesama.ui.activity.chapters.ChaptersActivity;
import catgirl.oneesama.ui.common.CommonPage;
import catgirl.oneesama.ui.activity.main.ondevice.OnDeviceFragment;
import catgirl.oneesama.ui.common.CommonViewHolder;
import rx.Observable;

public class SeriesPage extends CommonPage<SeriesPage.SeriesAuthor, SeriesPage.SeriesAuthorRealm, SeriesPage.SeriesViewHolder> {

    @Override
    public Observable<SeriesAuthorRealm> getDataSource(int id) {
        return RealmObservable.object(getActivity(), realm1 -> {
            Tag series = realm1.allObjects(Tag.class)
                    .where()
                    .equalTo("type", "Series")
                    .findAllSorted("name")
                    .get(id);
            Tag author = realm1.allObjects(Chapter.class)
                    .where()
                    .equalTo("tags.id", series.getId())
                    .findFirst()
                    .getTags()
                    .where()
                    .equalTo("type", "Author")
                    .findFirst();
            return new SeriesAuthorRealm(series, author);
        });
    }

    @Override
    public View getEmptyMessage(ViewGroup parent) {

        View emptyMessage = getActivity().getLayoutInflater().inflate(R.layout.common_empty_browse, parent, false);

        emptyMessage.findViewById(R.id.Common_Empty_BrowseButton)
                .setOnClickListener(button -> ((OnDeviceFragment.OnDeviceFragmentDelegate) getActivity()).onBrowseButtonPressed());

        ((TextView) emptyMessage.findViewById(R.id.Common_Empty_MessageText))
                .setText(R.string.page_series_no_chapters);

        return emptyMessage;
    }

    @Override
    public int getDataItemCount() {
        return (int) realm.allObjects(Tag.class).where().equalTo("type", "Series").count();
    }

    @Override
    public SeriesAuthor convertDataFromRealm(SeriesAuthorRealm source) {
        return new SeriesAuthor(
                new UiTag(source.series),
                new UiTag(source.author));
    }

    @Override
    public SeriesViewHolder provideViewHolder(ViewGroup parent) {
        return new SeriesViewHolder(getActivity().getLayoutInflater().inflate(R.layout.item_series, parent, false));
    }

    @Override
    public void bindViewHolder(SeriesViewHolder holder, int position, SeriesAuthor data) {
        holder.bind(position, data);
    }

    @Override
    public void resetViewHolder(SeriesViewHolder holder, int position) {
        holder.reset();
    }

    class SeriesAuthor {
        UiTag series;
        UiTag author;
        public SeriesAuthor(UiTag series, UiTag author) {
            this.series = series;
            this.author = author;
        }
    }

    class SeriesAuthorRealm {
        Tag series;
        Tag author;
        public SeriesAuthorRealm(Tag series, Tag author) {
            this.series = series;
            this.author = author;
        }
    }

    class SeriesViewHolder extends CommonViewHolder {

        @Bind(R.id.Item_Series_Author) TextView author;
        @Bind(R.id.Item_Series_Title) TextView title;

        SeriesAuthor data;

        public SeriesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this::onClick);
        }

        public void bind(int id, SeriesAuthor data) {
            this.data = data;

            author.setText(data.author.getName());
            title.setText(data.series.getName());
        }

        public void reset() {
            data = null;
            author.setText("");
            title.setText("");
        }

        public void onClick(View view) {
            if(data == null)
                return;

            Intent intent = new Intent(getActivity(), ChaptersActivity.class);
            intent.putExtra("TAG_ID", data.series.getId());
            startActivity(intent);
        }
    }
}
