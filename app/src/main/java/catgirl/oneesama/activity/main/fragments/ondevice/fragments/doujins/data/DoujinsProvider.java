package catgirl.oneesama.activity.main.fragments.ondevice.fragments.doujins.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import catgirl.oneesama.activity.common.data.AutoRefreshableRealmProvider;
import catgirl.oneesama.data.model.chapter.serializable.Tag;
import catgirl.oneesama.data.model.chapter.ui.UiTag;
import catgirl.oneesama.data.realm.RealmProvider;
import catgirl.oneesama.tools.NaturalOrderComparator;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;

public class DoujinsProvider extends AutoRefreshableRealmProvider<Tag, UiTag> {
    private RealmProvider realmProvider;

    public DoujinsProvider(RealmProvider realmProvider) {
        this.realmProvider = realmProvider;
    }

    @Override
    public Realm getRealm() {
        return realmProvider.provideRealm();
    }

    @Override
    public RealmQuery<Tag> getQuery(Realm realm) {
        return realm.allObjects(Tag.class)
                .where()
                .equalTo("type", UiTag.DOUJIN);
    }

    @Override
    public List<UiTag> processQueryResults(Realm realm, RealmResults<Tag> results) {
        List<UiTag> result = new ArrayList<>();

        Observable.from(results)
                .map(UiTag::new)
                .toList()
                .subscribe(result::addAll);

        Collections.sort(result, (lhs, rhs) -> new NaturalOrderComparator().compare(lhs.getName(), rhs.getName()));

        return result;
    }
}
