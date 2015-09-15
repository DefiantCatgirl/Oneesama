package catgirl.oneesama.ui.activity.main.ondevice;

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
import catgirl.oneesama.model.chapter.serializable.Tag;
import catgirl.oneesama.model.chapter.ui.UiTag;
import catgirl.oneesama.ui.activity.main.ondevice.pages.DoujinsPage;
import catgirl.oneesama.ui.activity.main.ondevice.pages.MiscPage;
import catgirl.oneesama.ui.activity.main.ondevice.pages.SeriesPage;

public class OnDeviceFragment extends Fragment {

    @Bind(R.id.Fragment_OnDevice_ViewPager) ViewPager viewPager;
    @Bind(R.id.Fragment_OnDevice_TabLayout) TabLayout tabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_ondevice, container, false);
        ButterKnife.bind(this, view);

        List<Fragment> fragments = new ArrayList<>();
        List<String> names = new ArrayList<>();

        fragments.add(new SeriesPage());
        names.add(getString(R.string.fragment_ondevice_series));

        fragments.add(new DoujinsPage());
        names.add(getString(R.string.fragment_ondevice_doujins));

        fragments.add(new MiscPage());
        names.add(getString(R.string.fragment_ondevice_misc));

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public interface OnDeviceFragmentDelegate {
        void onBrowseButtonPressed();
    }

    class SeriesAuthor {
        UiTag series;
        UiTag author;
        public SeriesAuthor(UiTag series, UiTag author) {
            this.series = series;
            this.author = author;
        }
    }

    class SeriesAuthorRealm {
        Tag series;
        Tag author;
        public SeriesAuthorRealm(Tag series, Tag author) {
            this.series = series;
            this.author = author;
        }
    }
}
