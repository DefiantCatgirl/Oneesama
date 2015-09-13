package catgirl.oneesama.ui.ondevice;

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
import io.realm.RealmChangeListener;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.observers.Subscribers;
import rx.schedulers.Schedulers;

public class OnDeviceFragment extends Fragment {

    @Bind(R.id.Fragment_OnDevice_Recycler) RecyclerView recycler;
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
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_ondevice, container, false);
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
                return (int) realm.allObjects(Tag.class).where().equalTo("type", "Series").count();
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

            subscription = RealmObservable.object(getActivity(), realm1 -> {
                Tag series = realm1.allObjects(Tag.class)
                        .where()
                        .equalTo("type", "Series")
                        .findAllSorted("name")
                        .get(id);
                Tag author = realm1.allObjects(Chapter.class)
                        .where()
                        .equalTo("tags.id", series.getId())
                        .findFirst()
                        .getTags()
                        .where()
                        .equalTo("type", "Author")
                        .findFirst();
                return new SeriesAuthorRealm(series, author);
            })
                .subscribeOn(Schedulers.from(ex))
                .unsubscribeOn(Schedulers.from(ex))
                .map(tag -> new SeriesAuthor(
                        new catgirl.oneesama.model.chapter.ui.Tag(tag.series.getId(), tag.series.getType(), tag.series.getName(), tag.series.getPermalink()),
                        new catgirl.oneesama.model.chapter.ui.Tag(tag.author.getId(), tag.author.getType(), tag.author.getName(), tag.author.getPermalink())))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tag -> {
                    author.setText(tag.author.getName());
                    title.setText(tag.series.getName());
                });
        }
    }
}
