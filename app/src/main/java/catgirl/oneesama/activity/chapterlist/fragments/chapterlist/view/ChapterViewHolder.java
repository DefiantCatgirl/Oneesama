package catgirl.oneesama.activity.chapterlist.fragments.chapterlist.view;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewHelper;
import com.pnikosis.materialishprogress.ProgressWheel;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import catgirl.oneesama.data.controller.ChaptersController;
import catgirl.oneesama.data.controller.legacy.Book;
import catgirl.oneesama.data.controller.legacy.BookStateDelegate;
import catgirl.oneesama.activity.legacyreader.tools.EndAnimatorListener;
import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.data.model.ChapterAuthor;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

public class ChapterViewHolder extends RecyclerView.ViewHolder implements BookStateDelegate {

    @Bind(R.id.Item_Chapter_Title) protected TextView title;
    @Bind(R.id.Item_Chapter_Volume) protected TextView volume;

    @Bind(R.id.Item_Chapter_StatusLayout) View statusLayout;
    @Bind(R.id.Item_Chapter_ProgressLayout) View progressLayout;
    @Bind(R.id.Item_Chapter_DownloadedLayout) View downloadedLayout;
    @Bind(R.id.Item_Chapter_ReloadLayout) View reloadLayout;
    @Bind(R.id.Item_Chapter_ProgressBar) ProgressWheel progressBar;
    @Bind(R.id.Item_Chapter_ProgressBG) ProgressWheel progressBG;

    @Bind(R.id.Item_Chapter_DeleteButton) ImageButton deleteButton;

    @Bind(R.id.Item_Chapter_Author) TextView author;

    ChapterAuthor data;
    int position;
    private IActionDelegate delegate;
    RecyclerView recycler;
    Handler handler;
    Context context;
    private ChaptersController chaptersController;
    private boolean shouldDisplayAuthor;

    public ChapterViewHolder(View itemView, RecyclerView recycler, ChaptersController chaptersController, boolean shouldDisplayAuthor) {
        super(itemView);
        this.chaptersController = chaptersController;
        this.shouldDisplayAuthor = shouldDisplayAuthor;
        ButterKnife.bind(this, itemView);
        context = itemView.getContext();
        progressBar.stopSpinning();
        progressBG.stopSpinning();
        progressBG.setInstantProgress(1f);
        this.recycler = recycler;
        itemView.setOnClickListener(this::onClick);
        deleteButton.setOnClickListener(this::onDeleteButtonClicked);
        handler = new Handler();
    }

    private void onDeleteButtonClicked(View view) {
        if(data == null || delegate == null)
            return;

        delegate.onDelete();
    }

    private void onClick(View view) {
        if(data == null || delegate == null)
            return;

        delegate.onClick();
    }

    public void bind(int position, ChapterAuthor data, IActionDelegate delegate) {
        this.data = data;
        this.position = position;
        this.delegate = delegate;

        title.setText(data.chapter.getTitle());

        if (shouldDisplayAuthor) {
            author.setVisibility(View.VISIBLE);
            author.setText(data.author.getName());
        } else {
            author.setVisibility(View.GONE);
        }

        if(data.chapter.getVolumeName() != null) {
            volume.setText(data.chapter.getVolumeName());
            volume.setVisibility(View.VISIBLE);
        } else {
            volume.setVisibility(View.GONE);
        }

        statusLayout.setVisibility(View.VISIBLE);

        // TODO: don't even get me started on how terrible this is
        progressLayout.setVisibility(View.GONE);
        reloadLayout.setVisibility(View.GONE);
        downloadedLayout.setVisibility(View.GONE);

        if(chaptersController.isChapterControllerActive(data.chapter.getId())) {
            Book controller = chaptersController.getChapterController(data.chapter.getId());
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

    @Override
    public void pageDownloaded(int id, boolean bookDownloaded, int pageId, boolean onlyProgress) {
        handler.post(() -> {
            reloadLayout.setVisibility(View.GONE);
            progressLayout.setVisibility(View.VISIBLE);
            Book controller = chaptersController.getChapterController(data.chapter.getId());
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

    public interface IActionDelegate {
        void onClick();
        void onDelete();
    }
}
