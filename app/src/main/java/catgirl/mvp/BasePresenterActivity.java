package catgirl.mvp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public abstract class BasePresenterActivity
        extends AppCompatActivity
        implements PresenterCache {

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
    public <T> Presenter<T> getPresenter(Long id) {
        return (Presenter<T>) nonConfigurationInstance.components.get(id);
    }

    @Override
    public <T> void setPresenter(Long id, Presenter<T> presenter) {
        nonConfigurationInstance.components.put(id, presenter);
    }

    private static class NonConfigurationInstance {
        private Map<Long, Object> components;
        private AtomicLong nextId;

        public NonConfigurationInstance(long seed) {
            components = new HashMap<>();
            nextId = new AtomicLong(seed);
        }
    }
}
