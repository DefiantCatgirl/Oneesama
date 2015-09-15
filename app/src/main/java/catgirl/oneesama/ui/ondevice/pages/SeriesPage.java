package catgirl.oneesama.ui.ondevice.pages;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import catgirl.oneesama.model.chapter.gson.Chapter;
import catgirl.oneesama.model.chapter.gson.Tag;
import catgirl.oneesama.tools.RealmObservable;
import catgirl.oneesama.ui.ondevice.OnDeviceFragment;
import rx.Observable;
import rx.Subscription;

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
                .setText("You don't have chapters from any series downloaded yet.");

        return emptyMessage;
    }

    @Override
    public int getDataItemCount() {
        return (int) realm.allObjects(Tag.class).where().equalTo("type", "Series").count();
    }

    @Override
    public SeriesAuthor convertDataFromRealm(SeriesAuthorRealm source) {
        return new SeriesAuthor(
                new catgirl.oneesama.model.chapter.ui.Tag(source.series),
                new catgirl.oneesama.model.chapter.ui.Tag(source.author));
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
        catgirl.oneesama.model.chapter.ui.Tag series;
        catgirl.oneesama.model.chapter.ui.Tag author;
        public SeriesAuthor(catgirl.oneesama.model.chapter.ui.Tag series, catgirl.oneesama.model.chapter.ui.Tag author) {
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

    class SeriesViewHolder extends CommonPage.ViewHolder {

        @Bind(R.id.Item_Series_Author) TextView author;
        @Bind(R.id.Item_Series_Title) TextView title;

        public SeriesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(int id, SeriesAuthor data) {
            author.setText(data.author.getName());
            title.setText(data.series.getName());
        }

        public void reset() {
            author.setText("");
            title.setText("");
        }
    }
}
