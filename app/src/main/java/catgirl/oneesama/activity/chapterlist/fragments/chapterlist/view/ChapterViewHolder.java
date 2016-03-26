package catgirl.oneesama.activity.chapterlist.fragments.chapterlist.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import catgirl.oneesama.activity.common.view.ChapterViewHolderStatusDelegate;
import catgirl.oneesama.data.controller.ChaptersController;
import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.data.model.ChapterAuthor;
import rx.subscriptions.CompositeSubscription;

public class ChapterViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.Item_Chapter_Title) protected TextView title;
    @Bind(R.id.Item_Chapter_Volume) protected TextView volume;
    @Bind(R.id.Item_Chapter_DeleteButton) ImageButton deleteButton;
    @Bind(R.id.Item_Chapter_Author) TextView author;

    ChapterAuthor data;
    private IActionDelegate delegate;
    RecyclerView recycler;
    Context context;

    // TODO: ew
    private boolean isDoujinsPage;

    ChapterViewHolderStatusDelegate statusDelegate;

    public ChapterViewHolder(
            View itemView,
            RecyclerView recycler,
            ChaptersController chaptersController,
            CompositeSubscription compositeSubscription,
            boolean isDoujinsPage) {
        super(itemView);
        this.isDoujinsPage = isDoujinsPage;
        ButterKnife.bind(this, itemView);
        context = itemView.getContext();
        this.recycler = recycler;
        itemView.setOnClickListener(this::onClick);
        deleteButton.setOnClickListener(this::onDeleteButtonClicked);

        statusDelegate = new ChapterViewHolderStatusDelegate(itemView, chaptersController, compositeSubscription);
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

    public void bind(ChapterAuthor data, IActionDelegate delegate) {
        this.data = data;
        this.delegate = delegate;

        title.setText(data.chapter.getTitle());

        if (isDoujinsPage) {
            author.setVisibility(View.VISIBLE);
            author.setText(data.author.getName());
            title.setText(data.chapter.getLongTitle());
        } else {
            author.setVisibility(View.GONE);
        }

        if(data.chapter.getVolumeName() != null & !isDoujinsPage) {
            volume.setText(data.chapter.getVolumeName());
            volume.setVisibility(View.VISIBLE);
        } else {
            volume.setVisibility(View.GONE);
        }

        statusDelegate.bind(data.chapter);
    }

    public interface IActionDelegate {
        void onClick();
        void onDelete();
    }
}
