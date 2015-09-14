package catgirl.oneesama.ui.ondevice.pages;

import catgirl.oneesama.model.chapter.gson.Chapter;
import catgirl.oneesama.model.chapter.gson.Tag;
import catgirl.oneesama.tools.RealmObservable;
import rx.Observable;

public class MiscPage extends CommonPage {

    @Override
    public Observable<SeriesAuthorRealm> getDataSource(int id) {
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

            Tag fake = new Tag();
            fake.setName(chapter.getTitle());
            fake.setId(chapter.getId());

            return new SeriesAuthorRealm(fake, author);
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
}
