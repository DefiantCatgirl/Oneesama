package catgirl.oneesama2.activity.chapterlist.fragments.chapterlist.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.mvp.BasePresenterFragment;
import catgirl.oneesama2.application.Application;
import catgirl.oneesama.R;
import catgirl.oneesama.controller.ChaptersController;
import catgirl.oneesama2.legacy.legacyreader.activityreader.ReaderActivity;
import catgirl.oneesama.ui.common.chapter.ChapterAuthor;
import catgirl.oneesama2.activity.chapterlist.fragments.chapterlist.ChapterListComponent;
import catgirl.oneesama2.activity.chapterlist.fragments.chapterlist.ChapterListModule;
import catgirl.oneesama2.activity.chapterlist.fragments.chapterlist.presenter.ChapterListPresenter;
import catgirl.oneesama2.activity.chapterlist.fragments.chapterlist.presenter.ChapterListPresenterFactory;

public class ChapterListFragment
        extends BasePresenterFragment<ChapterListPresenter, ChapterListComponent>
        implements ChapterListView
{
    // TODO: The first two blocks are boilerplate, maybe it makes sense to move third into a View

    // Component initialization //

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected ChapterListComponent createComponent() {
        return Application.getApplicationComponent().plus(new ChapterListModule());
    }

    @Override
    protected void onComponentCreated() {
        getComponent().inject(this);
    }

    // Presenter initialization //

    @Inject
    ChapterListPresenterFactory presenterFactory;

    @Inject
    ChaptersController chaptersController;

    @Override
    protected ChapterListPresenter createPresenter() {
        return presenterFactory.createPresenter(getArguments().getInt("TAG_ID"));
    }

    // View //

    @Bind(R.id.Fragment_OnDevice_CommonRecycler)
    RecyclerView recyclerView;

    View emptyMessage;

    boolean shouldDisplayAuthor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_ondevice_page_common, container, false);
        ButterKnife.bind(this, view);

        emptyMessage = getActivity().getLayoutInflater().inflate(R.layout.common_empty_browse, view, false);
        view.addView(emptyMessage);
        emptyMessage.setVisibility(View.GONE);

        recyclerView.setAdapter(new RecyclerView.Adapter<ChapterViewHolder>() {
            @Override
            public ChapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ChapterViewHolder(
                        getActivity().getLayoutInflater().inflate(R.layout.item_chapter_author, parent, false),
                        recyclerView,
                        chaptersController,
                        shouldDisplayAuthor);
            }

            @Override
            public void onBindViewHolder(ChapterViewHolder holder, int position) {
                holder.bind(position, getPresenter().getChapter(position), new ChapterViewHolder.IActionDelegate() {
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
                return getPresenter().getChaptersCount();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Empty
        Button button = (Button) emptyMessage.findViewById(R.id.Common_Empty_BrowseButton);

        button.setOnClickListener(b -> getActivity().onBackPressed());
        button.setText(R.string.activity_chapters_go_back);

        ((TextView) emptyMessage.findViewById(R.id.Common_Empty_MessageText))
                .setText(R.string.activity_chapters_no_comics);

        return view;
    }

    @Override
    public void showContents(@NonNull List<ChapterAuthor> chapters) {
        if (chapters.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyMessage.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyMessage.setVisibility(View.GONE);
        }

        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void setSeriesTitle(String title) {
        // TODO: find a better way, either message activity or move toolbar to fragment
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void setDisplayAuthor(boolean shouldDisplayAuthor) {
        this.shouldDisplayAuthor = shouldDisplayAuthor;
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

        if (getPresenter().getChaptersCount() == 0) {
            getActivity().onBackPressed();
        }
    }
}
