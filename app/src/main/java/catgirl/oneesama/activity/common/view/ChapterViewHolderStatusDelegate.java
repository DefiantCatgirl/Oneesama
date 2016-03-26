package catgirl.oneesama.activity.common.view;

import android.os.Handler;
import android.view.View;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewHelper;
import com.pnikosis.materialishprogress.ProgressWheel;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import catgirl.oneesama.activity.legacyreader.tools.EndAnimatorListener;
import catgirl.oneesama.data.controller.ChaptersController;
import catgirl.oneesama.data.controller.legacy.Book;
import catgirl.oneesama.data.controller.legacy.BookStateDelegate;
import catgirl.oneesama.data.model.chapter.ui.UiChapter;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

/**
 * Put {@code <include layout="@layout/partial_item_chapter_statuslayout"/>} somewhere in your layout.
 */
public class ChapterViewHolderStatusDelegate implements BookStateDelegate {
    private final ChaptersController chaptersController;
    private final CompositeSubscription compositeSubscription;
    Subscription subscription;

    @Bind(R.id.Item_Chapter_StatusLayout) View statusLayout;
    @Bind(R.id.Item_Chapter_ProgressLayout) View progressLayout;
    @Bind(R.id.Item_Chapter_DownloadedLayout) View downloadedLayout;
    @Bind(R.id.Item_Chapter_ReloadLayout) View reloadLayout;
    @Bind(R.id.Item_Chapter_ProgressBar) ProgressWheel progressBar;
    @Bind(R.id.Item_Chapter_ProgressBG) ProgressWheel progressBG;

    Handler handler = new Handler();
    Book controller;
    UiChapter chapter;

    /**
     *
     * @param view layout with the required views, easiest way is to use {@code <include layout="@layout/partial_item_chapter_statuslayout"/>}
     * @param chaptersController self-explanatory
     */
    public ChapterViewHolderStatusDelegate(View view, ChaptersController chaptersController, CompositeSubscription compositeSubscription) {
        this.chaptersController = chaptersController;
        this.compositeSubscription = compositeSubscription;
        ButterKnife.bind(this, view);

        progressBar.stopSpinning();
        progressBG.stopSpinning();
        progressBG.setInstantProgress(1f);
    }

    public void bind(UiChapter chapter) {
        this.chapter = chapter;

        if (chapter == null) {
            hide();
            return;
        }

        if (subscription != null) {
            subscription.unsubscribe();
            compositeSubscription.remove(subscription);
            subscription = null;
        }

        statusLayout.setVisibility(View.VISIBLE);

        // TODO: don't even get me started on how terrible this is
        progressLayout.setVisibility(View.GONE);
        reloadLayout.setVisibility(View.GONE);
        downloadedLayout.setVisibility(View.GONE);

        if(chaptersController.isChapterControllerActive(chapter.getId())) {
            controller = chaptersController.getChapterController(chapter.getId());
            if(controller.completelyDownloaded) {
                downloadedLayout.setVisibility(View.VISIBLE);
            } else if(controller.canReload) {
                reloadLayout.setVisibility(View.VISIBLE);
            } else {
                progressLayout.setVisibility(View.VISIBLE);
                progressBar.setInstantProgress((float) controller.pagesDownloaded / (float) controller.totalFiles);
            }
            controller.addBookStateDelegate(this);
        } else {
            subscription = chaptersController.subscribeForChapterControllerActivation()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(activatedChapter -> {
                        bind(this.chapter);
                    });
            compositeSubscription.add(subscription);
            boolean isDownloaded = chapter.isCompletelyDownloaded();
            downloadedLayout.setVisibility(isDownloaded ? View.VISIBLE : View.GONE);
            reloadLayout.setVisibility(isDownloaded ? View.GONE : View.VISIBLE);
        }
    }

    public void hide() {
        statusLayout.setVisibility(View.GONE);

        if (controller != null) {
            controller.removeBookStateDelegate(this);
            controller = null;
        }
    }

    @Override
    public void pageDownloaded(int id, boolean bookDownloaded, int pageId, boolean onlyProgress) {
        handler.post(() -> {
            if (chapter != null) {
                reloadLayout.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
                Book controller = chaptersController.getChapterController(chapter.getId());
                if (controller != null)
                    progressBar.setProgress((float) controller.pagesDownloaded / (float) controller.totalFiles);
            }
        });
    }

    @Override
    public void completelyDownloaded(int id, boolean success) {
        handler.post(() -> {
            View layout;
            if (success)
                layout = downloadedLayout;
            else
                layout = reloadLayout;

            layout.setVisibility(View.VISIBLE);
            ViewHelper.setAlpha(layout, 0f);
            animate(layout).alpha(1f).setListener(new EndAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    ViewHelper.setAlpha(layout, 1f);
                    progressLayout.setVisibility(View.GONE);
                }
            });
        });
    }
}
