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
        extends BasePresenterFragment<RecentPresenter, RecentComponent>
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

    static final int LOADED = 0;
    static final int LOADING = 1;
    static final int ERROR = 2;

    static final int needItemsThreshold = 5;

    int mode = LOADING;

    // Ugly hack to avoid endless requests
    // When we're loading a request initiated by the "Load more" button we don't want RecyclerView
    // posting more requests to the queue - if e.g. the network is down they simply come in too fast.
    boolean buttonAskedForMore = false;

    // Another ugly hack to make recycler scroll back upwards if we're loading from an error with
    // no items currently present: recycler tries to keep "looking" at the error item and scrolls down with it.
    boolean shouldScrollUp = false;

    Handler handler = new Handler();

    @Bind(R.id.Recycler) protected RecyclerView recyclerView;
    @Bind(R.id.Loading) protected View loadingView;
    @Bind(R.id.SwipeLayout) protected SwipeRefreshLayout swipeRefreshLayout;

    View emptyMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_refreshable_recycler, container, false);
        ButterKnife.bind(this, view);

        recyclerView.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                switch (viewType) {
                    case LOADED:
                        return RecentFragment.this.createViewHolder(parent);
                    case LOADING:
                        return new LoadingViewHolder(getActivity().getLayoutInflater().inflate(R.layout.item_loading, parent, false));
                    case ERROR:
                        return RecentFragment.this.createErrorViewHolder(parent);
                }
                return null;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                if (getItemViewType(position) == 0) {
                    RecentFragment.this.bindViewHolder((RecentViewHolder) holder, position);
                }

                if (mode == LOADING && position + needItemsThreshold >= getItemCount() && !buttonAskedForMore) {
                    handler.post(() -> getPresenter().loadMore());
                }
            }

            @Override
            public int getItemCount() {
                return RecentFragment.this.getItemCount() + (mode != LOADED ? 1 : 0);
            }

            @Override
            public int getItemViewType(int position) {
                if (position == getItemCount() - 1) {
                    // If it's the last item and we need loading or error at the bottom return corresponding type
                    return mode;
                } else {
                    // a.k.a. normal item type
                    return 0;
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Empty
        emptyMessage = getEmptyMessage(view);
        view.addView(emptyMessage);
        emptyMessage.setVisibility(View.GONE);

        swipeRefreshLayout.setOnRefreshListener(() -> getPresenter().loadNew());

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
    }

    protected View getEmptyMessage(ViewGroup parent) {
        // This can't really happen with recent chapters so w/e
        return new View(parent.getContext());
    }

    protected int getItemCount() {
        return getPresenter().getItemCount();
    }

    protected void bindViewHolder(RecentViewHolder holder, int position) {
        holder.bind(getPresenter().getItem(position), () -> {
            getPresenter().itemClicked(position);
        });
    }

    protected RecentViewHolder createViewHolder(ViewGroup parent) {
        return new RecentViewHolder(
                getActivity().getLayoutInflater().inflate(R.layout.item_recent_chapter, parent, false),
                chaptersController,
                compositeSubscription);
    }

    private RecyclerView.ViewHolder createErrorViewHolder(ViewGroup parent) {
        View errorView = getActivity().getLayoutInflater().inflate(R.layout.item_error, parent, false);
        errorView.findViewById(R.id.ReloadButton).setOnClickListener(view -> {
            if (getPresenter().getItemCount() == 0) {
                shouldScrollUp = true;
            }
            buttonAskedForMore = true;
            getPresenter().loadMore();
        });
        return new ErrorViewHolder(errorView);
    }

    public void testEmpty(boolean finished) {
        loadingView.setVisibility(View.GONE);

        if (finished && getPresenter().getItemCount() == 0) {
            swipeRefreshLayout.setVisibility(View.GONE);
            emptyMessage.setVisibility(View.VISIBLE);
        } else {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            emptyMessage.setVisibility(View.GONE);
        }
    }

    // Only supposed to be called when fragment is first created or re-created
    // OR when something in Realm changed
    @Override
    public void showExistingItems(List<RecentChapter> items, boolean finished) {
        mode = finished ? LOADED : LOADING;
        recyclerView.getAdapter().notifyDataSetChanged();

        testEmpty(finished);
    }

    @Override
    public void showMoreItems(List<RecentChapter> items, boolean finished) {
        recyclerView.getAdapter().notifyItemRangeInserted(getPresenter().getItemCount() - items.size(), items.size());
        if (shouldScrollUp) {
            recyclerView.getLayoutManager().scrollToPosition(0);
        }

        mode = finished ? LOADED : LOADING;

        buttonAskedForMore = false;
        shouldScrollUp = false;

        if (finished)
            recyclerView.getAdapter().notifyItemRemoved(recyclerView.getAdapter().getItemCount() - 1);

        testEmpty(finished);
    }

    // More recent items appearing on top
    @Override
    public void showNewItems(List<RecentChapter> items) {
        swipeRefreshLayout.setRefreshing(false);

        boolean shouldScrollUpwards = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition() == 0;

        recyclerView.getAdapter().notifyItemRangeInserted(0, items.size());

        if (shouldScrollUpwards)
            recyclerView.getLayoutManager().scrollToPosition(0);
    }

    // Only supposed to be called when fragment is re-created
    @Override
    public void showLoadingNewItems() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void showMoreItemsError(boolean showToast) {
        mode = ERROR;
        // This can only happen if the very last item is "Loading"
        recyclerView.getAdapter().notifyItemChanged(recyclerView.getAdapter().getItemCount() - 1);

        buttonAskedForMore = false;

        if (showToast) {
            if (getPresenter().getItemCount() == 0) {
                Toast.makeText(getContext(), R.string.fragment_recent_loading_first_items_error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.fragment_recent_loading_more_items_error, Toast.LENGTH_SHORT).show();
            }
        }

        testEmpty(false);
    }

    @Override
    public void showNewItemsError() {
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getContext(), R.string.fragment_recent_loading_new_items_error, Toast.LENGTH_SHORT).show();
    }

    // Call if not calling showExistingItems just so it refreshes itself once
    @Override
    public void showInitialState() {
        loadingView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.GONE);

        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void loadChapter(String permalink) {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(permalink));
        getContext().startActivity(intent);
    }

    @Override
    public void showLoadingMoreItems() {
        mode = LOADING;
        recyclerView.getAdapter().notifyItemChanged(recyclerView.getAdapter().getItemCount() - 1);
    }

    @Override
    public void updateExistingItems(List<RecentChapter> items) {
        recyclerView.getAdapter().notifyDataSetChanged();
    }
}
