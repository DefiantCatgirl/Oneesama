package catgirl.oneesama.activity.main.fragments.browse.legacy.pages;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class CommonBrowsePage<T, VH extends RecyclerView.ViewHolder> extends Fragment {
    @Bind(R.id.Fragment_OnDevice_CommonRecycler)
    protected RecyclerView recycler;
    @Bind(R.id.Fragment_OnDevice_Loading)
    protected View loading;
    View emptyContainer;
    View errorContainer;
    boolean errorState = false;

    static Map<String, Observable<Object>> observables = new HashMap<>();
    Subscription subscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_ondevice_page_common, container, false);
        ButterKnife.bind(this, view);

        emptyContainer = getEmptyMessage(view);
        errorContainer = getErrorMessage(view, () -> {
            errorState = false;
            showLoadingAndLoad(); });
        view.addView(emptyContainer);
        view.addView(errorContainer);

        if (savedInstanceState != null)
            errorState = savedInstanceState.getBoolean("ERROR_STATE", false);

        if (errorState) {
            showError();
        } else {
            if (getCache() == null) {
                showLoadingAndLoad();
            } else {
                if (getCache().isEmpty()) {
                    showEmpty();
                } else {
                    showRecycler();
                }
            }
        }

        recycler.setItemAnimator(null);

        recycler.setAdapter(new RecyclerView.Adapter<VH>() {

            @Override
            public VH onCreateViewHolder(ViewGroup parent, int viewType) {
                return provideViewHolder(parent);
            }

            @Override
            public void onBindViewHolder(VH holder, int position) {
                CommonBrowsePage.this.bindViewHolder(holder, position, getCache().get(position));
            }

            @Override
            public int getItemCount() {
                return getCache() == null ? 0 : getCache().size();
            }
        });

        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    public void showError() {
        errorState = true;
        emptyContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.VISIBLE);
        recycler.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
    }

    public void showEmpty() {
        emptyContainer.setVisibility(View.VISIBLE);
        errorContainer.setVisibility(View.GONE);
        recycler.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
    }

    public void showRecycler() {
        emptyContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);
        recycler.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
    }

    public void showLoadingAndLoad() {
        emptyContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);
        recycler.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);

        Observable<Object> observable;
        if (observables.containsKey(this.getClass().getName())) {
            observable = observables.get(this.getClass().getName());
        } else {
            observable = Observable.fromCallable(() -> {
                loadData();
                return null;
            });
            observables.put(this.getClass().getName(), observable);
        }

        subscription = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    showRecycler();
                    observables.remove(this.getClass().getName());
                    onCacheUpdated();
                }, error -> {
                    error.printStackTrace();
                    showError();
                });
    }

    public void onCacheUpdated() {
        if (getCache().isEmpty())
            showEmpty();
        else
            showRecycler();

        recycler.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(subscription != null)
            subscription.unsubscribe();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        recycler.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("ERROR_STATE", errorState);
        super.onSaveInstanceState(outState);
    }

    public abstract List<T> getCache();

    public abstract void loadData();

    public abstract VH provideViewHolder(ViewGroup parent);

    public abstract void bindViewHolder(VH holder, int position, T data);

    public View getEmptyMessage(ViewGroup parent) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.common_empty_browse, parent, false);
        view.findViewById(R.id.Common_Empty_BrowseButton).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.Common_Empty_MessageText)).setText(R.string.browse_empty_message);
        return view;
    }

    public View getErrorMessage(ViewGroup parent, Runnable reloadAction) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.common_empty_error, parent, false);
        view.findViewById(R.id.Common_Empty_BrowseButton).setOnClickListener(v -> reloadAction.run());
        ((TextView) view.findViewById(R.id.Common_Empty_MessageText)).setText(R.string.default_error_message);
        return view;
    }
}
