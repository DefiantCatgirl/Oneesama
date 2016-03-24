package catgirl.oneesama.activity.browseseriespage;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.yandex.metrica.YandexMetrica;

import catgirl.mvp.implementations.BaseComponentActivity;
import catgirl.oneesama.R;
import catgirl.oneesama.activity.browseseriespage.fragment.view.BrowseSeriesPageFragment;
import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.view.ChapterListFragment;

public class BrowseSeriesPageActivity extends BaseComponentActivity<BrowseSeriesPageComponent> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_series_page);

//        if(!getIntent().getExtras().containsKey("TAG_ID")) {
//            finish();
//            return;
//        }

        if(savedInstanceState == null) {
            Bundle bundle = new Bundle();//getIntent().getExtras());
            BrowseSeriesPageFragment fragment = new BrowseSeriesPageFragment();
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

    public void setSupportActionBar(Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    @Override
    public BrowseSeriesPageComponent createComponent() {
        return new BrowseSeriesPageComponent() {
        };
    }
}
