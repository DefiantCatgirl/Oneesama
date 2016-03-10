package catgirl.oneesama2.activity.chapterlist.fragments.chapterlist;

import catgirl.oneesama2.activity.chapterlist.fragments.chapterlist.fragment.ChapterListFragment;
import dagger.Subcomponent;

@Subcomponent(modules = {
        ChapterListModule.class,
})
public interface ChapterListComponent {
    void inject(ChapterListFragment fragment);
}
