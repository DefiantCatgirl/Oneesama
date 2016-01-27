package catgirl.mvp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

public abstract class BasePresenterFragment<P extends Presenter>
        extends Fragment {

    private static final String PRESENTER_INDEX_KEY = "presenter-index";

    private PresenterCache presenterCache;
    private P presenter;
    private long presenterId;
    private boolean isDestroyedBySystem;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof PresenterCache) {
            presenterCache = (PresenterCache) context;
        } else {
            throw new RuntimeException(getClass().getSimpleName() +
                    " must be attached to a context that implements " +
                    PresenterCache.class.getSimpleName());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            presenterId = presenterCache.generateId();
        } else {
            presenterId = savedInstanceState
                    .getLong(PRESENTER_INDEX_KEY);
        }

        presenter = (P) presenterCache.getPresenter(presenterId);

        if (presenter == null) {
            presenter = getPresenterFactory().createPresenter();
            presenterCache.setPresenter(presenterId, presenter);
        }
    }

    @SuppressWarnings("unchecked")
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            presenter.bindView(view);
        } catch (ClassCastException e) {
            throw new RuntimeException("The view provided does not"
                    + " implement the view interface expected by "
                    + presenter.getClass().getSimpleName(), e);
        }
    }

    @Override
    public void onResume() {
        isDestroyedBySystem = false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isDestroyedBySystem = true;
        outState.putLong(PRESENTER_INDEX_KEY, presenterId);
        presenter.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        if (!isDestroyedBySystem) {
            presenter.onDestroy();
            presenterCache.setPresenter(presenterId, null);
        }
    }

    @Override
    public void onDestroyView() {
        presenter.unbindView();
    }

    public P getPresenter() {
        return presenter;
    }

    protected abstract PresenterFactory<P> getPresenterFactory();

}