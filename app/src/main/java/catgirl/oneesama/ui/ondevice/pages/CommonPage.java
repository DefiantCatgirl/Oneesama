package catgirl.oneesama.ui.ondevice.pages;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import catgirl.oneesama.model.chapter.gson.Chapter;
import catgirl.oneesama.model.chapter.gson.Tag;
import catgirl.oneesama.tools.RealmObservable;
import io.realm.Realm;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class CommonPage extends Fragment {

    @Bind(R.id.Fragment_OnDevice_SeriesRecycler) RecyclerView recycler;
    @Bind(R.id.Fragment_OnDevice_EmptyContainer) View emptyContainer;
    @Bind(R.id.Fragment_OnDevice_BrowseButton) Button browseButton;

    Realm realm;
    int lastCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_ondevice_page_series, container, false);
        ButterKnife.bind(this, view);

        realm = Realm.getInstance(getActivity());
        lastCount = realm.allObjects(Chapter.class).size();

        realm.addChangeListener(() -> {
            int count = realm.allObjects(Chapter.class).size();
            if(lastCount != count)
            {
                recycler.getAdapter().notifyDataSetChanged();
                lastCount = count;
                Log.v("Log", "Realm changed");
            }
        });

        browseButton.setOnClickListener(button -> ((OnDeviceFragmentDelegate) getActivity()).onBrowseButtonPressed());

        boolean empty = realm.allObjects(Tag.class).where().equalTo("type", "Series").count() == 0;

        emptyContainer.setVisibility(empty ? View.VISIBLE : View.GONE);
        recycler.setVisibility(empty ? View.GONE : View.VISIBLE);

        recycler.setAdapter(new RecyclerView.Adapter<SeriesViewHolder>() {

            @Override
            public SeriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new SeriesViewHolder(getActivity().getLayoutInflater().inflate(R.layout.item_series, parent, false));
            }

            @Override
            public void onBindViewHolder(SeriesViewHolder holder, int position) {
                holder.bind(position);
            }

            @Override
            public int getItemCount() {
                return getDataItemCount();
            }
        });

        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onDestroyView() {
        realm.removeAllChangeListeners();
        realm.close();
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

    public Observable<SeriesAuthorRealm> getDataSource(int id) {
        return null;
    }


    public int getDataItemCount() {
        return 0;
    }

    public interface OnDeviceFragmentDelegate {
        void onBrowseButtonPressed();
    }

    class SeriesAuthor {
        catgirl.oneesama.model.chapter.ui.Tag series;
        catgirl.oneesama.model.chapter.ui.Tag author;
        public SeriesAuthor(catgirl.oneesama.model.chapter.ui.Tag series, catgirl.oneesama.model.chapter.ui.Tag author) {
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

    class SeriesViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.Item_Series_Author) TextView author;
        @Bind(R.id.Item_Series_Title) TextView title;

        Subscription subscription;

        public SeriesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(int id) {
            author.setText("");
            title.setText("");

            if(subscription != null)
                subscription.unsubscribe();

            Executor ex = Executors.newSingleThreadExecutor();

            subscription = getDataSource(id)
                    .subscribeOn(Schedulers.from(ex))
                    .unsubscribeOn(Schedulers.from(ex))
                    .map(item -> new SeriesAuthor(
                            new catgirl.oneesama.model.chapter.ui.Tag(item.series.getId(), item.series.getType(), item.series.getName(), item.series.getPermalink()),
                            new catgirl.oneesama.model.chapter.ui.Tag(item.author.getId(), item.author.getType(), item.author.getName(), item.author.getPermalink())))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(item -> {
                        author.setText(item.author.getName());
                        title.setText(item.series.getName());
                    });
        }
    }
}
