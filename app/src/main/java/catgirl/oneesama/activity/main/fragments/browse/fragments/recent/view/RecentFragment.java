package catgirl.oneesama.activity.main.fragments.browse.fragments.recent.view;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.mvp.BasePresenterFragment;
import catgirl.oneesama.R;
import catgirl.oneesama.activity.common.view.LazyLoadFragment;
import catgirl.oneesama.activity.main.MainActivity;
import catgirl.oneesama.activity.main.MainActivityModule;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.RecentComponent;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.RecentModule;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.data.model.RecentChapter;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.presenter.RecentPresenter;
import catgirl.oneesama.application.Application;
import catgirl.oneesama.data.controller.ChaptersController;
import rx.subscriptions.CompositeSubscription;

public class RecentFragment
        extends LazyLoadFragment<RecentChapter, RecentPresenter, RecentComponent>
        implements RecentView {

    @Override
    protected RecentComponent createComponent() {
        return Application.getApplicationComponent().plus(new MainActivityModule()).plus(new RecentModule());
    }

    @Override
    protected void onComponentCreated() {
        getComponent().inject(this);
    }

    @Override
    protected RecentPresenter createPresenter() {
        return getComponent().getPresenter();
    }

    // View //

    @Inject ChaptersController chaptersController;
    CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
    }

    @Override
    protected int getItemCount() {
        return getPresenter().getItemCount();
    }

    @Override
    protected long getItemId(int position) {
        return getPresenter().getItem(position).permalink.hashCode();
    }

    @Override
    protected void loadNew() {
        getPresenter().loadNew();
    }

    @Override
    protected void loadMore() {
        getPresenter().loadMore();
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolder(ViewGroup parent) {
        return new RecentViewHolder(
            getActivity().getLayoutInflater().inflate(R.layout.item_recent_chapter, parent, false),
            chaptersController,
            compositeSubscription);
    }

    @Override
    protected void bindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RecentViewHolder) holder).bind(
                getPresenter().getItem(position),
                () -> getPresenter().itemClicked(position));
    }

    @Override
    protected RecyclerView.ViewHolder createErrorViewHolder(ViewGroup parent) {
        View errorView = getActivity().getLayoutInflater().inflate(R.layout.item_error, parent, false);
        errorView.findViewById(R.id.ReloadButton).setOnClickListener(view -> {
            onErrorReloadPressed();
        });
        return new ErrorViewHolder(errorView);
    }

    @Override
    protected View getEmptyMessage(ViewGroup parent) {
        // This can't really happen with recent chapters so w/e
        return new View(parent.getContext());
    }

    @Override
    protected void showMoreItemsErrorToast() {
        if (getPresenter().getItemCount() == 0) {
            Toast.makeText(getContext(), R.string.fragment_recent_loading_first_items_error, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), R.string.fragment_recent_loading_more_items_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void showNewItemsErrorToast() {
        Toast.makeText(getContext(), R.string.fragment_recent_loading_new_items_error, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void loadChapter(String permalink) {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(permalink));
        getContext().startActivity(intent);
    }
}
