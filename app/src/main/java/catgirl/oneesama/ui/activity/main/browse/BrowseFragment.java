package catgirl.oneesama.ui.activity.main.browse;

import android.support.v4.app.Fragment;

import java.util.List;

import catgirl.oneesama.R;
import catgirl.oneesama.ui.activity.main.browse.pages.CommonBrowsePage;
import catgirl.oneesama.ui.activity.main.browse.pages.RecentChaptersPage;
import catgirl.oneesama.ui.activity.main.ondevice.OnDeviceFragment;

public class BrowseFragment extends OnDeviceFragment {

    @Override
    public void getFragmentList(List<Fragment> fragments, List<String> names) {
        fragments.add(new RecentChaptersPage());
        names.add(getString(R.string.fragment_browse_recent));
    }

}
