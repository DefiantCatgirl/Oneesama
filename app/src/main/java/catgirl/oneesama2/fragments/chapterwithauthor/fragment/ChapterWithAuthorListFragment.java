package catgirl.oneesama2.fragments.chapterwithauthor.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.mvp.BasePresenterFragment;
import catgirl.oneesama.Application;
import catgirl.oneesama.R;
import catgirl.oneesama.ui.common.chapter.ChapterAuthor;
import catgirl.oneesama2.fragments.chapterwithauthor.ChapterWithAuthorListComponent;
import catgirl.oneesama2.fragments.chapterwithauthor.ChapterWithAuthorListModule;
import catgirl.oneesama2.fragments.chapterwithauthor.presenter.ChapterWithAuthorListPresenter;
import catgirl.oneesama2.fragments.chapterwithauthor.presenter.ChapterWithAuthorListPresenterFactory;

public class ChapterWithAuthorListFragment
        extends BasePresenterFragment<ChapterWithAuthorListPresenter, ChapterWithAuthorListComponent>
        implements ChapterWithAuthorListView
{
    // Component initialization //

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);
    }

    @Override
    protected ChapterWithAuthorListComponent createComponent() {
        return Application.getApplicationComponent().plus(new ChapterWithAuthorListModule());
    }

    // Presenter initialization //

    @Inject
    ChapterWithAuthorListPresenterFactory presenterFactory;

    @Override
    protected ChapterWithAuthorListPresenter createPresenter() {
        return presenterFactory.createPresenter(getArguments().getString("TAG_ID"));
    }

    // View //

    @Bind(R.id.Fragment_OnDevice_CommonRecycler)
    RecyclerView recyclerView;

    View emptyMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_ondevice_page_common, container, false);
        ButterKnife.bind(this, view);

        emptyMessage = getActivity().getLayoutInflater().inflate(R.layout.common_empty_browse, view, false);

        recyclerView.setAdapter(new RecyclerView.Adapter<ChapterWithAuthorViewHolder>() {
            @Override
            public ChapterWithAuthorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ChapterWithAuthorViewHolder(
                        getActivity().getLayoutInflater().inflate(R.layout.item_chapter_author, parent, false),
                        recyclerView);
            }

            @Override
            public void onBindViewHolder(ChapterWithAuthorViewHolder holder, int position) {
                holder.bind(position, getPresenter().getChapter(position), new ChapterWithAuthorViewHolder.IActionDelegate() {
                    @Override
                    public void onClick() {
                        getPresenter().onItemClicked(position);
                    }

                    @Override
                    public void onDelete() {
                        // TODO: presenter should probably be responsible for the delete confirmation dialog
                        getPresenter().onItemDeleted(position);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return getPresenter().getChaptersCount();
            }
        });

        // Empty
        Button button = (Button) emptyMessage.findViewById(R.id.Common_Empty_BrowseButton);

        button.setOnClickListener(b -> getActivity().onBackPressed());
        button.setText(R.string.activity_chapters_go_back);

        ((TextView) emptyMessage.findViewById(R.id.Common_Empty_MessageText))
                .setText(R.string.activity_chapters_no_comics);

        return view;
    }

    @Override
    public void showContents(@NonNull List<ChapterAuthor> chapters, @NonNull String seriesName) {
        if (chapters.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyMessage.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyMessage.setVisibility(View.GONE);
        }

        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void showSeriesImage(@NonNull Bitmap seriesImage, boolean animateIn) {

    }
}
