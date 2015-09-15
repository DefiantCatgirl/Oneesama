package catgirl.oneesama.ui.common.chapter;

import android.view.View;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import butterknife.Bind;
import butterknife.ButterKnife;
import catgirl.oneesama.R;
import catgirl.oneesama.ui.common.CommonViewHolder;

public class ChapterViewHolder extends CommonViewHolder {

    @Bind(R.id.Item_Chapter_Title)
    protected TextView title;

    @Bind(R.id.Item_Chapter_StatusLayout)
    View statusLayout;
    @Bind(R.id.Item_Chapter_ProgressLayout) View progressLayout;
    @Bind(R.id.Item_Chapter_DownloadedLayout) View downloadedLayout;
    @Bind(R.id.Item_Chapter_ReloadLayout) View reloadLayout;
    @Bind(R.id.Item_Chapter_ProgressBar) ProgressWheel progressBar;

    public ChapterViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(int id, ChapterAuthor data) {
        title.setText(data.chapter.getTitle());

        statusLayout.setVisibility(View.VISIBLE);
        progressLayout.setVisibility(View.GONE);

        boolean isDownloaded = data.chapter.isCompletelyDownloaded();

        downloadedLayout.setVisibility(isDownloaded ? View.VISIBLE : View.GONE);
        reloadLayout.setVisibility(isDownloaded ? View.GONE : View.VISIBLE);
    }

    public void reset() {
        title.setText("");

        statusLayout.setVisibility(View.GONE);
    }
}