package catgirl.oneesama.activity.chapterlist.fragments.chapterlist.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import javax.inject.Inject;

import catgirl.oneesama.R;
import catgirl.oneesama.activity.browseseriespage.BrowseSeriesPageActivity;
import catgirl.oneesama.activity.chapterlist.ChapterListActivity;
import catgirl.oneesama.data.controller.ChaptersController;
import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.data.model.ChapterAuthor;
import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.ChapterListComponent;
import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.ChapterListModule;
import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.presenter.ChapterListPresenter;
import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.presenter.ChapterListPresenterFactory;
import catgirl.oneesama.activity.common.view.SimpleRecyclerFragment;
import catgirl.oneesama.application.Application;
import catgirl.oneesama.activity.legacyreader.activityreader.ReaderActivity;

public class ChapterListFragment
        extends SimpleRecyclerFragment<ChapterAuthor, ChapterViewHolder, ChapterListPresenter, ChapterListComponent>
        implements ChapterListView
{
    // TODO: The first two blocks are boilerplate, maybe it makes sense to move third into a View
    // TODO: alternatively find a way to get rid of said boilerplate

    // Component initialization //

    @Override
    protected ChapterListComponent createComponent() {
        return Application.getApplicationComponent().plus(new ChapterListModule());
    }

    @Override
    protected void onComponentCreated() {
        getComponent().inject(this);
    }

    // Presenter initialization //

    @Inject ChapterListPresenterFactory presenterFactory;
    @Inject ChaptersController chaptersController;

    @Override
    protected ChapterListPresenter createPresenter() {
        return presenterFactory.createPresenter(getArguments().getInt(ChapterListActivity.TAG_ID));
    }

    // View //

    boolean isDoujinsPage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.chapterlist_fragment_menu, menu);

        Drawable drawable = menu.findItem(R.id.MenuItem_Browse).getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.MenuItem_Browse:
                getPresenter().onBrowseClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public ChapterViewHolder createViewHolder(ViewGroup parent) {
        return new ChapterViewHolder(
                getActivity().getLayoutInflater().inflate(R.layout.item_chapter_author, parent, false),
                recyclerView,
                chaptersController,
                compositeSubscription,
                isDoujinsPage);
    }

    @Override
    public void bindViewHolder(ChapterViewHolder holder, int position) {
        holder.bind(getPresenter().getItem(position), new ChapterViewHolder.IActionDelegate() {
            @Override
            public void onClick() {
                // Granular deletion doesn't re-bind the existing viewholders,
                // hence can't use the initially bound position
                getPresenter().onItemClicked(holder.getAdapterPosition());
            }

            @Override
            public void onDelete() {
                getPresenter().onItemDeleteClicked(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return getPresenter().getItemsCount();
    }

    @Override
    public View getEmptyMessage(ViewGroup view) {
        View emptyMessage = getActivity().getLayoutInflater().inflate(R.layout.common_empty_browse, view, false);
        emptyMessage.setVisibility(View.GONE);

        Button button = (Button) emptyMessage.findViewById(R.id.Common_Empty_BrowseButton);

        button.setOnClickListener(b -> getActivity().onBackPressed());
        button.setText(R.string.activity_chapters_go_back);

        ((TextView) emptyMessage.findViewById(R.id.Common_Empty_MessageText))
                .setText(R.string.activity_chapters_no_comics);

        return emptyMessage;
    }

    @Override
    public void setSeriesTitle(String title) {
        // TODO: find a better way, either message activity or move toolbar to fragment
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void setIsDoujinsPage(boolean isDoujinsPage) {
        this.isDoujinsPage = isDoujinsPage;
    }

    @Override
    public void showSeriesImage(@NonNull Bitmap seriesImage, boolean animateIn) {

    }

    @Override
    public void switchToReader(int id) {
        Intent readerIntent = new Intent(getContext(), ReaderActivity.class);
        readerIntent.putExtra(ReaderActivity.PUBLICATION_ID, id);
        startActivity(readerIntent);
    }

    @Override
    public void showDeleteConfirmation(int position) {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    getPresenter().onItemDeletionConfirmed(position);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    getPresenter().onItemDeletionDismissed();
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getContext().getString(R.string.item_chapter_are_you_sure_delete))
                .setPositiveButton(getContext().getString(R.string.common_yes), dialogClickListener)
                .setNegativeButton(getContext().getString(R.string.common_no), dialogClickListener)
                .setOnDismissListener(dialog -> getPresenter().onItemDeletionDismissed())
                .show();
    }

    @Override
    public void showItemDeleted(int position) {
        recyclerView.getAdapter().notifyItemRemoved(position);

        if (getPresenter().getItemsCount() == 0) {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public void switchToBrowseSeriesPage(String permalink, String name) {
        Intent intent = new Intent(getActivity(), BrowseSeriesPageActivity.class);
        intent.putExtra(BrowseSeriesPageActivity.SERIES_PERMALINK, permalink);
        intent.putExtra(BrowseSeriesPageActivity.SERIES_TITLE, name);
        startActivity(intent);
    }

    protected boolean hasStableIds() {
        return true;
    }

    protected long getItemId(int position) {
        return getPresenter().getItem(position).chapter.getPermalink().hashCode();
    }
}
