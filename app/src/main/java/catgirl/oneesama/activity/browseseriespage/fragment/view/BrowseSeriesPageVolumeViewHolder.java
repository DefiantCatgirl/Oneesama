package catgirl.oneesama.activity.browseseriespage.fragment.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import catgirl.oneesama.activity.browseseriespage.fragment.data.model.BrowseSeriesPageVolume;

public class BrowseSeriesPageVolumeViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.VolumeHeader) TextView volumeHeader;

    public BrowseSeriesPageVolumeViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(BrowseSeriesPageVolume volume) {
        volumeHeader.setText(volume.header);
    }
}
