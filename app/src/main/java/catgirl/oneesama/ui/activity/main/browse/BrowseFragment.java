package catgirl.oneesama.ui.activity.main.browse;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import catgirl.oneesama.R;
import catgirl.oneesama.api.Config;

public class BrowseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v("Log", "Fragment created");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_browse, container, false);

        view.findViewById(R.id.Fragment_Browse_Button).setOnClickListener(button -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(Config.apiEndpoint));
            startActivity(i);
        });

        return view;
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
}
