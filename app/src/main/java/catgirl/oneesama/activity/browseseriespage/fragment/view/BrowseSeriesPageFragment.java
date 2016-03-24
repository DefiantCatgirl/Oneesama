package catgirl.oneesama.activity.browseseriespage.fragment.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.mvp.implementations.BasePresenterFragment;
import catgirl.oneesama.R;
import catgirl.oneesama.activity.browseseriespage.fragment.BrowseSeriesPageComponent;
import catgirl.oneesama.activity.browseseriespage.fragment.presenter.BrowseSeriesPagePresenter;
import catgirl.oneesama.activity.main.fragments.browse.fragments.series.view.SeriesViewHolder;

public class BrowseSeriesPageFragment
        extends BasePresenterFragment<BrowseSeriesPagePresenter, BrowseSeriesPageComponent>
        implements BrowseSeriesPageView {

    @Override
    protected BrowseSeriesPageComponent createComponent() {
        return new BrowseSeriesPageComponent() {

        };
    }

    @Override
    protected void onComponentCreated() {

    }

    @Override
    protected BrowseSeriesPagePresenter createPresenter() {
        return new BrowseSeriesPagePresenter();
    }


    @Bind(R.id.Recycler) public RecyclerView recyclerView;
    @Bind(R.id.Toolbar) public Toolbar toolbar;
    @Bind(R.id.ToolbarBackground) public ImageView toolbarBackground;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_browse_series_page, container, false);
        ButterKnife.bind(this, view);

        recyclerView.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return BrowseSeriesPageFragment.this.createViewHolder(parent);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                BrowseSeriesPageFragment.this.bindViewHolder(holder, position);
            }

            @Override
            public int getItemCount() {
                return BrowseSeriesPageFragment.this.getItemCount();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Test title");

        Picasso.with(getActivity()).load("http://dynasty-scans.com/system/tag_contents_covers/000/004/136/medium/i166035.jpg?1392680014").into(toolbarBackground);

        return view;
    }

    private int getItemCount() {
        return 100;
    }

    private void bindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    private RecyclerView.ViewHolder createViewHolder(ViewGroup parent) {
        return new SeriesViewHolder(
                getActivity().getLayoutInflater().inflate(R.layout.item_series, parent, false));
    }
}
