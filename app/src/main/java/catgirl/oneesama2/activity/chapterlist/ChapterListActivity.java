package catgirl.oneesama2.activity.chapterlist;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.mvp.BasePresenterActivity;
import catgirl.oneesama.R;
import catgirl.oneesama2.activity.chapterlist.fragments.chapterlist.fragment.ChapterListFragment;

public class ChapterListActivity extends BasePresenterActivity {
    @Bind(R.id.toolbar_layout)
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters);

        ButterKnife.bind(this);

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
            ChapterListFragment fragment = new ChapterListFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }
    }
}
