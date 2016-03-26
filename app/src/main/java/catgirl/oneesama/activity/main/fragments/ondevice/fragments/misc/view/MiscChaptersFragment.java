package catgirl.oneesama.activity.main.fragments.ondevice.fragments.misc.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import catgirl.oneesama.R;
import catgirl.oneesama.data.controller.ChaptersController;
import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.data.model.ChapterAuthor;
import catgirl.oneesama.activity.chapterlist.fragments.chapterlist.view.ChapterViewHolder;
import catgirl.oneesama.activity.common.view.SimpleRecyclerFragment;
import catgirl.oneesama.activity.main.MainActivityModule;
import catgirl.oneesama.activity.main.fragments.ondevice.OnDeviceFragment;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.misc.MiscChaptersComponent;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.misc.MiscChaptersModule;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.misc.presenter.MiscChaptersPresenter;
import catgirl.oneesama.application.Application;
import catgirl.oneesama.activity.legacyreader.activityreader.ReaderActivity;

public class MiscChaptersFragment
        extends SimpleRecyclerFragment<ChapterAuthor, ChapterViewHolder, MiscChaptersPresenter, MiscChaptersComponent>
        implements MiscChaptersView
{
    // TODO: The first two blocks are boilerplate, maybe it makes sense to move third into a View
    // TODO: alternatively find a way to get rid of said boilerplate

    // Component initialization //

    @Override
    protected MiscChaptersComponent createComponent() {
        return Application.getApplicationComponent().plus(new MainActivityModule()).plus(new MiscChaptersModule());
    }

    @Override
    protected void onComponentCreated() {
        getComponent().inject(this);
    }

    // Presenter initialization //

    @Inject ChaptersController chaptersController;

    @Override
    protected MiscChaptersPresenter createPresenter() {
        return getComponent().getPresenter();
    }

    // View //

    @Override
    public ChapterViewHolder createViewHolder(ViewGroup parent) {
        return new ChapterViewHolder(
                getActivity().getLayoutInflater().inflate(R.layout.item_chapter_author, parent, false),
                recyclerView,
                chaptersController,
                compositeSubscription,
                true);
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
    public View getEmptyMessage(ViewGroup parent) {
        View emptyMessage = getActivity().getLayoutInflater().inflate(R.layout.common_empty_browse, parent, false);

        emptyMessage.findViewById(R.id.Common_Empty_BrowseButton)
                .setOnClickListener(button -> ((OnDeviceFragment.OnDeviceFragmentDelegate) getActivity()).onBrowseButtonPressed());

        ((TextView) emptyMessage.findViewById(R.id.Common_Empty_MessageText))
                .setText(R.string.page_misc_no_chapters);

        return emptyMessage;
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
    }
}
