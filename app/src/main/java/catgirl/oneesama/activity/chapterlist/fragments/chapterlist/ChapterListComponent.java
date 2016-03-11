package catgirl.oneesama.activity.chapterlist.fragments.chapterlist;

import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.view.ChapterListFragment;
import dagger.Subcomponent;

@Subcomponent(modules = {
        ChapterListModule.class,
})
public interface ChapterListComponent {
    void inject(ChapterListFragment fragment);
}
