package catgirl.oneesama.ui.activity.main;

import android.animation.Animator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import catgirl.oneesama.controller.ChaptersController;
import catgirl.oneesama.controller.legacy.Book;
import catgirl.oneesama.model.chapter.serializable.Chapter;
import catgirl.oneesama.model.chapter.serializable.Page;
import catgirl.oneesama.model.chapter.serializable.Tag;
import catgirl.oneesama.ui.activity.legacyreader.activityreader.ReaderActivity;
import catgirl.oneesama.ui.activity.legacyreader.tools.EndAnimatorListener;
import catgirl.oneesama.ui.activity.main.browse.BrowseFragment;
import catgirl.oneesama.ui.activity.main.ondevice.OnDeviceFragment;
import io.realm.Realm;
import io.realm.RealmObject;
import rx.Observable;
import rx.Subscription;


public class MainActivity extends AppCompatActivity
        implements FragmentManager.OnBackStackChangedListener, NavigationView.OnNavigationItemSelectedListener, OnDeviceFragment.OnDeviceFragmentDelegate {

    private ActionBarDrawerToggle mDrawerToggle;

    private Realm realm;

    private MenuConfig menuConfig;
    private int currentMenuItemId = 0;

    @Bind(R.id.toolbar_layout) Toolbar toolbar;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.MainActivity_NavigationView) NavigationView mNavigationView;
    @Bind(R.id.container) ViewGroup container;
    @Bind(R.id.MainActivity_LoadingLayout) View loadingLayout;
    @Bind(R.id.MainActivity_AddButton) ImageButton addButton;

    boolean loading = false;

    // TODO use a library or move this into a singleton
    static Observable<Book> chapterRequest;
    Subscription chapterSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

//        fixOrphans();

        menuConfig = new MenuConfig();

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        setUpNavigationMenu(savedInstanceState != null);

        if(savedInstanceState != null) {

            loading = savedInstanceState.getBoolean("LOADING");
            if(loading) {
                loadingLayout.setVisibility(View.VISIBLE);
                if(chapterRequest != null)
                    subscribeToChapterRequest(chapterRequest);
            }

            onBackStackChanged();
        }

        setSupportActionBar(toolbar);

        addButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.dialog_add_chapter_title));

            View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_chapter, null);
            EditText input = (EditText) dialogView.findViewById(R.id.Dialog_AddChapter_Input);

            // Paste copied link from clipboard
            ClipData data = ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).getPrimaryClip();
            if(data != null) {
                ClipData.Item item = data.getItemAt(0);
                if(item.getText().toString().contains("dynasty-scans.com/chapters/")) {
                    input.setText(item.getText());
                    input.selectAll();
                }
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
    protected void onStart() {
        super.onStart();

        if(getIntent() != null && getIntent().getData() != null)
            onNewIntent(getIntent());
    }

    @Override
    protected void onDestroy() {
        realm.close();
        if(chapterSubscription != null)
            chapterSubscription.unsubscribe();
        super.onDestroy();
    }

    @Override
    protected void onNewIntent (Intent intent) {
        if(loading)
            return;
        if(intent.getData() == null)
            return;

        openChapterByUrl(intent.getData());

        setIntent(null);
    }

    public void openChapterByUrl(Uri url) {
        if(url == null || url.getLastPathSegment() == null) {
            Toast.makeText(this, "Error adding chapter:\n" + "Invalid URL", Toast.LENGTH_LONG).show();
            return;
        }
        Chapter chapter = realm.where(Chapter.class).equalTo("permalink", url.getLastPathSegment()).findFirst();
        if(chapter != null) {
            Intent readerIntent = new Intent(this, ReaderActivity.class);
            readerIntent.putExtra(ReaderActivity.PUBLICATION_ID, chapter.getId());
            startActivity(readerIntent);
            return;
        }

        loading = true;
        loadingLayout.setVisibility(View.VISIBLE);
        loadingLayout.setAlpha(0f);
        loadingLayout.animate().alpha(1f).setListener(new EndAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                loadingLayout.setAlpha(1f);
            }
        });

        chapterRequest = ChaptersController.getInstance()
                .requestChapterController(url.getLastPathSegment())
                .cache();

        subscribeToChapterRequest(chapterRequest);
    }

    public void subscribeToChapterRequest(Observable<Book> observable) {
        chapterSubscription = observable.subscribe(response -> {
            Intent readerIntent = new Intent(this, ReaderActivity.class);
            readerIntent.putExtra(ReaderActivity.PUBLICATION_ID, response.data.getId());
            startActivity(readerIntent);
            loading = false;
            loadingLayout.setVisibility(View.GONE);
            chapterRequest = null;
            chapterSubscription = null;
        }, error -> {
            Toast.makeText(this, "Error adding chapter:\n" + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            loading = false;
            loadingLayout.setVisibility(View.GONE);
            chapterRequest = null;
            chapterSubscription = null;
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("CURRENT_ITEM", currentMenuItemId);
        outState.putBoolean("LOADING", loading);
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
                        String.format(getString(R.string.activity_main_about_text), getPackageManager().getPackageInfo(getPackageName(), 0).versionName),
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

            menuItems.add(new MenuConfigItem(MenuItemType.ITEM_ONDEVICE, getString(R.string.activity_main_ondevice), R.drawable.ic_file_download_black_24dp));
            menuItems.add(new MenuConfigItem(MenuItemType.ITEM_BROWSE, getString(R.string.activity_main_browse), R.drawable.ic_library_books_black_24dp));
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
    }
}
