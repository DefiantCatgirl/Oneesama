package catgirl.oneesama.activity.main.fragments.ondevice.fragments.series.view;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import catgirl.oneesama.R;
import catgirl.oneesama.activity.chapterlist.ChapterListActivity;
import catgirl.oneesama.activity.main.fragments.ondevice.OnDeviceFragment;
import catgirl.oneesama.activity.common.view.SimpleRecyclerFragment;
import catgirl.oneesama.activity.main.MainActivityModule;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.series.SeriesComponent;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.series.SeriesModule;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.series.data.model.SeriesAuthor;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.series.presenter.SeriesPresenter;
import catgirl.oneesama.application.Application;

public class SeriesFragment
        extends SimpleRecyclerFragment<SeriesAuthor, SeriesViewHolder, SeriesPresenter, SeriesComponent>
        implements SeriesView {

    // Component //

    @Override
    protected SeriesComponent createComponent() {
        // TODO: This is clearly gonna mess up scopes, find a better way
        return Application.getApplicationComponent().plus(new MainActivityModule()).plus(new SeriesModule());
    }

    @Override
    protected void onComponentCreated() {
        getComponent().inject(this);
    }

    // Presenter //

    @Override
    protected SeriesPresenter createPresenter() {
        return getComponent().getPresenter();
    }

    // View //

    @Override
    protected View getEmptyMessage(ViewGroup parent) {
        View emptyMessage = getActivity().getLayoutInflater().inflate(R.layout.common_empty_browse, parent, false);

        emptyMessage.findViewById(R.id.Common_Empty_BrowseButton)
                .setOnClickListener(button -> ((OnDeviceFragment.OnDeviceFragmentDelegate) getActivity()).onBrowseButtonPressed());

        ((TextView) emptyMessage.findViewById(R.id.Common_Empty_MessageText))
                .setText(R.string.page_series_no_chapters);

        return emptyMessage;
    }

    @Override
    protected int getItemCount() {
        return getPresenter().getItemsCount();
    }

    @Override
    protected void bindViewHolder(SeriesViewHolder holder, int position) {
        holder.bind(getPresenter().getItem(position), () -> getPresenter().onItemClicked(holder.getAdapterPosition()));
    }

    @Override
    protected SeriesViewHolder createViewHolder(ViewGroup parent) {
        return new SeriesViewHolder(getActivity().getLayoutInflater().inflate(R.layout.item_series, parent, false));
    }

    @Override
    public void switchToChapterList(int tagId) {
        Intent intent = new Intent(getActivity(), ChapterListActivity.class);
        intent.putExtra(ChapterListActivity.TAG_ID, tagId);
        startActivity(intent);
    }
}
