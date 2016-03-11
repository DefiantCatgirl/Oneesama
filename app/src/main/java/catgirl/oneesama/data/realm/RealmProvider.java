package catgirl.oneesama.data.realm;

import io.realm.Realm;

public class RealmProvider {
    public Realm provideRealm() {
        return Realm.getDefaultInstance();
    }
}
