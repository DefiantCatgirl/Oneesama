package catgirl.oneesama.ui.activity.main.ondevice.pages;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import catgirl.oneesama.model.chapter.serializable.Chapter;
import catgirl.oneesama.model.chapter.serializable.Tag;
import catgirl.oneesama.model.chapter.ui.UiChapter;
import catgirl.oneesama.model.chapter.ui.UiTag;
import catgirl.oneesama.tools.RealmObservable;
import catgirl.oneesama.ui.activity.chapters.ChaptersActivity;
import catgirl.oneesama.ui.common.CommonPage;
import catgirl.oneesama.ui.activity.main.ondevice.OnDeviceFragment;
import catgirl.oneesama.ui.common.CommonViewHolder;
import catgirl.oneesama.ui.common.chapter.ChapterAuthor;
import io.realm.RealmResults;
import rx.Observable;

public class SeriesPage extends CommonPage<SeriesPage.SeriesAuthor, SeriesPage.SeriesViewHolder> {

    @Override
    public List<SeriesAuthor> getDataSource() {
        RealmResults<Tag> results = realm.allObjects(Tag.class)
                .where()
                .equalTo("type", "Series")
                .findAllSorted("name");

        List<SeriesAuthor> result = new ArrayList<>();

        Observable.from(results)
                .map(series -> new SeriesAuthor(new UiTag(series), new UiTag(realm.allObjects(Chapter.class)
                        .where()
                        .equalTo("tags.id", series.getId())
                        .findFirst()
                        .getTags()
                        .where()
                        .equalTo("type", "Author")
                        .findFirst())))
                .toList()
                .subscribe(result::addAll);

        return result;
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

            if(data.author != null)
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
