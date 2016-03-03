package catgirl.oneesama2.fragments.chapterwithauthor;

import catgirl.oneesama2.fragments.chapterwithauthor.fragment.ChapterWithAuthorListFragment;
import dagger.Subcomponent;

@Subcomponent(modules = {
        ChapterWithAuthorListModule.class,
})
public interface ChapterWithAuthorListComponent {
    void inject(ChapterWithAuthorListFragment fragment);
}
