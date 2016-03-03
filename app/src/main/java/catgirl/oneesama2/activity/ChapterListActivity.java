package catgirl.oneesama2.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.mvp.BasePresenterActivity;
import catgirl.oneesama.R;
import catgirl.oneesama.model.chapter.serializable.Tag;
import catgirl.oneesama2.fragments.chapterwithauthor.fragment.ChapterWithAuthorListFragment;
import io.realm.Realm;

public class ChapterListActivity extends BasePresenterActivity {
    @Bind(R.id.toolbar_layout)
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters);

        ButterKnife.bind(this);

        Realm realm = Realm.getDefaultInstance();
        Tag tag = realm.where(Tag.class).equalTo("id", getIntent().getIntExtra("TAG_ID", 0)).findFirst();
        String name = tag.getName();
        toolbar.setTitle(name);
        realm.close();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        if(!getIntent().getExtras().containsKey("TAG_ID")) {
            finish();
            return;
        }

        if(savedInstanceState == null) {
            Bundle bundle = new Bundle(getIntent().getExtras());
            bundle.putString("TAG_NAME", name);
            bundle.putString("TAG_TYPE", tag.getType());
//            if(tag.getType().equals("Series")) {
//                SeriesChaptersFragment fragment = new SeriesChaptersFragment();
//                fragment.setArguments(bundle);
//                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
//            } else {
//                ChaptersFragment fragment = new ChaptersFragment();
//                fragment.setArguments(bundle);
//                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
//            }

            ChapterWithAuthorListFragment fragment = new ChapterWithAuthorListFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }
    }
}
