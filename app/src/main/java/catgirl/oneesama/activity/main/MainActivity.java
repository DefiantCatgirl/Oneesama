package catgirl.oneesama.activity.main;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.yandex.metrica.YandexMetrica;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.mvp.implementations.BaseComponentActivity;
import catgirl.oneesama.R;
import catgirl.oneesama.activity.common.activity.ChapterLoaderActivity;
import catgirl.oneesama.activity.common.activity.ChapterLoaderActivityDelegate;
import catgirl.oneesama.activity.main.fragments.browse.BrowseFragment;
import catgirl.oneesama.activity.main.fragments.history.view.HistoryFragment;
import catgirl.oneesama.activity.main.fragments.ondevice.OnDeviceFragment;
import catgirl.oneesama.application.Application;
import catgirl.oneesama.application.Config;
import catgirl.oneesama.data.controller.ChaptersController;
import catgirl.oneesama.data.model.chapter.serializable.Chapter;
import catgirl.oneesama.data.model.chapter.serializable.Page;
import catgirl.oneesama.data.model.chapter.serializable.Tag;
import catgirl.oneesama.data.realm.RealmProvider;
import io.realm.Realm;
import io.realm.RealmObject;


public class MainActivity extends BaseComponentActivity<MainActivityComponent>
        implements
        FragmentManager.OnBackStackChangedListener,
        NavigationView.OnNavigationItemSelectedListener,
        OnDeviceFragment.OnDeviceFragmentDelegate,
        ChapterLoaderActivity
{

    private static final String CURRENT_ITEM_KEY = "current-item";

    @Inject RealmProvider realmProvider;
    @Inject ChaptersController chaptersController;

    private ActionBarDrawerToggle mDrawerToggle;


    private MenuConfig menuConfig;
    private int currentMenuItemId = 0;

    @Bind(R.id.toolbar_layout) Toolbar toolbar;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.MainActivity_NavigationView) NavigationView mNavigationView;
    @Bind(R.id.container) ViewGroup container;
    @Bind(R.id.MainActivity_AddButton) ImageButton addButton;

    ChapterLoaderActivityDelegate chapterLoaderDelegate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        getComponent().inject(this);

//        fixOrphans();

        chapterLoaderDelegate = new ChapterLoaderActivityDelegate(
                this, findViewById(android.R.id.content),
                realmProvider.provideRealm(),
                chaptersController);

        menuConfig = new MenuConfig();

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        setUpNavigationMenu(savedInstanceState != null);

        chapterLoaderDelegate.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            onBackStackChanged();
        }

        setSupportActionBar(toolbar);

        addButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.dialog_add_chapter_title));

            View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_chapter, null);
            EditText input = (EditText) dialogView.findViewById(R.id.Dialog_AddChapter_Input);

            // Paste copied link from clipboard
            CharSequence text = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                ClipData data = ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).getPrimaryClip();
                if (data != null) {
                    ClipData.Item item = data.getItemAt(0);
                    text = item.getText();
                }
            } else {
                text = ((android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).getText();
            }

            if (text != null && text.toString().contains("dynasty-scans.com/chapters/")) {
                input.setText(text);
                input.selectAll();
            }

            builder.setView(dialogView);

            builder.setPositiveButton("OK", (dialog, which) -> {
                openChapterByUrl(Uri.parse(input.getText().toString()));
                dialog.cancel();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.cancel();
            });

            AlertDialog dialog = builder.create();
            input.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                    return true;
                }
                return false;
            });
            dialog.setOnShowListener(activeDialog -> {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
            });
            dialog.show();
        });
    }

    @Override
    public MainActivityComponent createComponent() {
        return Application.getApplicationComponent().plus(new MainActivityModule());
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(getIntent() != null && getIntent().getData() != null)
            onNewIntent(getIntent());
    }

    @Override
    protected void onDestroy() {
        chapterLoaderDelegate.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onNewIntent (Intent intent) {
        super.onNewIntent(intent);

        if(chapterLoaderDelegate.isLoading())
            return;
        if(intent.getData() == null)
            return;

        Uri url = intent.getData();

        // Dynasty "Recent chapters" URL is "/chapters/added" so "/chapters/<chapter_permalink>"
        // intent filter has no choice but to grab it and, normally, fail miserably.
        // But there's a "Recent chapters" page in the app itself, so we can redirect there!
        if(url != null) {
            String last = url.getLastPathSegment();
            if (last != null && (last.equals("added")
                    || last.startsWith("added?")
                    || last.startsWith("added."))) {
                onBrowseButtonPressed();
                setIntent(null);
                return;
            }
        }

        openChapterByUrl(intent.getData());

        setIntent(null);
    }

    public void openChapterByUrl(Uri url) {
        if(url == null || url.getLastPathSegment() == null) {
            Toast.makeText(this, "Error adding chapter:\n" + "Invalid URL", Toast.LENGTH_LONG).show();
            return;
        }
        openChapterByPermalink(url.getLastPathSegment());
    }

    @Override
    public void openChapterByPermalink(String permalink) {
        chapterLoaderDelegate.openChapterByPermalink(permalink);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_ITEM_KEY, currentMenuItemId);
        chapterLoaderDelegate.onSaveInstanceState(outState);
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

        int groupId = 0;

        for(int i = 0; i < menuConfig.menuItems.size(); i++) {
            MenuConfigItem item = menuConfig.menuItems.get(i);
            if(item.type == MenuItemType.ITEM_SEPARATOR) {
                groupId++;
                continue;
            }
            MenuItem menuItem = menu.add(groupId, i, i, item.name);
            menuItem.setIcon(item.icon);
        }

        menu.setGroupCheckable(0, true, true);

        if(!fromSavedInstance)
            switchFragment(currentMenuItemId);
    }

    @Override
    public void onBackStackChanged() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return;
        }

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
                        String.format(getString(R.string.activity_main_about_text), getPackageManager().getPackageInfo(getPackageName(), 0).versionName),
                        Toast.LENGTH_LONG
                ).show();
                updateUiForFragment(currentMenuItemId);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return;
        }

        if(item.type == MenuItemType.ITEM_DYNASTY) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(Config.apiEndpoint));
            startActivity(i);
            updateUiForFragment(currentMenuItemId);
            return;
        }

        if(item.type == MenuItemType.ITEM_WEBSITE) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(getString(R.string.oneesama_website)));
            startActivity(i);
            updateUiForFragment(currentMenuItemId);
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
            case ITEM_HISTORY:
                fragmentClass = HistoryFragment.class;
                break;
            default:
                break;
        }

        if(fragmentClass != null)
            try {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(id));

                if (fragment == null) {
                    fragment = (Fragment) fragmentClass.newInstance();
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.container,
                                fragment,
                                String.valueOf(id))
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
        if (menuConfig.getId(MenuItemType.ITEM_BROWSE) != currentMenuItemId) {
            selectMenuItem(MenuItemType.ITEM_BROWSE);
        }
    }

    public enum MenuItemType {
        ITEM_BROWSE,
        ITEM_ONDEVICE,
        ITEM_ABOUT,
        ITEM_DYNASTY,
        ITEM_WEBSITE,
        ITEM_HISTORY,
        ITEM_SEPARATOR
    }

    public static class MenuConfigItem {
        MenuItemType type;
        String name;
        int icon;

        public MenuConfigItem(MenuItemType type, String name, int icon) {
            this.type = type;
            this.name = name;
            this.icon = icon;
        }

        public static MenuConfigItem getSeparator() {
            return new MenuConfigItem(MenuItemType.ITEM_SEPARATOR, null, 0);
        }
    }

    class MenuConfig {
        List<MenuConfigItem> menuItems;

        public MenuConfig() {
            menuItems = new ArrayList<>();

            menuItems.add(new MenuConfigItem(MenuItemType.ITEM_ONDEVICE, getString(R.string.activity_main_ondevice), R.drawable.ic_file_download_black_24dp));
            menuItems.add(new MenuConfigItem(MenuItemType.ITEM_HISTORY, getString(R.string.activity_main_history), R.drawable.ic_history_black_24dp));
            menuItems.add(new MenuConfigItem(MenuItemType.ITEM_BROWSE, getString(R.string.activity_main_browse), R.drawable.ic_library_books_black_24dp));
            menuItems.add(MenuConfigItem.getSeparator());
            menuItems.add(new MenuConfigItem(MenuItemType.ITEM_DYNASTY, getString(R.string.activity_main_dynasty), R.drawable.ic_web_black_24dp));
            menuItems.add(new MenuConfigItem(MenuItemType.ITEM_WEBSITE, getString(R.string.activity_main_website), R.drawable.ic_github_circle_black_24dp));
            menuItems.add(new MenuConfigItem(MenuItemType.ITEM_ABOUT, getString(R.string.activity_main_about), R.drawable.ic_help_black_24dp));
        }

        public int getId(MenuItemType type) {
            int id = -1;
            for(int i = 0; i < menuItems.size(); i++)
                if(menuItems.get(i).type == type)
                    return i;
            return id;
        }
    }

    // Remove orphaned tags and pages fix for development
    private void fixOrphans() {
        Realm realm = realmProvider.provideRealm();
        realm.beginTransaction();

        List<RealmObject> toRemove = new ArrayList<>();

        for(Tag tag : realm.allObjects(Tag.class)) {
            if(realm.where(Chapter.class).equalTo("tags.id", tag.getId()).count() == 0)
                toRemove.add(tag);
        }
        for(Page page : realm.allObjects(Page.class)) {
            if(realm.where(Chapter.class).equalTo("pages.url", page.getUrl()).count() == 0)
                toRemove.add(page);
        }
        for(RealmObject object : toRemove)
            object.removeFromRealm();

        realm.commitTransaction();
        realm.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        YandexMetrica.onResumeActivity(this);
    }

    @Override
    protected void onPause() {
        YandexMetrica.onPauseActivity(this);
        super.onPause();
    }
}
