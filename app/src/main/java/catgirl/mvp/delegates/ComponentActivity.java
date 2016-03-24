package catgirl.mvp.delegates;

import android.app.Activity;
import android.os.Bundle;

import catgirl.oneesama.activity.main.MainActivityComponent;
import catgirl.oneesama.activity.main.MainActivityModule;
import catgirl.oneesama.application.Application;

public interface ComponentActivity<Component> {
    Component createComponent();
}
