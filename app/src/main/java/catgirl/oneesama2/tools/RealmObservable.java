package catgirl.oneesama2.tools;

import android.content.Context;

import io.realm.Realm;
import rx.Observable;
import rx.functions.Func1;

public final class RealmObservable {
    private RealmObservable() {
    }

    public static <T extends Object> Observable<T> object(Context context, final Func1<Realm, T> function) {
        return Observable.create(new OnSubscribeRealm<T>(context) {
            @Override
            public T get(Realm realm) {
                return function.call(realm);
            }
        });
    }

    public static <T extends Object> Observable<T> object(Context context, String fileName, final Func1<Realm, T> function) {
        return Observable.create(new OnSubscribeRealm<T>(context, fileName) {
            @Override
            public T get(Realm realm) {
                return function.call(realm);
            }
        });
    }
}
