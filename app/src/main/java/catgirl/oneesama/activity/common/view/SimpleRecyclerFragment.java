package catgirl.oneesama.activity.common.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import rx.subscriptions.CompositeSubscription;

/**
 * A class with boilerplate for displaying a simple RecyclerView or an empty message
 * @param <T> View model class
 * @param <VH> ViewHolder class
 * @param <P> Presenter class - for BasePresenterFragment
 * @param <C> Component class - for BasePresenterFragment
 */
public abstract class SimpleRecyclerFragment<T, VH extends RecyclerView.ViewHolder, P extends Presenter, C>
        extends BasePresenterFragment<P, C>
        implements SimpleRecyclerView<T> {

    protected CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Bind(R.id.Fragment_OnDevice_CommonRecycler)
    protected RecyclerView recyclerView;

    View emptyMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_ondevice_page_common, container, false);
        ButterKnife.bind(this, view);

        recyclerView.setAdapter(new RecyclerView.Adapter<VH>() {
            @Override
            public VH onCreateViewHolder(ViewGroup parent, int viewType) {
                return SimpleRecyclerFragment.this.createViewHolder(parent);
            }

            @Override
            public void onBindViewHolder(VH holder, int position) {
                SimpleRecyclerFragment.this.bindViewHolder(holder, position);
            }

            @Override
            public int getItemCount() {
                return SimpleRecyclerFragment.this.getItemCount();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Empty
        emptyMessage = getEmptyMessage(view);
        view.addView(emptyMessage);
        emptyMessage.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void showContents(@NonNull List<T> contents) {
        if (contents.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyMessage.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyMessage.setVisibility(View.GONE);
        }

        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
    }

    protected abstract View getEmptyMessage(ViewGroup parent);

    protected abstract int getItemCount();

    protected abstract void bindViewHolder(VH holder, int position);

    protected abstract VH createViewHolder(ViewGroup parent);
}
