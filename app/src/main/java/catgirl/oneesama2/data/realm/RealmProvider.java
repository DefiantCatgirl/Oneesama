package catgirl.oneesama2.data.realm;

import io.realm.Realm;

public class RealmProvider {
    public Realm provideRealm() {
        return Realm.getDefaultInstance();
    }
}
