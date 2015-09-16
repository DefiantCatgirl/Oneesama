package catgirl.oneesama.ui.common.chapter;

import android.animation.Animator;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.Application;
import catgirl.oneesama.R;
import catgirl.oneesama.controller.ChaptersController;
import catgirl.oneesama.controller.legacy.Book;
import catgirl.oneesama.controller.legacy.BookStateDelegate;
import catgirl.oneesama.ui.activity.legacyreader.activityreader.ReaderActivity;
import catgirl.oneesama.ui.activity.legacyreader.tools.EndAnimatorListener;
import catgirl.oneesama.ui.common.CommonViewHolder;

public class ChapterViewHolder extends CommonViewHolder implements BookStateDelegate {

    @Bind(R.id.Item_Chapter_Title)
    protected TextView title;

    @Bind(R.id.Item_Chapter_StatusLayout) View statusLayout;
    @Bind(R.id.Item_Chapter_ProgressLayout) View progressLayout;
    @Bind(R.id.Item_Chapter_DownloadedLayout) View downloadedLayout;
    @Bind(R.id.Item_Chapter_ReloadLayout) View reloadLayout;
    @Bind(R.id.Item_Chapter_ProgressBar) ProgressWheel progressBar;

    ChapterAuthor data;
    int pageId;
    RecyclerView recycler;
    Handler handler;

    public ChapterViewHolder(View itemView, RecyclerView recycler) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        progressBar.stopSpinning();
        this.recycler = recycler;
        itemView.setOnClickListener(this::onClick);
        handler = new Handler();
    }

    private void onClick(View view) {
        if(data == null)
            return;

        Intent readerIntent = new Intent(Application.getContextOfApplication(), ReaderActivity.class);
        readerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        readerIntent.putExtra(ReaderActivity.PUBLICATION_ID, data.chapter.getId());
        Application.getContextOfApplication().startActivity(readerIntent);
    }

    public void bind(int pageId, ChapterAuthor data) {
        this.data = data;
        this.pageId = pageId;

        title.setText(data.chapter.getTitle());

        statusLayout.setVisibility(View.VISIBLE);

        progressLayout.setVisibility(View.GONE);
        reloadLayout.setVisibility(View.GONE);
        downloadedLayout.setVisibility(View.GONE);

        if(ChaptersController.getInstance().isChapterControllerActive(data.chapter.getId())) {
            Book controller = ChaptersController.getInstance().getChapterController(data.chapter.getId());
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
            boolean isDownloaded = data.chapter.isCompletelyDownloaded();
            downloadedLayout.setVisibility(isDownloaded ? View.VISIBLE : View.GONE);
            reloadLayout.setVisibility(isDownloaded ? View.GONE : View.VISIBLE);
        }
    }

    public void reset() {
        title.setText("");

        statusLayout.setVisibility(View.GONE);
    }

    @Override
    public void pageDownloaded(int id, boolean bookDownloaded, int pageId, boolean onlyProgress) {
        handler.post(() -> {
            reloadLayout.setVisibility(View.GONE);
            progressLayout.setVisibility(View.VISIBLE);
            Book controller = ChaptersController.getInstance().getChapterController(data.chapter.getId());
            if(controller != null)
                progressBar.setProgress((float) controller.pagesDownloaded / (float) controller.totalFiles);
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
            layout.setAlpha(0f);
            layout.animate().alpha(1f).setListener(new EndAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    layout.setAlpha(1f);
                    progressLayout.setVisibility(View.GONE);
                }
            });
        });
    }
}