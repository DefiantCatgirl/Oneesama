package catgirl.oneesama.activity.browseseriespage;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.yandex.metrica.YandexMetrica;

import javax.inject.Inject;

import catgirl.mvp.implementations.BaseComponentActivity;
import catgirl.oneesama.R;
import catgirl.oneesama.activity.browseseriespage.fragment.view.BrowseSeriesPageFragment;
import catgirl.oneesama.activity.common.activity.ChapterLoaderActivity;
import catgirl.oneesama.activity.common.activity.ChapterLoaderActivityDelegate;
import catgirl.oneesama.application.Application;
import catgirl.oneesama.data.controller.ChaptersController;
import catgirl.oneesama.data.realm.RealmProvider;

public class BrowseSeriesPageActivity
        extends BaseComponentActivity<BrowseSeriesPageActivityComponent>
        implements ChapterLoaderActivity
{

    public static final String SERIES_PERMALINK = "series_permalink";
    public static final String SERIES_TITLE = "series_title";

    @Inject RealmProvider realmProvider;
    @Inject ChaptersController chaptersController;

    ChapterLoaderActivityDelegate chapterLoaderDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_series_page);
        getComponent().inject(this);

        if(getIntent().getExtras() == null || !getIntent().getExtras().containsKey(SERIES_PERMALINK)) {
            finish();
            return;
        }

        chapterLoaderDelegate = new ChapterLoaderActivityDelegate(
                this, findViewById(android.R.id.content),
                realmProvider.provideRealm(),
                chaptersController);

        chapterLoaderDelegate.onCreate(savedInstanceState);

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

    @Override
    public void openChapterByPermalink(String permalink) {
        chapterLoaderDelegate.openChapterByPermalink(permalink);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chapterLoaderDelegate.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        chapterLoaderDelegate.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
}
