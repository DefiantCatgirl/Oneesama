package catgirl.oneesama.activity.main.fragments.history.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import catgirl.oneesama.activity.common.view.ChapterViewHolderStatusDelegate;
import catgirl.oneesama.data.controller.ChaptersController;
import catgirl.oneesama.data.model.chapter.ui.UiChapter;
import rx.subscriptions.CompositeSubscription;

public class HistoryViewHolder extends RecyclerView.ViewHolder {
    private final ChaptersController chaptersController;
    private final CompositeSubscription compositeSubscription;
    private final ChapterViewHolderStatusDelegate statusDelegate;
    private ActionDelegate delegate;

    @Bind(R.id.Item_Chapter_Title) protected TextView title;
    @Bind(R.id.Item_Chapter_DeleteButton) ImageButton deleteButton;
    @Bind(R.id.Item_Chapter_Author) TextView author;

    public HistoryViewHolder(View itemView, ChaptersController chaptersController, CompositeSubscription compositeSubscription) {
        super(itemView);
        this.chaptersController = chaptersController;
        this.compositeSubscription = compositeSubscription;

        ButterKnife.bind(this, itemView);

        itemView.setOnClickListener(this::onClick);
        deleteButton.setOnClickListener(this::onDeleteButtonClicked);

        statusDelegate = new ChapterViewHolderStatusDelegate(itemView, chaptersController, compositeSubscription);
    }

    private void onDeleteButtonClicked(View view) {
        if (delegate != null)
            delegate.onDelete();
    }

    private void onClick(View view) {
        if (delegate != null)
            delegate.onClick();
    }

    public void bind(UiChapter item, ActionDelegate delegate) {
        this.delegate = delegate;

        author.setVisibility(View.GONE);
        title.setText(item.getLongTitle());

        statusDelegate.bind(item);
    }

    public interface ActionDelegate {
        void onClick();
        void onDelete();
    }
}
