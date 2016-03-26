package catgirl.oneesama.activity.main.fragments.history.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import catgirl.oneesama.R;
import catgirl.oneesama.activity.common.view.SimpleRecyclerFragment;
import catgirl.oneesama.activity.legacyreader.activityreader.ReaderActivity;
import catgirl.oneesama.activity.main.MainActivityModule;
import catgirl.oneesama.activity.main.fragments.history.HistoryComponent;
import catgirl.oneesama.activity.main.fragments.history.HistoryModule;
import catgirl.oneesama.activity.main.fragments.history.presenter.HistoryPresenter;
import catgirl.oneesama.activity.main.fragments.ondevice.OnDeviceFragment;
import catgirl.oneesama.application.Application;
import catgirl.oneesama.data.controller.ChaptersController;
import catgirl.oneesama.data.model.chapter.ui.UiChapter;

public class HistoryFragment
        extends SimpleRecyclerFragment<UiChapter, HistoryViewHolder, HistoryPresenter, HistoryComponent>
        implements HistoryView {

    // Component initialization //

    @Override
    protected HistoryComponent createComponent() {
        return Application.getApplicationComponent().plus(new MainActivityModule()).plus(new HistoryModule());
    }

    @Override
    protected void onComponentCreated() {
        getComponent().inject(this);
    }

    // Presenter initialization //

    @Inject
    ChaptersController chaptersController;

    @Override
    protected HistoryPresenter createPresenter() {
        return getComponent().createPresenter();
    }

    // View //

    boolean justStarted = true;

    @Override
    public void onResume() {
        super.onResume();

        Log.v("HistoryFragment", toString() + ": onResume; justStarted: " + justStarted);

        if (!justStarted)
            getPresenter().onResume();

        justStarted = false;
    }

    @Override
    public HistoryViewHolder createViewHolder(ViewGroup parent) {
        return new HistoryViewHolder(
                getActivity().getLayoutInflater().inflate(R.layout.item_chapter_author, parent, false),
                chaptersController,
                compositeSubscription);
    }

    @Override
    public void bindViewHolder(HistoryViewHolder holder, int position) {
        holder.bind(getPresenter().getItem(position), new HistoryViewHolder.ActionDelegate() {
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
                .setText(R.string.page_history_no_chapters);

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

    @Override
    protected ViewGroup getView(LayoutInflater inflater, ViewGroup container) {
        return (ViewGroup) inflater.inflate(R.layout.fragment_history, container, false);
    }
}
