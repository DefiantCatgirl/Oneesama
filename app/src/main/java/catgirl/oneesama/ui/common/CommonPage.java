package catgirl.oneesama.ui.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import io.realm.Realm;
import io.realm.RealmChangeListener;

public abstract class CommonPage<T, VH extends CommonViewHolder> extends Fragment {

    @Bind(R.id.Fragment_OnDevice_CommonRecycler)
    protected RecyclerView recycler;
    View emptyContainer;

    protected Realm realm;
    int lastCount;

    RealmChangeListener listener;

    List<T> cache = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_ondevice_page_common, container, false);
        ButterKnife.bind(this, view);

        realm = Realm.getDefaultInstance();
        lastCount = getDataItemCount();

        cache = getDataSource();

        listener = (() -> {
            int count = getDataItemCount();
            if (lastCount != count) {

                // TODO remove debug
                Log.v("Log", "Realm changed " + this.getClass().getName());

                cache = getDataSource();

                recycler.getAdapter().notifyDataSetChanged();

                boolean empty = getDataItemCount() == 0;
                emptyContainer.setVisibility(empty ? View.VISIBLE : View.GONE);
                recycler.setVisibility(empty ? View.GONE : View.VISIBLE);

                lastCount = count;
            }
        });
        realm.addChangeListener(listener);

        boolean empty = getDataItemCount() == 0;

        emptyContainer = getEmptyMessage(view);
        view.addView(emptyContainer);

        emptyContainer.setVisibility(empty ? View.VISIBLE : View.GONE);
        recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        recycler.setItemAnimator(null);

        recycler.setAdapter(new RecyclerView.Adapter<VH>() {

            @Override
            public VH onCreateViewHolder(ViewGroup parent, int viewType) {
                return provideViewHolder(parent);
            }

            @Override
            public void onBindViewHolder(VH holder, int position) {
                resetViewHolder(holder, position);
                CommonPage.this.bindViewHolder(holder, position, cache.get(position));
            }

            @Override
            public int getItemCount() {
                return cache.size();
            }
        });

        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onDestroyView() {
        realm.removeChangeListener(listener);
        realm.close();
        super.onDestroyView();
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
        super.onSaveInstanceState(outState);
    }

    public abstract List<T> getDataSource();
    public abstract int getDataItemCount();
    public abstract VH provideViewHolder(ViewGroup parent);
    public abstract void bindViewHolder(VH holder, int position, T data);
    public abstract void resetViewHolder(VH holder, int position);
    public abstract View getEmptyMessage(ViewGroup parent);
}
