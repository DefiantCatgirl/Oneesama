package catgirl.oneesama.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import catgirl.oneesama.api.Config;
import catgirl.oneesama.api.DynastyService;
import catgirl.oneesama.model.chapter.gson.Chapter;
import catgirl.oneesama.ui.browse.BrowseFragment;
import catgirl.oneesama.ui.ondevice.OnDeviceFragment;
import io.realm.Realm;
import io.realm.RealmObject;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity
        implements FragmentManager.OnBackStackChangedListener, NavigationView.OnNavigationItemSelectedListener, OnDeviceFragment.OnDeviceFragmentDelegate {

    private final Handler mDrawerActionHandler = new Handler();
    private ActionBarDrawerToggle mDrawerToggle;

    private Realm realm;

    private MenuConfig menuConfig = new MenuConfig();
    private int currentMenuItemId = 0;

    @Bind(R.id.toolbar_layout) Toolbar toolbar;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.MainActivity_NavigationView) NavigationView mNavigationView;
    @Bind(R.id.container) ViewGroup container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

//        if(savedInstanceState != null) {
//            currentMenuItemId = savedInstanceState.getInt("CURRENT_ITEM", 0);
//        }

        realm = Realm.getInstance(this);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        setUpNavigationMenu(savedInstanceState != null);

        if(savedInstanceState != null) {
            onBackStackChanged();
        }

        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(getIntent() != null && getIntent().getData() != null)
            onNewIntent(getIntent());
    }

    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }

    @Override
    protected void onNewIntent (Intent intent) {
        if(intent.getData() == null)
            return;

        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.apiEndpoint)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        DynastyService service = retrofit.create(DynastyService.class);

        service.getChapter(intent.getData().getLastPathSegment())
                .subscribeOn(Schedulers.io())
                .doOnNext(response -> {
                    Realm realm = Realm.getInstance(this);
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(response);
                    realm.commitTransaction();
                    realm.close();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    // TODO remove debug
                    Log.v("Log", response.getPermalink());
                    for (Chapter chapter : realm.allObjects(Chapter.class)) {
                        Log.v("Log1", "Chapter: " + chapter.getTitle());
                        Log.v("Log1", "Page count: " + chapter.getPages().size());
                    }
                });

        setIntent(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("CURRENT_ITEM", currentMenuItemId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(mNavigationView))
            mDrawerLayout.closeDrawers();
        else if(currentMenuItemId != 0)
            super.onBackPressed();
        else
            finish();
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item);
    }

    private void setUpNavigationMenu(boolean fromSavedInstance) {
        mNavigationView.setNavigationItemSelectedListener(this);

        final Menu menu = mNavigationView.getMenu();

        for(int i = 0; i < menuConfig.menuItems.size(); i++) {
            MenuConfigItem item = menuConfig.menuItems.get(i);
            MenuItem menuItem = menu.add(0, i, i, item.name);
            menuItem.setIcon(item.icon);
        }

        menu.setGroupCheckable(0, true, true);

        if(!fromSavedInstance)
            switchFragment(currentMenuItemId);
    }

    @Override
    public void onBackStackChanged() {
        updateUiForFragment(
                Integer.parseInt(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName())
        );
    }

    public void updateUiForFragment(int menuItemId) {
        MenuConfigItem item = menuConfig.menuItems.get(menuItemId);
        toolbar.setTitle(item.name);
        mNavigationView.getMenu().getItem(menuItemId).setChecked(true);
        currentMenuItemId = menuItemId;
    }

    public void switchFragment(int id) {
        MenuConfigItem item = menuConfig.menuItems.get(id);

        if(item.type == MenuItemType.ITEM_ABOUT) {
            try {
                Toast.makeText(
                        this,
                        "Oneesama v." + getPackageManager().getPackageInfo(getPackageName(), 0).versionName + ", a Dynasty Reader client.\nMore to come.",
                        Toast.LENGTH_LONG
                ).show();
                updateUiForFragment(currentMenuItemId);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return;
        }

        updateUiForFragment(id);

        Class fragmentClass = null;

        switch(item.type) {
            case ITEM_BROWSE:
                fragmentClass = BrowseFragment.class;
                break;
            case ITEM_ONDEVICE:
                fragmentClass = OnDeviceFragment.class;
                break;
            default:
                break;
        }

        if(fragmentClass != null)
            try {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.container,
                                ((Fragment) fragmentClass.newInstance()))
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(String.valueOf(id))
                        .commit();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if(currentMenuItemId == menuItem.getItemId())
            return true;

        mDrawerLayout.closeDrawers();

        switchFragment(menuItem.getItemId());

        return true;
    }

    public void selectMenuItem(MenuItemType type) {
        switchFragment(menuConfig.getId(type));
    }

    @Override
    public void onBrowseButtonPressed() {
        selectMenuItem(MenuItemType.ITEM_BROWSE);
    }

    public enum MenuItemType {
        ITEM_BROWSE,
        ITEM_ONDEVICE,
        ITEM_ABOUT
    }

    public class MenuConfigItem {
        MenuItemType type;
        String name;
        int icon;

        public MenuConfigItem(MenuItemType type, String name, int icon) {
            this.type = type;
            this.name = name;
            this.icon = icon;
        }
    }

    class MenuConfig {
        List<MenuConfigItem> menuItems;

        public MenuConfig() {
            menuItems = new ArrayList<>();


            menuItems.add(new MenuConfigItem(MenuItemType.ITEM_BROWSE, "Browse", R.drawable.ic_library_books_black_24dp));
            menuItems.add(new MenuConfigItem(MenuItemType.ITEM_ABOUT, "About", R.drawable.ic_help_black_24dp));
        }

        public int getId(MenuItemType type) {
            int id = -1;
            for(int i = 0; i < menuItems.size(); i++)
                if(menuItems.get(i).type == type)
                    return i;
            return id;
        }
    }

}
