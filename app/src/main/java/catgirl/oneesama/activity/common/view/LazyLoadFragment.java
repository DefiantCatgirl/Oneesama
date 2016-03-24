package catgirl.oneesama.activity.common.view;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.mvp.implementations.BasePresenterFragment;
import catgirl.mvp.Presenter;
import catgirl.oneesama.R;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.view.LoadingViewHolder;

public abstract class LazyLoadFragment<T, P extends Presenter, C>
        extends BasePresenterFragment<P, C>
        implements LazyLoadView<T> {

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

    @Bind(R.id.Recycler) public RecyclerView recyclerView;
    @Bind(R.id.Loading) public View loadingView;
    @Bind(R.id.SwipeLayout) public SwipeRefreshLayout swipeRefreshLayout;

    View emptyMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_refreshable_recycler, container, false);
        ButterKnife.bind(this, view);

        RecyclerView.Adapter<RecyclerView.ViewHolder> adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                switch (viewType) {
                    case LOADED:
                        return LazyLoadFragment.this.createViewHolder(parent);
                    case LOADING:
                        return new LoadingViewHolder(getActivity().getLayoutInflater().inflate(R.layout.item_loading, parent, false));
                    case ERROR:
                        return LazyLoadFragment.this.createErrorViewHolder(parent);
                }
                return null;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                if (getItemViewType(position) == 0) {
                    LazyLoadFragment.this.bindViewHolder(holder, position);
                }

                if (mode == LOADING && position + needItemsThreshold >= getItemCount() && !buttonAskedForMore) {
                    handler.post(LazyLoadFragment.this::loadMore);
                }
            }

            @Override
            public int getItemCount() {
                return LazyLoadFragment.this.getItemCount() + (mode != LOADED ? 1 : 0);
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

            @Override
            public long getItemId(int position) {
                if (position < getItemCount() - 1)
                    return LazyLoadFragment.this.getItemId(position);
                else
                    return RecyclerView.NO_ID;
            }
        };

        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Empty
        emptyMessage = getEmptyMessage(view);
        view.addView(emptyMessage);
        emptyMessage.setVisibility(View.GONE);

        swipeRefreshLayout.setOnRefreshListener(this::loadNew);

        return view;
    }

    protected abstract int getItemCount();
    protected abstract long getItemId(int position);
    protected abstract void loadNew();
    protected abstract void loadMore();
    protected abstract RecyclerView.ViewHolder createViewHolder(ViewGroup parent);
    protected abstract void bindViewHolder(RecyclerView.ViewHolder holder, int position);
    protected abstract RecyclerView.ViewHolder createErrorViewHolder(ViewGroup parent);
    protected abstract View getEmptyMessage(ViewGroup parent);
    protected abstract void showMoreItemsErrorToast();
    protected abstract void showNewItemsErrorToast();

    public void testEmpty(boolean finished) {
        loadingView.setVisibility(View.GONE);

        if (finished && getItemCount() == 0) {
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
    public void showExistingItems(List<T> items, boolean finished) {
        mode = finished ? LOADED : LOADING;
        recyclerView.getAdapter().notifyDataSetChanged();

        testEmpty(finished);
    }

    @Override
    public void showMoreItems(List<T> items, boolean finished) {
        recyclerView.getAdapter().notifyItemRangeInserted(getItemCount() - items.size(), items.size());
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
    public void showNewItems(List<T> items) {
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
    public void hideLoadingNewItems() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showMoreItemsError(boolean showToast) {
        mode = ERROR;
        // This can only happen if the very last item is "Loading"
        recyclerView.getAdapter().notifyItemChanged(recyclerView.getAdapter().getItemCount() - 1);

        buttonAskedForMore = false;

        if (showToast) {
            showMoreItemsErrorToast();
        }

        testEmpty(false);
    }

    @Override
    public void showNewItemsError() {
        swipeRefreshLayout.setRefreshing(false);
        showNewItemsErrorToast();
    }

    // Call if not calling showExistingItems just so it refreshes itself once
    @Override
    public void showInitialState() {
        loadingView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.GONE);

        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void showLoadingMoreItems() {
        mode = LOADING;
        recyclerView.getAdapter().notifyItemChanged(recyclerView.getAdapter().getItemCount() - 1);
    }

    @Override
    public void updateExistingItems(List<T> items) {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public void onErrorReloadPressed() {
        if (getItemCount() == 0) {
            shouldScrollUp = true;
        }
        buttonAskedForMore = true;
        loadMore();
    }
}
