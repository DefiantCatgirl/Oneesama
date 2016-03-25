package catgirl.oneesama.activity.browseseriespage.fragment.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.mvp.implementations.BasePresenterFragment;
import catgirl.oneesama.R;
import catgirl.oneesama.activity.browseseriespage.BrowseSeriesPageActivity;
import catgirl.oneesama.activity.browseseriespage.BrowseSeriesPageActivityModule;
import catgirl.oneesama.activity.browseseriespage.fragment.BrowseSeriesPageComponent;
import catgirl.oneesama.activity.browseseriespage.fragment.BrowseSeriesPageModule;
import catgirl.oneesama.activity.browseseriespage.fragment.presenter.BrowseSeriesPagePresenter;
import catgirl.oneesama.activity.main.fragments.browse.fragments.series.view.SeriesViewHolder;
import catgirl.oneesama.application.Application;

import static android.support.v4.view.ViewCompat.animate;

public class BrowseSeriesPageFragment
        extends BasePresenterFragment<BrowseSeriesPagePresenter, BrowseSeriesPageComponent>
        implements BrowseSeriesPageView {

    // Component

    @Override
    protected BrowseSeriesPageComponent createComponent() {
        return Application.getApplicationComponent()
                .plus(new BrowseSeriesPageActivityModule())
                .plus(new BrowseSeriesPageModule());
    }

    @Override
    protected void onComponentCreated() {
        getComponent().inject(this);
    }

    @Override
    protected BrowseSeriesPagePresenter createPresenter() {
        BrowseSeriesPagePresenter presenter = getComponent().createPresenter();
        presenter.configure(
                getArguments().getString(BrowseSeriesPageActivity.SERIES_PERMALINK),
                getArguments().getString(BrowseSeriesPageActivity.SERIES_TITLE));
        return presenter;
    }

    // View

    @Bind(R.id.Recycler) public RecyclerView recyclerView;
    @Bind(R.id.Toolbar) public Toolbar toolbar;
    @Bind(R.id.ToolbarBackground) public ImageView toolbarBackground;
    @Bind(R.id.AppBar) public AppBarLayout appBar;
//    @Bind(R.id.ToolbarBackgroundShadow) public View shadow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_browse_series_page, container, false);
        ButterKnife.bind(this, view);

        RecyclerView.Adapter<RecyclerView.ViewHolder> adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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

            @Override
            public long getItemId(int position) {
                return BrowseSeriesPageFragment.this.getItemId(position);
            }
        };

        adapter.setHasStableIds(true);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        return view;
    }

    private long getItemId(int position) {
        return 0;//getPresenter().getItem(position).permalink.hashCode();
    }

    private int getItemCount() {
        return 1;
    }

    private void bindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    private RecyclerView.ViewHolder createViewHolder(ViewGroup parent) {
        return new SeriesViewHolder(
                getActivity().getLayoutInflater().inflate(R.layout.item_series, parent, false));
    }

    @Override
    public void setTitle(String title) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

    @Override
    public void loadCover(String url) {
//        Picasso.with(getActivity()).load(url).into(toolbarBackground, new Callback() {
//            @Override
//            public void onSuccess() {
//                if (shadow.getVisibility() != View.VISIBLE) {
//                    shadow.setVisibility(View.VISIBLE);
//                    ViewCompat.setAlpha(shadow, 0f);
//                    animate(shadow).alpha(1f).setListener(new ViewPropertyAnimatorListener() {
//                        @Override public void onAnimationStart(View view) {}
//                        @Override public void onAnimationCancel(View view) {}
//
//                        @Override
//                        public void onAnimationEnd(View view) {
//                            ViewCompat.setAlpha(shadow, 1f);
//                        }
//                    }).start();
//                }
//            }
//
//            @Override
//            public void onError() {
//                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
