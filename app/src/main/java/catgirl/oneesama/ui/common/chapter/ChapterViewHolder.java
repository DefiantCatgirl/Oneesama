package catgirl.oneesama.ui.common.chapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
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
import catgirl.oneesama.controller.ChaptersController;
import catgirl.oneesama.controller.legacy.Book;
import catgirl.oneesama.controller.legacy.BookStateDelegate;
import catgirl.oneesama.ui.activity.legacyreader.activityreader.ReaderActivity;
import catgirl.oneesama.ui.activity.legacyreader.tools.EndAnimatorListener;
import catgirl.oneesama.ui.common.CommonViewHolder;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

public class ChapterViewHolder extends CommonViewHolder implements BookStateDelegate {

    @Bind(R.id.Item_Chapter_Title) protected TextView title;

    @Bind(R.id.Item_Chapter_StatusLayout) View statusLayout;
    @Bind(R.id.Item_Chapter_ProgressLayout) View progressLayout;
    @Bind(R.id.Item_Chapter_DownloadedLayout) View downloadedLayout;
    @Bind(R.id.Item_Chapter_ReloadLayout) View reloadLayout;
    @Bind(R.id.Item_Chapter_ProgressBar) ProgressWheel progressBar;
    @Bind(R.id.Item_Chapter_ProgressBG) ProgressWheel progressBG;

    @Bind(R.id.Item_Chapter_DeleteButton) ImageButton deleteButton;

    ChapterAuthor data;
    int position;
    RecyclerView recycler;
    Handler handler;
    Context context;

    public ChapterViewHolder(View itemView, RecyclerView recycler) {
        super(itemView);
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
        if(data == null)
            return;

        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    ChaptersController.getInstance().deleteChapter(data.chapter.getId());
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.item_chapter_are_you_sure_delete)).setPositiveButton(context.getString(R.string.common_yes), dialogClickListener)
                .setNegativeButton(context.getString(R.string.common_no), dialogClickListener).show();
    }

    private void onClick(View view) {
        if(data == null)
            return;

        Intent readerIntent = new Intent(context, ReaderActivity.class);
        readerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        readerIntent.putExtra(ReaderActivity.PUBLICATION_ID, data.chapter.getId());
        context.startActivity(readerIntent);
    }

    public void bind(int position, ChapterAuthor data) {
        this.data = data;
        this.position = position;

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