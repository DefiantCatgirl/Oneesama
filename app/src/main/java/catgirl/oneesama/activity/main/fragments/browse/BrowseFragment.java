package catgirl.oneesama.activity.main.fragments.browse;

import android.support.v4.app.Fragment;

import java.util.List;

import catgirl.oneesama.R;
import catgirl.oneesama.activity.main.fragments.browse.fragments.recent.view.RecentFragment;
import catgirl.oneesama.activity.main.fragments.ondevice.OnDeviceFragment;

public class BrowseFragment extends OnDeviceFragment {

    @Override
    public void getFragmentList(List<Fragment> fragments, List<String> names) {
        fragments.add(new RecentFragment());
        names.add(getString(R.string.fragment_browse_recent));
    }

}
