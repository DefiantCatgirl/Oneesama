package catgirl.mvp.delegates;

import android.os.Bundle;

import catgirl.mvp.ComponentPresenterCache;

public class ComponentActivityDelegate<Component> {

    private static final String COMPONENT_INDEX_KEY = "activity-component-index";
    private final ComponentActivity<Component> activity;
    private final ComponentPresenterCache presenterCache;
    private long componentId;

    public ComponentActivityDelegate(ComponentActivity<Component> activity, ComponentPresenterCache presenterCache) {
        this.activity = activity;
        this.presenterCache = presenterCache;
    }

    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            componentId = presenterCache.generateId();
        } else {
            componentId = savedInstanceState.getLong(COMPONENT_INDEX_KEY);
        }

        Component component = presenterCache.getComponent(componentId);

        if (component == null) {
            component = activity.createComponent();
            presenterCache.setComponent(componentId, component);
        }
    }

    public Component getComponent() {
        return presenterCache.getComponent(componentId);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(COMPONENT_INDEX_KEY, componentId);
    }
}
