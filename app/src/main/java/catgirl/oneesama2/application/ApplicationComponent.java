package catgirl.oneesama2.application;

import javax.inject.Singleton;

import catgirl.oneesama2.fragments.chapterwithauthor.ChapterWithAuthorListComponent;
import catgirl.oneesama2.fragments.chapterwithauthor.ChapterWithAuthorListModule;
import dagger.Component;
import io.realm.Realm;

@Singleton
@Component(modules = {
        ApplicationModule.class,
})
public interface ApplicationComponent {
    // For those odd cases where it's tiresome to bother with injections
    Realm getRealm();

    // Fragment scoped subcomponents
    ChapterWithAuthorListComponent plus(ChapterWithAuthorListModule module);
}
