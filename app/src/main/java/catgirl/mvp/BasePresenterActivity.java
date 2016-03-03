package catgirl.mvp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Activity that stores an instance of a presenter and a Dagger component over configuration changes.
 * When the activity is destroyed for good or due to lack of memory it should free said presenter and component.
 */
public abstract class BasePresenterActivity
        extends AppCompatActivity
        implements ComponentPresenterCache {

    private static final String NEXT_ID_KEY = "next-presenter-id";

    private NonConfigurationInstance nonConfigurationInstance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NonConfigurationInstance instance = (NonConfigurationInstance) getLastCustomNonConfigurationInstance();

        if (instance == null) {
            long seed = 0;
            if (savedInstanceState != null) {
                seed = savedInstanceState.getLong(NEXT_ID_KEY);
            }
            this.nonConfigurationInstance =
                    new NonConfigurationInstance(seed);
        } else {
            nonConfigurationInstance = instance;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(NEXT_ID_KEY, nonConfigurationInstance.nextId.get());
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return nonConfigurationInstance;
    }


    @Override
    public long generateId() {
        return nonConfigurationInstance.nextId.getAndIncrement();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Presenter<T> getPresenter(long id) {
        return (Presenter<T>) nonConfigurationInstance.objects.get(id);
    }

    @Override
    public <T> void setPresenter(long id, Presenter<T> presenter) {
        nonConfigurationInstance.objects.put(id, presenter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getComponent(long id) {
        return (C) nonConfigurationInstance.objects.get(id);
    }

    @Override
    public <C> void setComponent(long id, C component) {
        nonConfigurationInstance.objects.put(id, component);
    }

    private static class NonConfigurationInstance {
        private Map<Long, Object> objects;
        private AtomicLong nextId;

        public NonConfigurationInstance(long seed) {
            objects = new HashMap<>();
            nextId = new AtomicLong(seed);
        }
    }
}
