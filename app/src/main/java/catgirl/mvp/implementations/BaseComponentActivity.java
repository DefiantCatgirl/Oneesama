package catgirl.mvp.implementations;

import android.os.Bundle;

import catgirl.mvp.delegates.ComponentActivity;
import catgirl.mvp.delegates.ComponentActivityDelegate;

public abstract class BaseComponentActivity<Component>
        extends BaseCacheActivity
        implements ComponentActivity<Component> {

    ComponentActivityDelegate<Component> delegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        delegate = new ComponentActivityDelegate<>(this, this);
        delegate.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        delegate.onSaveInstanceState(outState);
    }

    protected Component getComponent() {
        return delegate.getComponent();
    }
}
