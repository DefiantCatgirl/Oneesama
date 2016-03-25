package catgirl.oneesama.activity.browseseriespage;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.yandex.metrica.YandexMetrica;

import catgirl.mvp.implementations.BaseComponentActivity;
import catgirl.oneesama.R;
import catgirl.oneesama.activity.browseseriespage.fragment.view.BrowseSeriesPageFragment;
import catgirl.oneesama.application.Application;

public class BrowseSeriesPageActivity extends BaseComponentActivity<BrowseSeriesPageActivityComponent> {

    public static final String SERIES_PERMALINK = "series_permalink";
    public static final String SERIES_TITLE = "series_title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_series_page);

        if(getIntent().getExtras() == null || !getIntent().getExtras().containsKey(SERIES_PERMALINK)) {
            finish();
            return;
        }

        if(savedInstanceState == null) {
            Bundle bundle = new Bundle(getIntent().getExtras());
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
    public BrowseSeriesPageActivityComponent createComponent() {
        return Application.getApplicationComponent().plus(new BrowseSeriesPageActivityModule());
    }
}
