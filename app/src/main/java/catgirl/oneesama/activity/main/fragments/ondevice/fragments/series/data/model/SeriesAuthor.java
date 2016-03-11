package catgirl.oneesama.activity.main.fragments.ondevice.fragments.series.data.model;

import catgirl.oneesama.data.model.chapter.ui.UiTag;

public class SeriesAuthor {
    public UiTag series;
    public UiTag author;
    public SeriesAuthor(UiTag series, UiTag author) {
        this.series = series;
        this.author = author;
    }
}
