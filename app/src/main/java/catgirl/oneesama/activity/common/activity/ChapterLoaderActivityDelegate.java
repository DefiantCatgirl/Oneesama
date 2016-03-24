package catgirl.oneesama.activity.common.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewHelper;
import com.yandex.metrica.YandexMetrica;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import catgirl.oneesama.activity.legacyreader.activityreader.ReaderActivity;
import catgirl.oneesama.activity.legacyreader.tools.EndAnimatorListener;
import catgirl.oneesama.data.controller.ChaptersController;
import catgirl.oneesama.data.controller.legacy.Book;
import catgirl.oneesama.data.model.chapter.serializable.Chapter;
import catgirl.oneesama.data.model.chapter.ui.UiTag;
import io.realm.Realm;
import rx.Observable;
import rx.Subscription;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

/**
 * Put {@code <include layout="@layout/partial_loading_layout"/>} somewhere in your layout.
 */
public class ChapterLoaderActivityDelegate {

    private static final String LOADING_KEY = "loading";

    private final Activity activity;
    private final Realm realm;
    private final ChaptersController chaptersController;

    // TODO use a library or move this into a singleton
    // And yes, it is in fact just one chapterRequest per app
    static Observable<Book> chapterRequest;
    Subscription chapterSubscription;

    @Bind(R.id.MainActivity_LoadingLayout) View loadingLayout;

    boolean loading = false;

    public ChapterLoaderActivityDelegate(Activity activity, View view, Realm realm, ChaptersController chaptersController) {
        this.activity = activity;
        this.realm = realm;
        this.chaptersController = chaptersController;

        ButterKnife.bind(this, view);
    }

    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            loading = savedInstanceState.getBoolean(LOADING_KEY);
            if (loading) {
                loadingLayout.setVisibility(View.VISIBLE);
                loadingLayout.clearAnimation();
                if (chapterRequest != null)
                    subscribeToChapterRequest(chapterRequest);
            }
        }
    }

    public void onDestroy() {
        if(chapterSubscription != null)
            chapterSubscription.unsubscribe();
        realm.close();
    }

    public boolean isLoading() {
        return loading;
    }

    public void openChapterByPermalink(String permalink) {
        Chapter chapter = realm.where(Chapter.class).equalTo("permalink", permalink).findFirst();
        if(chapter != null) {
            Intent readerIntent = new Intent(activity, ReaderActivity.class);
            readerIntent.putExtra(ReaderActivity.PUBLICATION_ID, chapter.getId());
            activity.startActivity(readerIntent);
            return;
        }

        loading = true;
        loadingLayout.setVisibility(View.VISIBLE);
        ViewHelper.setAlpha(loadingLayout, 0f);
        animate(loadingLayout).alpha(1f).setListener(new EndAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                ViewHelper.setAlpha(loadingLayout, 1f);
            }
        });

        chapterRequest = chaptersController
                .requestChapterController(permalink)
                .cache();

        subscribeToChapterRequest(chapterRequest);
    }

    private void subscribeToChapterRequest(Observable<Book> observable) {
        chapterSubscription = observable.subscribe(response -> {

            String eventParameters = "{\"id\":\"" + response.data.getPermalink() + "\", \"tags\": [";
            for(UiTag tag : response.data.getTags()) {
                eventParameters += "{\"" + tag.getType() + "\": \"" + tag.getName() + "\"},";
            }
            eventParameters = eventParameters.substring(0, eventParameters.length() - 1);
            eventParameters += "]}";
            YandexMetrica.reportEvent("Chapter added", eventParameters);

            Intent readerIntent = new Intent(activity, ReaderActivity.class);
            readerIntent.putExtra(ReaderActivity.PUBLICATION_ID, response.data.getId());
            activity.startActivity(readerIntent);
            loading = false;
            loadingLayout.setVisibility(View.GONE);
            loadingLayout.clearAnimation();
            chapterRequest = null;
            chapterSubscription = null;
        }, error -> {
            Toast.makeText(activity, "Error adding chapter:\n" + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            loading = false;
            loadingLayout.setVisibility(View.GONE);
            loadingLayout.clearAnimation();
            chapterRequest = null;
            chapterSubscription = null;
        });
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(LOADING_KEY, loading);
    }
}
