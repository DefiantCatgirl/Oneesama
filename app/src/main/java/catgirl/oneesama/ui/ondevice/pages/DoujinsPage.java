package catgirl.oneesama.ui.ondevice.pages;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import catgirl.oneesama.R;
import catgirl.oneesama.model.chapter.gson.Chapter;
import catgirl.oneesama.model.chapter.gson.Tag;
import catgirl.oneesama.tools.RealmObservable;
import catgirl.oneesama.ui.ondevice.OnDeviceFragment;
import rx.Observable;

public class DoujinsPage extends SeriesPage {

    @Override
    public Observable<SeriesAuthorRealm> getDataSource(int id) {
        return RealmObservable.object(getActivity(), realm1 -> {
            Tag series = realm1.allObjects(Tag.class)
                    .where()
                    .equalTo("type", "Doujin")
                    .findAllSorted("name")
                    .get(id);

            Tag fake = new Tag();
            fake.setName("");
            fake.setId(id);

            return new SeriesAuthorRealm(series, fake);
        });
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
                .setText("You don't have any doujins downloaded yet. T-that would have been lewd.");

        return emptyMessage;
    }

}
