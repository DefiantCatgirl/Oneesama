package catgirl.oneesama.ui.activity.main.ondevice.pages;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import catgirl.oneesama.R;
import catgirl.oneesama2.data.model.chapter.serializable.Tag;
import catgirl.oneesama2.data.model.chapter.ui.UiTag;
import catgirl.oneesama.ui.activity.main.ondevice.OnDeviceFragment;
import io.realm.RealmResults;
import rx.Observable;

public class DoujinsPage extends SeriesPage {

    @Override
    public List<SeriesAuthor> getDataSource() {
        RealmResults<Tag> results = realm.allObjects(Tag.class)
                .where()
                .equalTo("type", "Doujin")
                .findAllSorted("name");

        List<SeriesAuthor> result = new ArrayList<>();

        Observable.from(results)
                .map(chapter -> new SeriesAuthor(new UiTag(chapter), null))
                .toList()
                .subscribe(result::addAll);

        return result;
    }

    @Override
    public int getDataItemCount() {
        return (int) realm.allObjects(Tag.class).where().equalTo("type", "Doujin").count();
    }

    @Override
    public View getEmptyMessage(ViewGroup parent) {

        View emptyMessage = getActivity().getLayoutInflater().inflate(R.layout.common_empty_browse, parent, false);

        emptyMessage.findViewById(R.id.Common_Empty_BrowseButton)
                .setOnClickListener(button -> ((OnDeviceFragment.OnDeviceFragmentDelegate) getActivity()).onBrowseButtonPressed());

        ((TextView) emptyMessage.findViewById(R.id.Common_Empty_MessageText))
                .setText(R.string.page_doujins_no_chapters);

        return emptyMessage;
    }
}
