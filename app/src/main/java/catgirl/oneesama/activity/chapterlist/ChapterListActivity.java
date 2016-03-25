package catgirl.oneesama.activity.chapterlist;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.yandex.metrica.YandexMetrica;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.mvp.implementations.BaseCacheActivity;
import catgirl.oneesama.R;
import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.view.ChapterListFragment;

public class ChapterListActivity extends BaseCacheActivity {

    public static final String TAG_ID = "tag_id";

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

        if(getIntent().getExtras() == null || !getIntent().getExtras().containsKey(TAG_ID)) {
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

    @Override
    protected void onResume() {
        super.onResume();
        YandexMetrica.onResumeActivity(this);
    }

    @Override
    protected void onPause() {
        YandexMetrica.onPauseActivity(this);
        super.onPause();
    }
}
