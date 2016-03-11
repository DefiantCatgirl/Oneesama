package catgirl.oneesama.activity.main.fragments.ondevice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.doujins.view.DoujinsFragment;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.misc.view.MiscChaptersFragment;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.series.view.SeriesFragment;

public class OnDeviceFragment extends Fragment {

    @Bind(R.id.Fragment_OnDevice_ViewPager) ViewPager viewPager;
    @Bind(R.id.Fragment_OnDevice_TabLayout) TabLayout tabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void getFragmentList(List<Fragment> fragments, List<String> names) {
        fragments.add(new SeriesFragment());
        names.add(getString(R.string.fragment_ondevice_series));

        fragments.add(new DoujinsFragment());
        names.add(getString(R.string.fragment_ondevice_doujins));

        fragments.add(new MiscChaptersFragment());
        names.add(getString(R.string.fragment_ondevice_misc));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_ondevice, container, false);
        ButterKnife.bind(this, view);

        List<Fragment> fragments = new ArrayList<>();
        List<String> names = new ArrayList<>();

        getFragmentList(fragments, names);

        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return names.get(position);
            }
        });

        // Hack for flickering TabLayout titles bug https://code.google.com/p/android/issues/detail?id=180454
        // until it's fixed in Support Design Library
        // TODO - test upon Support Design Library update and remove
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout) {
            int mScrollState;
            int mScrollPosition;
            float mScrollOffset;

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                mScrollState = state;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                mScrollPosition = position;
                mScrollOffset = positionOffset;
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (mScrollState != ViewPager.SCROLL_STATE_IDLE) {
                    tabLayout.setScrollPosition(mScrollPosition, mScrollOffset, true);
                }
            }
        });

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }

    public interface OnDeviceFragmentDelegate {
        void onBrowseButtonPressed();
    }
}
