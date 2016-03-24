package catgirl.oneesama.activity.main.fragments.browse.fragments.series.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import catgirl.oneesama.R;
import catgirl.oneesama.activity.common.view.LazyLoadFragment;
import catgirl.oneesama.activity.main.MainActivityModule;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.view.ErrorViewHolder;
import catgirl.oneesama.activity.main.fragments.browse.fragments.series.BrowseSeriesComponent;
import catgirl.oneesama.activity.main.fragments.browse.fragments.series.BrowseSeriesModule;
import catgirl.oneesama.activity.main.fragments.browse.fragments.series.data.model.SeriesItem;
import catgirl.oneesama.activity.main.fragments.browse.fragments.series.presenter.SeriesPresenter;
import catgirl.oneesama.application.Application;

public class SeriesFragment
        extends LazyLoadFragment<SeriesItem, SeriesPresenter, BrowseSeriesComponent>
        implements SeriesView {

    @Override
    protected BrowseSeriesComponent createComponent() {
        return Application.getApplicationComponent().plus(new MainActivityModule()).plus(new BrowseSeriesModule());
    }

    @Override
    protected void onComponentCreated() {
        getComponent().inject(this);
    }

    @Override
    protected SeriesPresenter createPresenter() {
        return getComponent().getPresenter();
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
        return new SeriesViewHolder(
                getActivity().getLayoutInflater().inflate(R.layout.item_series, parent, false));
    }

    @Override
    protected void bindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SeriesViewHolder) holder).bind(
                getPresenter().getItem(position),
                () -> getPresenter().itemClicked(position));
    }

    @Override
    protected RecyclerView.ViewHolder createErrorViewHolder(ViewGroup parent) {
        View errorView = getActivity().getLayoutInflater().inflate(R.layout.item_error_try_again, parent, false);
        errorView.findViewById(R.id.ReloadButton).setOnClickListener(view -> {
            onErrorReloadPressed();
        });
        return new ErrorViewHolder(errorView);
    }

    @Override
    protected View getEmptyMessage(ViewGroup parent) {
        return new View(parent.getContext());
    }

    @Override
    protected void showMoreItemsErrorToast() {
        Toast.makeText(getActivity(), R.string.fragment_browseseries_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void showNewItemsErrorToast() {
        Toast.makeText(getActivity(), R.string.fragment_browseseries_refresh_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void switchToSeries(String seriesPermalink, String title) {
        Toast.makeText(getActivity(), "Clicked on " + seriesPermalink, Toast.LENGTH_SHORT).show();
    }
}
