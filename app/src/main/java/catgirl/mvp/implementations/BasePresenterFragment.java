package catgirl.mvp.implementations;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import catgirl.mvp.ComponentPresenterCache;
import catgirl.mvp.Presenter;

public abstract class BasePresenterFragment<P extends Presenter, C>
        extends Fragment {

    private static final String PRESENTER_INDEX_KEY = "presenter-index";
    private static final String COMPONENT_INDEX_KEY = "component-index";

    private ComponentPresenterCache componentPresenterCache;
    private P presenter;
    private long presenterId;
    private C component;
    private long componentId;
    private boolean isDestroyedBySystem;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ComponentPresenterCache) {
            componentPresenterCache = (ComponentPresenterCache) context;
        } else {
            throw new RuntimeException(getClass().getSimpleName() +
                    " must be attached to a context that implements " +
                    ComponentPresenterCache.class.getSimpleName());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            presenterId = componentPresenterCache.generateId();
            componentId = componentPresenterCache.generateId();
        } else {
            presenterId = savedInstanceState.getLong(PRESENTER_INDEX_KEY);
            componentId = savedInstanceState.getLong(COMPONENT_INDEX_KEY);
        }

        // Important: component must go first since it is used to create the presenter.
        // Presenter is created through a factory so the code here doesn't reflect this.

        component = (C) componentPresenterCache.getComponent(componentId);

        if (component == null) {
            component = createComponent();
            componentPresenterCache.setComponent(componentId, component);
        }

        onComponentCreated();

        presenter = (P) componentPresenterCache.getPresenter(presenterId);

        if (presenter == null) {
            presenter = createPresenter();
            presenter.onCreate(savedInstanceState);
            componentPresenterCache.setPresenter(presenterId, presenter);
        }
    }

    @SuppressWarnings("unchecked")
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            presenter.bindView(this);
        } catch (ClassCastException e) {
            throw new RuntimeException("The view provided does not"
                    + " implement the view interface expected by "
                    + presenter.getClass().getSimpleName(), e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isDestroyedBySystem = false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isDestroyedBySystem = true;
        outState.putLong(PRESENTER_INDEX_KEY, presenterId);
        outState.putLong(COMPONENT_INDEX_KEY, componentId);
        presenter.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        if (!isDestroyedBySystem) {
            presenter.onDestroy();
            componentPresenterCache.setPresenter(presenterId, null);
            componentPresenterCache.setComponent(componentId, null);
        }
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        presenter.unbindView();
        super.onDestroyView();
    }

    public C getComponent() {
        return component;
    }

    protected abstract C createComponent();

    protected abstract void onComponentCreated();

    public P getPresenter() {
        return presenter;
    }

    protected abstract P createPresenter();

}
