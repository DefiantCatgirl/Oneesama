package catgirl.oneesama.ui.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import rx.Subscription;

public class CommonViewHolder extends RecyclerView.ViewHolder {
    Subscription subscription;

    public CommonViewHolder(View itemView) {
        super(itemView);
    }
}
