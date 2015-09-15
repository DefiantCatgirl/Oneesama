package catgirl.oneesama.ui.activity.chapters;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import catgirl.oneesama.model.chapter.serializable.Tag;
import io.realm.Realm;

public class ChaptersActivity extends AppCompatActivity {

    @Bind(R.id.toolbar_layout) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters);

        ButterKnife.bind(this);

        Realm realm = Realm.getInstance(this);
        Tag tag = realm.where(Tag.class).equalTo("id", getIntent().getIntExtra("TAG_ID", 0)).findFirst();
        String name = tag.getName();
        toolbar.setTitle(name);
        realm.close();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        if(!getIntent().getExtras().containsKey("TAG_ID")) {
            finish();
            return;
        }

        if(savedInstanceState == null) {
            ChaptersFragment fragment = new ChaptersFragment();
            Bundle bundle = new Bundle(getIntent().getExtras());
            bundle.putString("TAG_NAME", name);
            bundle.putString("TAG_TYPE", tag.getType());
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }
    }

}
