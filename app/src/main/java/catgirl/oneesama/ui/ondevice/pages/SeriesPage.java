package catgirl.oneesama.ui.ondevice.pages;

import catgirl.oneesama.model.chapter.gson.Chapter;
import catgirl.oneesama.model.chapter.gson.Tag;
import catgirl.oneesama.tools.RealmObservable;
import rx.Observable;

public class SeriesPage extends CommonPage {

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
    public int getDataItemCount() {
        return (int) realm.allObjects(Tag.class).where().equalTo("type", "Series").count();
    }
}
