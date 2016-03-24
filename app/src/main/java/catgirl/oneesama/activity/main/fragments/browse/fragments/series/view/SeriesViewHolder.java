package catgirl.oneesama.activity.main.fragments.browse.fragments.series.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import catgirl.oneesama.activity.main.fragments.browse.fragments.series.data.model.SeriesItem;

public class SeriesViewHolder extends RecyclerView.ViewHolder {
    private SeriesViewHolderDelegate delegate;

    @Bind(R.id.Item_Series_Title) TextView title;
    @Bind(R.id.Item_Series_Author) TextView author;

    public SeriesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        author.setVisibility(View.GONE);
        itemView.setOnClickListener(this::onClick);
    }

    private void onClick(View view) {
        if (delegate != null) {
            delegate.onClick();
        }
    }

    public void bind(SeriesItem series, SeriesViewHolderDelegate delegate) {
        this.delegate = delegate;
        title.setText(series.name);
    }

    public interface SeriesViewHolderDelegate {
        void onClick();
    }
}
