package catgirl.oneesama2.ui.fragment;

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
import catgirl.oneesama.R;
import catgirl.oneesama.model.chapter.ui.UiChapter;
import catgirl.oneesama2.ui.presenter.ChapterListPresenter;
import catgirl.oneesama2.ui.presenter.ChapterListPresenterFactory;

public class ChapterListFragment extends BasePresenterFragment<ChapterListPresenter> implements IChapterListView {

    ViewGroup view;

    // Presenter initialization //

    @Inject ChapterListPresenterFactory presenterFactory;

    @Override
    protected ChapterListPresenter createPresenter() {
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

        view = (ViewGroup) inflater.inflate(R.layout.fragment_ondevice_page_common, container, false);
        ButterKnife.bind(this, view);

        emptyMessage = getActivity().getLayoutInflater().inflate(R.layout.common_empty_browse, view, false);



        // Empty
        Button button = (Button) emptyMessage.findViewById(R.id.Common_Empty_BrowseButton);

        button.setOnClickListener(b -> getActivity().onBackPressed());
        button.setText(R.string.activity_chapters_go_back);

        ((TextView) emptyMessage.findViewById(R.id.Common_Empty_MessageText))
                .setText(R.string.activity_chapters_no_comics);

        return view;
    }

    @Override
    public void showContents(@NonNull List<UiChapter> chapters, @NonNull String seriesName) {
        if (chapters.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyMessage.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public void showSeriesImage(@NonNull Bitmap seriesImage, boolean animateIn) {

    }
}
