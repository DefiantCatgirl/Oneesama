package catgirl.oneesama.activity.main.fragments.ondevice.fragments.series.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import catgirl.oneesama.activity.main.fragments.ondevice.fragments.series.data.model.SeriesAuthor;

public class SeriesViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.Item_Series_Author) TextView author;
    @Bind(R.id.Item_Series_Title) TextView title;

    private SeriesViewHolderDelegate delegate;

    public SeriesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this::onClick);
    }

    public void bind(SeriesAuthor data, SeriesViewHolderDelegate delegate) {
        this.delegate = delegate;

        if(data.author != null) {
            author.setText(data.author.getName());
        } else {
            author.setText(null);
        }

        title.setText(data.series.getName());
    }

    public void onClick(View view) {
        if (delegate != null) {
            delegate.onClick();
        }
    }

    public interface SeriesViewHolderDelegate {
        void onClick();
    }
}
