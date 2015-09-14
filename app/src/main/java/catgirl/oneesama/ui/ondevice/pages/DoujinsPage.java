package catgirl.oneesama.ui.ondevice.pages;

import catgirl.oneesama.model.chapter.gson.Chapter;
import catgirl.oneesama.model.chapter.gson.Tag;
import catgirl.oneesama.tools.RealmObservable;
import rx.Observable;

public class DoujinsPage extends CommonPage {

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

}
