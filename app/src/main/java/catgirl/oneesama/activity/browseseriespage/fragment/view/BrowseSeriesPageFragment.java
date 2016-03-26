package catgirl.oneesama.activity.browseseriespage.fragment.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
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

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.mvp.implementations.BasePresenterFragment;
import catgirl.oneesama.R;
import catgirl.oneesama.activity.browseseriespage.BrowseSeriesPageActivity;
import catgirl.oneesama.activity.browseseriespage.BrowseSeriesPageActivityModule;
import catgirl.oneesama.activity.browseseriespage.fragment.BrowseSeriesPageComponent;
import catgirl.oneesama.activity.browseseriespage.fragment.BrowseSeriesPageModule;
import catgirl.oneesama.activity.browseseriespage.fragment.data.model.BrowseSeriesPageChapter;
import catgirl.oneesama.activity.browseseriespage.fragment.data.model.BrowseSeriesPageVolume;
import catgirl.oneesama.activity.browseseriespage.fragment.presenter.BrowseSeriesPagePresenter;
import catgirl.oneesama.application.Application;
import catgirl.oneesama.data.controller.ChaptersController;
import rx.subscriptions.CompositeSubscription;

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

    @Inject ChaptersController chaptersController;
    CompositeSubscription subscription = new CompositeSubscription();

    @Bind(R.id.Recycler) public RecyclerView recyclerView;
    @Bind(R.id.Loading) public View loading;
    @Bind(R.id.ErrorLayout) public View errorLayout;
    @Bind(R.id.ReloadButton) public View reloadButton;

    @Bind(R.id.Toolbar) public Toolbar toolbar;
    @Bind(R.id.ToolbarBackground) public ImageView toolbarBackground;
    @Bind(R.id.AppBar) public AppBarLayout appBar;
    @Bind(R.id.CollapsingToolbar) public CollapsingToolbarLayout collapsingToolbar;
    @Bind(R.id.ToolbarBackgroundShadow) public View shadow;

    static final int TYPE_CHAPTER = 0;
    static final int TYPE_VOLUME = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_browse_series_page, container, false);
        ButterKnife.bind(this, view);

        RecyclerView.Adapter<RecyclerView.ViewHolder> adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return BrowseSeriesPageFragment.this.createViewHolder(parent, viewType);
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

            @Override
            public int getItemViewType(int position) {
                return BrowseSeriesPageFragment.this.getItemViewType(position);
            }
        };

        adapter.setHasStableIds(true);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
    }

    private long getItemId(int position) {
        return position;
    }

    private int getItemViewType(int position) {
        return getPresenter().getItem(position) instanceof BrowseSeriesPageChapter ?
                TYPE_CHAPTER : TYPE_VOLUME;
    }

    private int getItemCount() {
        return getPresenter().getItemCount();
    }

    private void bindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);

        if (type == TYPE_CHAPTER) {
            ((BrowseSeriesPageChapterViewHolder) holder)
                    .bind((BrowseSeriesPageChapter) getPresenter().getItem(position));
        } else if (type == TYPE_VOLUME) {
            ((BrowseSeriesPageVolumeViewHolder) holder)
                    .bind((BrowseSeriesPageVolume) getPresenter().getItem(position));
        }
    }

    private RecyclerView.ViewHolder createViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_CHAPTER) {
            return new BrowseSeriesPageChapterViewHolder(
                    getActivity().getLayoutInflater().inflate(R.layout.item_chapter_browse_series, parent, false),
                    chaptersController,
                    subscription);
        } else if (viewType == TYPE_VOLUME) {
            return new BrowseSeriesPageVolumeViewHolder(
                    getActivity().getLayoutInflater().inflate(R.layout.item_volume, parent, false));
        }
        return null;
    }

    @Override
    public void setTitle(String title) {
        collapsingToolbar.setTitle(title);
    }

    @Override
    public void loadCover(String url) {
        Picasso.with(getActivity()).load(url).into(toolbarBackground, new Callback() {
            @Override
            public void onSuccess() {
                if (shadow.getVisibility() != View.VISIBLE) {
                    shadow.setVisibility(View.VISIBLE);
                    ViewCompat.setAlpha(shadow, 0f);
                    animate(shadow).alpha(1f).setListener(new ViewPropertyAnimatorListener() {
                        @Override public void onAnimationStart(View view) {}
                        @Override public void onAnimationCancel(View view) {}

                        @Override
                        public void onAnimationEnd(View view) {
                            ViewCompat.setAlpha(shadow, 1f);
                        }
                    }).start();
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void showContents(List<Object> chapters) {
        recyclerView.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.INVISIBLE);

        recyclerView.getAdapter().notifyItemRangeInserted(0, chapters.size());
    }

    @Override
    public void showError() {
        recyclerView.setVisibility(View.INVISIBLE);
        errorLayout.setVisibility(View.VISIBLE);
        loading.setVisibility(View.INVISIBLE);

        recyclerView.getAdapter().notifyDataSetChanged();

        reloadButton.setOnClickListener(view -> {
            getPresenter().requestData();
        });
    }

    @Override
    public void showLoading() {
        recyclerView.setVisibility(View.INVISIBLE);
        errorLayout.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);

        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void updateExistingItems(List<Object> chapters) {
        recyclerView.getAdapter().notifyDataSetChanged();
    }
}
