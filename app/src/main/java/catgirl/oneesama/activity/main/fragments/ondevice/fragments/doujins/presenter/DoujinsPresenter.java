package catgirl.oneesama.activity.main.fragments.ondevice.fragments.doujins.presenter;

import catgirl.oneesama.activity.common.presenter.AutoRefreshableRecyclerPresenter;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.doujins.data.DoujinsProvider;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.doujins.view.DoujinsView;
import catgirl.oneesama.data.model.chapter.ui.UiTag;

public class DoujinsPresenter extends AutoRefreshableRecyclerPresenter<UiTag, DoujinsView, DoujinsProvider> {

    private DoujinsProvider doujinsProvider;

    public DoujinsPresenter(DoujinsProvider doujinsProvider) {
        this.doujinsProvider = doujinsProvider;
    }

    @Override
    public void onDestroy() {
        doujinsProvider.onDestroy();
        super.onDestroy();
    }

    @Override
    public DoujinsProvider getProvider() {
        return doujinsProvider;
    }

    public void onItemClicked(int position) {
        if (getView() != null)
            getView().switchToChapterList(items.get(position).getId());
    }
}
